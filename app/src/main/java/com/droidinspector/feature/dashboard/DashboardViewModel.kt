package com.droidinspector.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidinspector.core.utils.FormatUtils
import com.droidinspector.data.local.PreferencesRepository
import com.droidinspector.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * État de l'écran Dashboard principal.
 * Signature : @Bienvenu
 */
data class DashboardUiState(
    val isLoaded: Boolean = false,
    val deviceName: String = "",
    val manufacturer: String = "",
    val model: String = "",
    val androidVersion: String = "",
    val apiLevel: Int = 0,
    val buildNumber: String = "",
    val cpuArchitecture: String = "",
    val cpuCoreCount: Int = 0,
    val screenResolution: String = "",
    val screenRefreshRateHz: Float = 60f,
    val sensorsCount: Int = 0,
    val ramUsedFormatted: String = "0.0 Go",
    val ramTotalFormatted: String = "0.0 Go",
    val ramUsagePercentage: Float = 0f,
    val storageUsedFormatted: String = "0.0 Go",
    val storageTotalFormatted: String = "0.0 Go",
    val storageUsagePercentage: Float = 0f,
    val batteryLevel: Int = 0,
    val isCharging: Boolean = false,
    val batteryStatusText: String = "",
    val batteryTemperature: Float = 0f,
    val batteryVoltageMv: Int = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DeviceRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeBatteryStream()
        startPollingMetrics()
    }

    private fun observeBatteryStream() {
        viewModelScope.launch {
            repository.getBatteryInfo().collect { battery ->
                _uiState.value = _uiState.value.copy(
                    batteryLevel = battery.percentage,
                    isCharging = battery.isCharging,
                    batteryStatusText = battery.statusLabel,
                    batteryTemperature = battery.temperatureCelsius,
                    batteryVoltageMv = battery.voltageMilliVolts
                )
            }
        }
    }

    private fun startPollingMetrics() {
        viewModelScope.launch {
            while (isActive) {
                refreshNow()
                val interval = preferencesRepository.refreshIntervalMs.first().coerceIn(1000L, 10000L)
                delay(interval)
            }
        }
    }

    fun refreshNow() {
        viewModelScope.launch {
            val summary = repository.getDeviceSummary()
            val hardware = repository.getHardwareInfo()
            val storage = repository.getStorageInfo()
            val sensors = repository.getAvailableSensors()
            val battery = repository.getCurrentBatterySnapshot()

            val ramUsed = (hardware.ramTotalBytes - hardware.ramAvailableBytes).coerceAtLeast(0L)
            val ramPct = if (hardware.ramTotalBytes > 0L) {
                ((ramUsed.toDouble() / hardware.ramTotalBytes.toDouble()) * 100).toFloat().coerceIn(0f, 100f)
            } else 0f

            val storagePct = if (storage.totalBytes > 0L) {
                ((storage.usedBytes.toDouble() / storage.totalBytes.toDouble()) * 100).toFloat().coerceIn(0f, 100f)
            } else 0f

            _uiState.value = _uiState.value.copy(
                isLoaded = true,
                deviceName = summary.deviceName,
                manufacturer = summary.manufacturer,
                model = summary.model,
                androidVersion = summary.androidVersion,
                apiLevel = summary.apiLevel,
                buildNumber = summary.buildNumber,
                cpuArchitecture = hardware.cpuArchitecture,
                cpuCoreCount = hardware.coreCount,
                screenResolution = hardware.screenResolution,
                screenRefreshRateHz = hardware.screenRefreshRateHz,
                sensorsCount = sensors.size,
                ramUsedFormatted = FormatUtils.formatGigabytesShort(ramUsed),
                ramTotalFormatted = FormatUtils.formatGigabytesShort(hardware.ramTotalBytes),
                ramUsagePercentage = ramPct,
                storageUsedFormatted = FormatUtils.formatGigabytesShort(storage.usedBytes),
                storageTotalFormatted = FormatUtils.formatGigabytesShort(storage.totalBytes),
                storageUsagePercentage = storagePct,
                batteryLevel = battery.percentage,
                isCharging = battery.isCharging,
                batteryStatusText = battery.statusLabel,
                batteryTemperature = battery.temperatureCelsius,
                batteryVoltageMv = battery.voltageMilliVolts
            )
        }
    }
}
