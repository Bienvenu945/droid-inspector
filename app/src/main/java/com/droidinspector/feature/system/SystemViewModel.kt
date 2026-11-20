package com.droidinspector.feature.system

import androidx.lifecycle.ViewModel
import com.droidinspector.domain.model.DeviceSummary
import com.droidinspector.domain.model.SystemSecurityInfo
import com.droidinspector.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class SystemUiState(
    val summary: DeviceSummary,
    val security: SystemSecurityInfo
)

@HiltViewModel
class SystemViewModel @Inject constructor(
    repository: DeviceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SystemUiState(
            summary = repository.getDeviceSummary(),
            security = repository.getSystemSecurityInfo()
        )
    )
    val uiState: StateFlow<SystemUiState> = _uiState.asStateFlow()
}
