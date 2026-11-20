package com.droidinspector.data.repository

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import com.droidinspector.domain.model.*
import com.droidinspector.domain.repository.DeviceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DeviceRepository {

    override fun getDeviceSummary(): DeviceSummary {
        return DeviceSummary(
            deviceName = "${Build.MANUFACTURER} ${Build.MODEL}",
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            androidVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            securityPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Build.VERSION.SECURITY_PATCH else "N/A",
            buildNumber = Build.DISPLAY,
            board = Build.BOARD,
            bootloader = Build.BOOTLOADER,
            radioVersion = Build.getRadioVersion() ?: "Unknown"
        )
    }

    override fun getBatteryInfo(): Flow<BatteryInfo> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                intent?.let {
                    val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val percentage = if (level >= 0 && scale > 0) (level * 100) / scale else 0

                    val status = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                            status == BatteryManager.BATTERY_STATUS_FULL

                    val chargePlug = it.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
                    val source = when (chargePlug) {
                        BatteryManager.BATTERY_PLUGGED_AC -> "Secteur (AC)"
                        BatteryManager.BATTERY_PLUGGED_USB -> "Port USB"
                        BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Sans fil (Induction)"
                        else -> if (isCharging) "En charge" else "Batterie"
                    }

                    val healthVal = it.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
                    val health = when (healthVal) {
                        BatteryManager.BATTERY_HEALTH_GOOD -> "Bonne santé"
                        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Surchauffe"
                        BatteryManager.BATTERY_HEALTH_DEAD -> "Défectueuse"
                        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Surtension"
                        else -> "Normale"
                    }

                    val technology = it.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"
                    val temperatureTenths = it.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                    val temperatureCelsius = temperatureTenths / 10f
                    val voltage = it.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)

                    trySend(
                        BatteryInfo(
                            percentage = percentage,
                            isCharging = isCharging,
                            chargingSource = source,
                            health = health,
                            technology = technology,
                            temperatureCelsius = temperatureCelsius,
                            voltageMilliVolts = voltage
                        )
                    )
                }
            }
        }

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, filter)

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    override fun getHardwareInfo(): HardwareInfo {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)

        val displayMetrics = context.resources.displayMetrics
        val coreCount = Runtime.getRuntime().availableProcessors()

        return HardwareInfo(
            cpuArchitecture = System.getProperty("os.arch") ?: "arm64-v8a",
            abiList = Build.SUPPORTED_ABIS.toList(),
            coreCount = coreCount,
            minFrequencyMhz = 300L,
            maxFrequencyMhz = 2840L,
            ramTotalBytes = memInfo.totalMem,
            ramAvailableBytes = memInfo.availMem,
            screenResolution = "${displayMetrics.widthPixels} x ${displayMetrics.heightPixels}",
            screenDensityDpi = displayMetrics.densityDpi,
            screenRefreshRateHz = 60f,
            gpuVendor = "Qualcomm / ARM",
            gpuRenderer = "Adreno / Mali GPU"
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
                else -> sensor.stringType ?: "Autre capteur"
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
                powerMa = sensor.power
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

    override fun getStorageInfo(): StorageInfo {
        val path: File = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong

        val totalBytes = totalBlocks * blockSize
        val availableBytes = availableBlocks * blockSize
        val usedBytes = totalBytes - availableBytes

        return StorageInfo(
            totalBytes = totalBytes,
            availableBytes = availableBytes,
            usedBytes = usedBytes,
            appsUsageBytes = (usedBytes * 0.45).toLong(),
            mediaUsageBytes = (usedBytes * 0.35).toLong(),
            systemUsageBytes = (usedBytes * 0.20).toLong()
        )
    }

    override suspend fun getInstalledApplications(includeSystemApps: Boolean): List<AppPackageInfo> {
        val pm = context.packageManager
        val packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)

        return packages.mapNotNull { pkg ->
            val isSys = (pkg.applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM)) != 0
            if (!includeSystemApps && isSys) return@mapNotNull null

            val appName = pkg.applicationInfo?.loadLabel(pm)?.toString() ?: pkg.packageName
            val permissions = pkg.requestedPermissions?.toList() ?: emptyList()

            @Suppress("DEPRECATION")
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pkg.longVersionCode
            } else {
                pkg.versionCode.toLong()
            }

            AppPackageInfo(
                appName = appName,
                packageName = pkg.packageName,
                versionName = pkg.versionName ?: "1.0",
                versionCode = versionCode,
                isSystemApp = isSys,
                firstInstallTime = pkg.firstInstallTime,
                lastUpdateTime = pkg.lastUpdateTime,
                targetSdkVersion = pkg.applicationInfo?.targetSdkVersion ?: 34,
                requestedPermissions = permissions
            )
        }
    }
}
