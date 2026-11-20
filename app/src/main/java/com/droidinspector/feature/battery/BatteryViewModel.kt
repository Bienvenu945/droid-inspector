package com.droidinspector.feature.battery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidinspector.domain.model.BatteryInfo
import com.droidinspector.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BatteryUiState(
    val isLoading: Boolean = true,
    val batteryInfo: BatteryInfo? = null
)

@HiltViewModel
class BatteryViewModel @Inject constructor(
    private val repository: DeviceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BatteryUiState())
    val uiState: StateFlow<BatteryUiState> = _uiState.asStateFlow()

    init {
        observeBattery()
    }

    private fun observeBattery() {
        viewModelScope.launch {
            repository.getBatteryInfo().collect { info ->
                _uiState.value = BatteryUiState(
                    isLoading = false,
                    batteryInfo = info
                )
            }
        }
    }
}
