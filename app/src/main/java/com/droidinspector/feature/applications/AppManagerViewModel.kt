package com.droidinspector.feature.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidinspector.domain.model.AppPackageInfo
import com.droidinspector.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AppFilter(val label: String) {
    ALL("Toutes"),
    USER("Utilisateur"),
    SYSTEM("Système")
}

enum class AppSortOrder(val label: String) {
    NAME("Nom (A-Z)"),
    SIZE("Taille"),
    INSTALL_DATE("Date d'installation")
}

data class AppManagerUiState(
    val isLoading: Boolean = true,
    val allApps: List<AppPackageInfo> = emptyList(),
    val filteredApps: List<AppPackageInfo> = emptyList(),
    val searchQuery: String = "",
    val filter: AppFilter = AppFilter.ALL,
    val sortOrder: AppSortOrder = AppSortOrder.NAME,
    val selectedApp: AppPackageInfo? = null
)

@HiltViewModel
class AppManagerViewModel @Inject constructor(
    private val repository: DeviceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppManagerUiState())
    val uiState: StateFlow<AppManagerUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val apps = repository.getInstalledApplications()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                allApps = apps
            )
            applyFilters()
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun onFilterChange(filter: AppFilter) {
        _uiState.value = _uiState.value.copy(filter = filter)
        applyFilters()
    }

    fun onSortOrderChange(sortOrder: AppSortOrder) {
        _uiState.value = _uiState.value.copy(sortOrder = sortOrder)
        applyFilters()
    }

    fun selectApp(app: AppPackageInfo?) {
        _uiState.value = _uiState.value.copy(selectedApp = app)
    }

    private fun applyFilters() {
        val current = _uiState.value
        val query = current.searchQuery.trim().lowercase()

        val filtered = current.allApps
            .filter { app ->
                val matchesQuery = query.isEmpty() ||
                    app.appName.lowercase().contains(query) ||
                    app.packageName.lowercase().contains(query)

                val matchesType = when (current.filter) {
                    AppFilter.ALL -> true
                    AppFilter.USER -> !app.isSystemApp
                    AppFilter.SYSTEM -> app.isSystemApp
                }
                matchesQuery && matchesType
            }
            .let { list ->
                when (current.sortOrder) {
                    AppSortOrder.NAME -> list.sortedBy { it.appName.lowercase() }
                    AppSortOrder.SIZE -> list.sortedByDescending { it.apkSizeBytes }
                    AppSortOrder.INSTALL_DATE -> list.sortedByDescending { it.firstInstallTime }
                }
            }

        _uiState.value = current.copy(filteredApps = filtered)
    }
}
