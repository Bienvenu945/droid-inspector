package com.droidinspector.feature.hardware

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidinspector.domain.model.HardwareInfo
import com.droidinspector.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HardwareUiState(
    val isLoading: Boolean = true,
    val hardwareInfo: HardwareInfo? = null
)

@HiltViewModel
class HardwareViewModel @Inject constructor(
    private val repository: DeviceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HardwareUiState())
    val uiState: StateFlow<HardwareUiState> = _uiState.asStateFlow()

    init {
        startLiveFrequencyPolling()
    }

    private fun startLiveFrequencyPolling() {
        viewModelScope.launch {
            while (isActive) {
                val info = runCatching { repository.getHardwareInfo() }.getOrNull()
                if (info != null) {
                    _uiState.value = HardwareUiState(isLoading = false, hardwareInfo = info)
                }
                delay(2000L)
            }
        }
    }
}
