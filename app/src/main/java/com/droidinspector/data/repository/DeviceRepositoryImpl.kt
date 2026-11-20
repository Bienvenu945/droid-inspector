package com.droidinspector.data.repository

import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.DocumentsContract
import android.view.Display
import com.droidinspector.core.utils.FormatUtils
import com.droidinspector.domain.model.AppPackageInfo
import com.droidinspector.domain.model.BatteryInfo
import com.droidinspector.domain.model.CpuCoreFrequency
import com.droidinspector.domain.model.DeviceSummary
import com.droidinspector.domain.model.HardwareInfo
import com.droidinspector.domain.model.SafFileItem
import com.droidinspector.domain.model.SensorInfo
import com.droidinspector.domain.model.StorageInfo
import com.droidinspector.domain.model.SystemSecurityInfo
import com.droidinspector.domain.repository.DeviceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DeviceRepository {

    override fun getDeviceSummary(): DeviceSummary {
        val manufacturer = (Build.MANUFACTURER ?: "Inconnu").replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }
        return DeviceSummary(
            deviceName = "$manufacturer ${Build.MODEL}",
            model = Build.MODEL ?: "Inconnu",
            manufacturer = manufacturer,
            brand = Build.BRAND ?: "Inconnu",
            product = Build.PRODUCT ?: "Inconnu",
            hardware = Build.HARDWARE ?: "Inconnu",
            androidVersion = Build.VERSION.RELEASE ?: "Inconnu",
            apiLevel = Build.VERSION.SDK_INT,
            securityPatch = Build.VERSION.SECURITY_PATCH ?: "N/D",
            buildNumber = Build.DISPLAY ?: "N/D",
            buildTimeMillis = Build.TIME,
            board = Build.BOARD ?: "N/D",
            bootloader = Build.BOOTLOADER ?: "N/D",
            fingerprint = Build.FINGERPRINT ?: "N/D",
            kernelVersion = readKernelVersion(),
            javaVmVersion = System.getProperty("java.vm.version") ?: "ART"
        )
    }

    override fun getSystemSecurityInfo(): SystemSecurityInfo {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
        val encryptionStatus = when (dpm?.storageEncryptionStatus) {
            DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE -> "Chiffrement actif"
            DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_PER_USER -> "Chiffrement par utilisateur (FBE)"
            DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_DEFAULT_KEY -> "Actif (clé par défaut)"
            DevicePolicyManager.ENCRYPTION_STATUS_INACTIVE -> "Inactif"
            else -> "Non disponible"
        }

        val seLinuxMode = try {
            val enforceFile = File("/sys/fs/selinux/enforce")
            if (enforceFile.exists() && enforceFile.canRead()) {
                if (enforceFile.readText().trim() == "1") "Enforcing (Strict)" else "Permissive"
            } else {
                "Enforcing (Restreint par l'OS)"
            }
        } catch (_: Exception) {
            "Protégé par l'OS"
        }

        val rootPaths = listOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/su/bin/su",
            "/data/local/xbin/su"
        )
        val rootDetected = rootPaths.any { File(it).exists() }
        val isDebugBuild = (Build.TAGS ?: "").contains("test-keys")

        return SystemSecurityInfo(
            encryptionStatus = encryptionStatus,
            seLinuxMode = seLinuxMode,
            rootDetected = rootDetected,
            isDebugBuild = isDebugBuild
        )
    }

    override fun getCurrentBatterySnapshot(): BatteryInfo {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return parseBatteryIntent(intent)
    }

    override fun getBatteryInfo(): Flow<BatteryInfo> = callbackFlow {
        // Emit immediate snapshot first
        trySend(getCurrentBatterySnapshot())

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent != null) {
                    trySend(parseBatteryIntent(intent))
                }
            }
        }

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, filter)

        awaitClose {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: IllegalArgumentException) {
            }
        }
    }

    private fun parseBatteryIntent(intent: Intent?): BatteryInfo {
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val percentage = if (level >= 0 && scale > 0) (level * 100) / scale else 0

        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL

        val statusLabel = when {
            status == BatteryManager.BATTERY_STATUS_FULL -> "Pleine"
            status == BatteryManager.BATTERY_STATUS_CHARGING -> "En charge"
            status == BatteryManager.BATTERY_STATUS_DISCHARGING -> "En décharge"
            status == BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Non branché"
            else -> "Sur batterie"
        }

        val chargePlug = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        val source = when (chargePlug) {
            BatteryManager.BATTERY_PLUGGED_AC -> "Secteur (AC)"
            BatteryManager.BATTERY_PLUGGED_USB -> "Port USB"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Sans fil"
            else -> if (isCharging) "Alimentation externe" else "Batterie"
        }

        val healthVal = intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
            ?: BatteryManager.BATTERY_HEALTH_UNKNOWN
        val health = when (healthVal) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Bonne santé"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Surchauffe"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Défectueuse"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Surtension"
            BatteryManager.BATTERY_HEALTH_COLD -> "Température basse"
            else -> "Normale"
        }

        val technology = intent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)?.takeIf { it.isNotBlank() } ?: "Li-ion"
        val tempTenths = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        val voltage = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0

        val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
        val currentNow = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
            ?.takeIf { it != Int.MIN_VALUE && it != 0 }
        val chargeCounter = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
            ?.takeIf { it != Int.MIN_VALUE && it > 0 }

        return BatteryInfo(
            percentage = percentage,
            isCharging = isCharging,
            statusLabel = statusLabel,
            chargingSource = source,
            health = health,
            technology = technology,
            temperatureCelsius = tempTenths / 10f,
            voltageMilliVolts = voltage,
            currentMicroAmps = currentNow,
            chargeCounterMicroAh = chargeCounter
        )
    }

    override suspend fun getHardwareInfo(): HardwareInfo = withContext(Dispatchers.IO) {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)

        val displayMetrics = context.resources.displayMetrics
        val coreCount = Runtime.getRuntime().availableProcessors()

        val coreFrequencies = (0 until coreCount).map { core ->
            CpuCoreFrequency(
                coreIndex = core,
                currentKhz = readSysLong("/sys/devices/system/cpu/cpu$core/cpufreq/scaling_cur_freq"),
                minKhz = readSysLong("/sys/devices/system/cpu/cpu$core/cpufreq/cpuinfo_min_freq"),
                maxKhz = readSysLong("/sys/devices/system/cpu/cpu$core/cpufreq/cpuinfo_max_freq")
            )
        }

        // An application Context is not tied to a display: Context.display throws on Android 11+.
        // DisplayManager works from any Context.
        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val defaultDisplay: Display? = displayManager.getDisplay(Display.DEFAULT_DISPLAY)

        val refreshRate = defaultDisplay?.refreshRate ?: 60f
        val hdrSupported = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            (defaultDisplay?.hdrCapabilities?.supportedHdrTypes?.isNotEmpty()) == true
        } else {
            false
        }

        val glEsVersion = actManager.deviceConfigurationInfo.glEsVersion ?: "3.0"
        val pm = context.packageManager
        val vulkanSupported = pm.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_VERSION)

        val candidateFeatures = listOf(
            PackageManager.FEATURE_BLUETOOTH_LE to "Bluetooth Low Energy",
            PackageManager.FEATURE_NFC to "NFC",
            PackageManager.FEATURE_FINGERPRINT to "Capteur d'empreinte",
            PackageManager.FEATURE_CAMERA_FLASH to "Flash Caméra",
            PackageManager.FEATURE_LOCATION_GPS to "Récepteur GPS",
            PackageManager.FEATURE_WIFI_DIRECT to "Wi-Fi Direct",
            PackageManager.FEATURE_SENSOR_GYROSCOPE to "Gyroscope matériel",
            PackageManager.FEATURE_SENSOR_BAROMETER to "Baromètre matériel",
            PackageManager.FEATURE_USB_HOST to "Mode USB Host (OTG)"
        ).filter { (feature, _) -> pm.hasSystemFeature(feature) }.map { it.second }

        val manufacturer = (Build.MANUFACTURER ?: "Inconnu").replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }

        HardwareInfo(
            manufacturer = manufacturer,
            model = Build.MODEL ?: "Inconnu",
            product = Build.PRODUCT ?: "Inconnu",
            cpuArchitecture = System.getProperty("os.arch") ?: Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a",
            abiList = Build.SUPPORTED_ABIS.toList(),
            coreCount = coreCount,
            coreFrequencies = coreFrequencies,
            ramTotalBytes = memInfo.totalMem,
            ramAvailableBytes = memInfo.availMem,
            ramThresholdBytes = memInfo.threshold,
            isLowMemory = memInfo.lowMemory,
            screenResolution = "${displayMetrics.widthPixels} x ${displayMetrics.heightPixels} px",
            screenDensityDpi = displayMetrics.densityDpi,
            screenDensityBucket = FormatUtils.getDensityBucket(displayMetrics.densityDpi),
            screenRefreshRateHz = refreshRate,
            hdrSupported = hdrSupported,
            glEsVersion = "OpenGL ES $glEsVersion",
            vulkanSupported = vulkanSupported,
            hardwareFeatures = candidateFeatures
        )
    }

    override fun getAvailableSensors(): List<SensorInfo> {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)

        return sensorList.map { sensor ->
            val typeStr = when (sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> "Accéléromètre"
                Sensor.TYPE_GYROSCOPE -> "Gyroscope"
                Sensor.TYPE_MAGNETIC_FIELD -> "Magnétomètre"
                Sensor.TYPE_LIGHT -> "Luminosité"
                Sensor.TYPE_PROXIMITY -> "Proximité"
                Sensor.TYPE_PRESSURE -> "Baromètre"
                Sensor.TYPE_GRAVITY -> "Gravité"
                Sensor.TYPE_LINEAR_ACCELERATION -> "Accélération linéaire"
                Sensor.TYPE_ROTATION_VECTOR -> "Vecteur de rotation"
                Sensor.TYPE_STEP_COUNTER -> "Compteur de pas"
                Sensor.TYPE_STEP_DETECTOR -> "Détecteur de pas"
                Sensor.TYPE_AMBIENT_TEMPERATURE -> "Température ambiante"
                Sensor.TYPE_RELATIVE_HUMIDITY -> "Humidité relative"
                else -> sensor.stringType?.substringAfterLast('.')?.replace('_', ' ')
                    ?.replaceFirstChar { it.uppercase() } ?: "Capteur"
            }

            SensorInfo(
                id = sensor.type,
                name = sensor.name,
                type = sensor.type,
                typeString = typeStr,
                vendor = sensor.vendor,
                version = sensor.version,
                maxRange = sensor.maximumRange,
                resolution = sensor.resolution,
                powerMa = sensor.power,
                minDelayUs = sensor.minDelay
            )
        }
    }

    override fun getSensorUpdates(sensorType: Int): Flow<List<Float>> = callbackFlow {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(sensorType)

        if (sensor == null) {
            close()
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.values?.let { vals ->
                    trySend(vals.toList())
                }
            }

            override fun onAccuracyChanged(s: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    override suspend fun getStorageInfo(): StorageInfo = withContext(Dispatchers.IO) {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong

        val totalBytes = totalBlocks * blockSize
        val availableBytes = availableBlocks * blockSize
        val usedBytes = (totalBytes - availableBytes).coerceAtLeast(0L)

        StorageInfo(
            totalBytes = totalBytes,
            availableBytes = availableBytes,
            usedBytes = usedBytes,
            blockSizeBytes = blockSize,
            dataPath = path.absolutePath
        )
    }

    override suspend fun listSafDirectory(treeUri: Uri, documentId: String?): List<SafFileItem> =
        withContext(Dispatchers.IO) {
            val result = mutableListOf<SafFileItem>()
            val targetDocId = documentId ?: DocumentsContract.getTreeDocumentId(treeUri)
            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, targetDocId)

            val projection = arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_SIZE,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED
            )

            try {
                context.contentResolver.query(childrenUri, projection, null, null, null)?.use { cursor ->
                    val idCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                    val nameCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                    val mimeCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)
                    val sizeCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_SIZE)
                    val modCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_LAST_MODIFIED)

                    while (cursor.moveToNext()) {
                        val childId = cursor.getString(idCol) ?: continue
                        val name = cursor.getString(nameCol) ?: "Sans nom"
                        val mime = cursor.getString(mimeCol) ?: "application/octet-stream"
                        val size = if (!cursor.isNull(sizeCol)) cursor.getLong(sizeCol) else 0L
                        val modified = if (!cursor.isNull(modCol)) cursor.getLong(modCol) else 0L
                        val isDir = mime == DocumentsContract.Document.MIME_TYPE_DIR

                        result.add(
                            SafFileItem(
                                name = name,
                                uriString = childId,
                                isDirectory = isDir,
                                sizeBytes = size,
                                lastModifiedMillis = modified,
                                mimeType = mime
                            )
                        )
                    }
                }
            } catch (_: Exception) {
                return@withContext emptyList()
            }

            result.sortedWith(compareBy<SafFileItem> { !it.isDirectory }.thenBy { it.name.lowercase() })
        }

    override suspend fun getInstalledApplications(): List<AppPackageInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        @Suppress("DEPRECATION")
        val packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)

        packages.mapNotNull { pkg ->
            val appInfo = pkg.applicationInfo ?: return@mapNotNull null
            val isSys = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val appName = appInfo.loadLabel(pm).toString()
            val permissions = pkg.requestedPermissions?.toList() ?: emptyList()

            @Suppress("DEPRECATION")
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pkg.longVersionCode
            } else {
                pkg.versionCode.toLong()
            }

            val apkSize = try {
                val sourceFile = appInfo.sourceDir?.let { File(it) }
                if (sourceFile != null && sourceFile.exists()) sourceFile.length() else 0L
            } catch (_: Exception) {
                0L
            }

            val iconBitmap = try {
                drawableToBitmap(appInfo.loadIcon(pm))
            } catch (_: Exception) {
                null
            }

            AppPackageInfo(
                appName = appName,
                packageName = pkg.packageName,
                versionName = pkg.versionName ?: "1.0",
                versionCode = versionCode,
                isSystemApp = isSys,
                firstInstallTime = pkg.firstInstallTime,
                lastUpdateTime = pkg.lastUpdateTime,
                targetSdkVersion = appInfo.targetSdkVersion,
                minSdkVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) appInfo.minSdkVersion else 26,
                apkSizeBytes = apkSize,
                requestedPermissions = permissions,
                iconBitmap = iconBitmap
            )
        }.sortedBy { it.appName.lowercase() }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return Bitmap.createScaledBitmap(drawable.bitmap, 72, 72, true)
        }
        val bitmap = Bitmap.createBitmap(72, 72, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun readSysLong(path: String): Long? {
        return try {
            val file = File(path)
            if (file.exists() && file.canRead()) {
                file.readText().trim().toLongOrNull()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun readKernelVersion(): String {
        val osVersion = System.getProperty("os.version")
        return try {
            val procVersion = File("/proc/version")
            if (procVersion.exists() && procVersion.canRead()) {
                procVersion.readText().trim().take(80)
            } else {
                osVersion ?: "Linux Kernel"
            }
        } catch (_: Exception) {
            osVersion ?: "Linux Kernel"
        }
    }
}
