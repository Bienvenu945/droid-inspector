package com.droidinspector.domain.model

import android.graphics.Bitmap

data class DeviceSummary(
    val deviceName: String,
    val model: String,
    val manufacturer: String,
    val brand: String,
    val product: String,
    val hardware: String,
    val androidVersion: String,
    val apiLevel: Int,
    val securityPatch: String,
    val buildNumber: String,
    val buildTimeMillis: Long,
    val board: String,
    val bootloader: String,
    val fingerprint: String,
    val kernelVersion: String,
    val javaVmVersion: String
)

data class SystemSecurityInfo(
    val encryptionStatus: String,
    val seLinuxMode: String,
    val rootDetected: Boolean,
    val isDebugBuild: Boolean
)

data class BatteryInfo(
    val percentage: Int,
    val isCharging: Boolean,
    val statusLabel: String,
    val chargingSource: String,
    val health: String,
    val technology: String,
    val temperatureCelsius: Float,
    val voltageMilliVolts: Int,
    val currentMicroAmps: Int? = null,
    val chargeCounterMicroAh: Int? = null
)

data class CpuCoreFrequency(
    val coreIndex: Int,
    val currentKhz: Long?,
    val minKhz: Long?,
    val maxKhz: Long?
)

data class HardwareInfo(
    val manufacturer: String,
    val model: String,
    val product: String,
    val cpuArchitecture: String,
    val abiList: List<String>,
    val coreCount: Int,
    val coreFrequencies: List<CpuCoreFrequency>,
    val ramTotalBytes: Long,
    val ramAvailableBytes: Long,
    val ramThresholdBytes: Long,
    val isLowMemory: Boolean,
    val screenResolution: String,
    val screenDensityDpi: Int,
    val screenDensityBucket: String,
    val screenRefreshRateHz: Float,
    val hdrSupported: Boolean,
    val glEsVersion: String,
    val vulkanSupported: Boolean,
    val hardwareFeatures: List<String>
)

data class SensorInfo(
    val id: Int,
    val name: String,
    val type: Int,
    val typeString: String,
    val vendor: String,
    val version: Int,
    val maxRange: Float,
    val resolution: Float,
    val powerMa: Float,
    val minDelayUs: Int
)

data class StorageInfo(
    val totalBytes: Long,
    val availableBytes: Long,
    val usedBytes: Long,
    val blockSizeBytes: Long,
    val dataPath: String
)

data class SafFileItem(
    val name: String,
    val uriString: String,
    val isDirectory: Boolean,
    val sizeBytes: Long,
    val lastModifiedMillis: Long,
    val mimeType: String
)

data class AppPackageInfo(
    val appName: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val isSystemApp: Boolean,
    val firstInstallTime: Long,
    val lastUpdateTime: Long,
    val targetSdkVersion: Int,
    val minSdkVersion: Int,
    val apkSizeBytes: Long,
    val requestedPermissions: List<String>,
    val iconBitmap: Bitmap? = null
)

data class ReportSectionsSelection(
    val includeDevice: Boolean = true,
    val includeHardware: Boolean = true,
    val includeSystem: Boolean = true,
    val includeBattery: Boolean = true,
    val includeStorage: Boolean = true,
    val includeSensors: Boolean = true,
    val includeApps: Boolean = true
)
