package com.droidinspector.feature.reports

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidinspector.data.pdf.PdfReportGenerator
import com.droidinspector.domain.model.ReportSectionsSelection
import com.droidinspector.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class ReportsUiState(
    val selection: ReportSectionsSelection = ReportSectionsSelection(),
    val isGenerating: Boolean = false,
    val statusMessage: String? = null,
    val errorMessage: String? = null,
    val deviceTitle: String = "",
    val sensorsCount: Int = 0,
    val appsCount: Int = 0
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val repository: DeviceRepository,
    private val pdfGenerator: PdfReportGenerator
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadSummaryCounts()
    }

    private fun loadSummaryCounts() {
        viewModelScope.launch {
            val summary = repository.getDeviceSummary()
            val sensors = repository.getAvailableSensors()
            val apps = repository.getInstalledApplications()
            _uiState.value = _uiState.value.copy(
                deviceTitle = "${summary.deviceName} · Android ${summary.androidVersion}",
                sensorsCount = sensors.size,
                appsCount = apps.size
            )
        }
    }

    fun updateSelection(transform: (ReportSectionsSelection) -> ReportSectionsSelection) {
        _uiState.value = _uiState.value.copy(
            selection = transform(_uiState.value.selection),
            statusMessage = null,
            errorMessage = null
        )
    }

    fun exportPdfToUri(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true, statusMessage = null, errorMessage = null)
            val result = pdfGenerator.generateToUri(
                uri = uri,
                selection = _uiState.value.selection,
                device = repository.getDeviceSummary(),
                hardware = repository.getHardwareInfo(),
                security = repository.getSystemSecurityInfo(),
                battery = repository.getCurrentBatterySnapshot(),
                storage = repository.getStorageInfo(),
                sensors = repository.getAvailableSensors(),
                apps = repository.getInstalledApplications()
            )
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isGenerating = false,
                    statusMessage = "Rapport PDF enregistré avec succès."
                )
            } else {
                _uiState.value.copy(
                    isGenerating = false,
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Erreur lors de l'export PDF."
                )
            }
        }
    }

    fun generateForSharing(onFileReady: (File) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true, statusMessage = null, errorMessage = null)
            val result = pdfGenerator.generateToCacheFile(
                selection = _uiState.value.selection,
                device = repository.getDeviceSummary(),
                hardware = repository.getHardwareInfo(),
                security = repository.getSystemSecurityInfo(),
                battery = repository.getCurrentBatterySnapshot(),
                storage = repository.getStorageInfo(),
                sensors = repository.getAvailableSensors(),
                apps = repository.getInstalledApplications()
            )
            val file = result.getOrNull()
            if (file != null) {
                _uiState.value = _uiState.value.copy(isGenerating = false)
                onFileReady(file)
            } else {
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Impossible de créer le fichier PDF."
                )
            }
        }
    }
}
