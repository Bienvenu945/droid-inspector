package com.droidinspector.feature.sensors

import android.hardware.Sensor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidinspector.domain.model.SensorInfo
import com.droidinspector.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SensorLabUiState(
    val sensors: List<SensorInfo> = emptyList(),
    val selectedSensorType: Int? = null,
    val selectedSensorValues: List<Float> = emptyList(),
    val accelerometerValues: List<Float> = emptyList(),
    val hasAccelerometer: Boolean = false,
    val missingCoreSensors: List<String> = emptyList()
)

@HiltViewModel
class SensorLabViewModel @Inject constructor(
    private val repository: DeviceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SensorLabUiState())
    val uiState: StateFlow<SensorLabUiState> = _uiState.asStateFlow()

    private var activeSensorJob: Job? = null
    private var accelJob: Job? = null

    init {
        loadSensors()
    }

    private fun loadSensors() {
        val available = repository.getAvailableSensors()
        val hasAccel = available.any { it.type == Sensor.TYPE_ACCELEROMETER }

        val expectedCore = listOf(
            Sensor.TYPE_ACCELEROMETER to "Accéléromètre",
            Sensor.TYPE_GYROSCOPE to "Gyroscope",
            Sensor.TYPE_MAGNETIC_FIELD to "Magnétomètre",
            Sensor.TYPE_LIGHT to "Capteur de luminosité",
            Sensor.TYPE_PROXIMITY to "Capteur de proximité",
            Sensor.TYPE_PRESSURE to "Baromètre"
        )
        val missing = expectedCore
            .filter { (type, _) -> available.none { it.type == type } }
            .map { it.second }

        val initialSelected = available.firstOrNull()?.type

        _uiState.value = SensorLabUiState(
            sensors = available,
            selectedSensorType = initialSelected,
            hasAccelerometer = hasAccel,
            missingCoreSensors = missing
        )

        if (hasAccel) {
            observeAccelerometer()
        }
        initialSelected?.let { selectSensor(it) }
    }

    private fun observeAccelerometer() {
        accelJob?.cancel()
        accelJob = viewModelScope.launch {
            repository.getSensorUpdates(Sensor.TYPE_ACCELEROMETER).collect { values ->
                _uiState.value = _uiState.value.copy(accelerometerValues = values)
            }
        }
    }

    fun selectSensor(sensorType: Int) {
        if (_uiState.value.selectedSensorType == sensorType && activeSensorJob?.isActive == true) return
        _uiState.value = _uiState.value.copy(
            selectedSensorType = sensorType,
            selectedSensorValues = emptyList()
        )
        activeSensorJob?.cancel()
        activeSensorJob = viewModelScope.launch {
            repository.getSensorUpdates(sensorType).collect { values ->
                _uiState.value = _uiState.value.copy(selectedSensorValues = values)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        activeSensorJob?.cancel()
        accelJob?.cancel()
    }
}
