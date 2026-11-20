package com.droidinspector.feature.storage

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidinspector.domain.model.SafFileItem
import com.droidinspector.domain.model.StorageInfo
import com.droidinspector.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FolderBreadcrumb(
    val name: String,
    val documentId: String?
)

data class StorageUiState(
    val isLoading: Boolean = true,
    val storageInfo: StorageInfo? = null,
    val selectedTreeUri: Uri? = null,
    val breadcrumbs: List<FolderBreadcrumb> = emptyList(),
    val currentFiles: List<SafFileItem> = emptyList(),
    val filteredFiles: List<SafFileItem> = emptyList(),
    val searchQuery: String = "",
    val isLoadingFolder: Boolean = false
)

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val repository: DeviceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StorageUiState())
    val uiState: StateFlow<StorageUiState> = _uiState.asStateFlow()

    init {
        refreshStorage()
    }

    fun refreshStorage() {
        viewModelScope.launch {
            val info = repository.getStorageInfo()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                storageInfo = info
            )
        }
    }

    fun onSafTreeSelected(uri: Uri) {
        _uiState.value = _uiState.value.copy(
            selectedTreeUri = uri,
            breadcrumbs = listOf(FolderBreadcrumb("Dossier sélectionné", null)),
            searchQuery = ""
        )
        loadDirectory(uri, null)
    }

    fun openSubFolder(folder: SafFileItem) {
        val treeUri = _uiState.value.selectedTreeUri ?: return
        if (!folder.isDirectory) return

        val updatedBreadcrumbs = _uiState.value.breadcrumbs + FolderBreadcrumb(
            name = folder.name,
            documentId = folder.uriString
        )
        _uiState.value = _uiState.value.copy(
            breadcrumbs = updatedBreadcrumbs,
            searchQuery = ""
        )
        loadDirectory(treeUri, folder.uriString)
    }

    fun navigateToBreadcrumb(index: Int) {
        val treeUri = _uiState.value.selectedTreeUri ?: return
        val crumbs = _uiState.value.breadcrumbs
        if (index !in crumbs.indices) return

        val trimmed = crumbs.take(index + 1)
        _uiState.value = _uiState.value.copy(
            breadcrumbs = trimmed,
            searchQuery = ""
        )
        loadDirectory(treeUri, trimmed.last().documentId)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterFiles()
    }

    private fun loadDirectory(treeUri: Uri, documentId: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingFolder = true)
            val items = repository.listSafDirectory(treeUri, documentId)
            _uiState.value = _uiState.value.copy(
                isLoadingFolder = false,
                currentFiles = items
            )
            filterFiles()
        }
    }

    private fun filterFiles() {
        val query = _uiState.value.searchQuery.trim().lowercase()
        val filtered = if (query.isEmpty()) {
            _uiState.value.currentFiles
        } else {
            _uiState.value.currentFiles.filter { it.name.lowercase().contains(query) }
        }
        _uiState.value = _uiState.value.copy(filteredFiles = filtered)
    }
}
