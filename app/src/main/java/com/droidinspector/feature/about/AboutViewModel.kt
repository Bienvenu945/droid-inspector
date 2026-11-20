package com.droidinspector.feature.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidinspector.data.local.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val isDarkTheme: StateFlow<Boolean> = preferencesRepository.isDarkTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val refreshIntervalMs: StateFlow<Long> = preferencesRepository.refreshIntervalMs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2000L)

    fun toggleDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setDarkTheme(enabled)
        }
    }

    fun setRefreshInterval(intervalMs: Long) {
        viewModelScope.launch {
            preferencesRepository.setRefreshIntervalMs(intervalMs)
        }
    }
}
