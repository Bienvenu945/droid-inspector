package com.droidinspector.domain.model

data class DeviceSummary(
    val deviceName: String,
    val model: String,
    val manufacturer: String,
    val androidVersion: String,
    val apiLevel: Int,
    val securityPatch: String,
    val buildNumber: String,
    val board: String,
    val bootloader: String,
    val radioVersion: String
)

data class BatteryInfo(
    val percentage: Int,
    val isCharging: Boolean,
    val chargingSource: String, // AC, USB, Wireless
    val health: String, // Good, Overheat, Dead, Over Voltage
    val technology: String, // Li-ion, Li-poly
    val temperatureCelsius: Float,
    val voltageMilliVolts: Int,
    val capacityMah: Int = 4500
)

data class HardwareInfo(
    val cpuArchitecture: String,
    val abiList: List<String>,
    val coreCount: Int,
    val minFrequencyMhz: Long,
    val maxFrequencyMhz: Long,
    val ramTotalBytes: Long,
    val ramAvailableBytes: Long,
    val screenResolution: String,
    val screenDensityDpi: Int,
    val screenRefreshRateHz: Float,
    val gpuVendor: String,
    val gpuRenderer: String
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
    val currentValues: List<Float> = emptyList()
)

data class StorageInfo(
    val totalBytes: Long,
    val availableBytes: Long,
    val usedBytes: Long,
    val isRemovable: Boolean = false,
    val appsUsageBytes: Long = 0L,
    val mediaUsageBytes: Long = 0L,
    val systemUsageBytes: Long = 0L
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
    val requestedPermissions: List<String> = emptyList()
)
