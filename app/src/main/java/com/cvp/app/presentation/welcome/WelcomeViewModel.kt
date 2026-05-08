package com.cvp.app.presentation.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvp.app.domain.repository.RiskZoneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

sealed interface ZonesLoadState {
    data object Idle : ZonesLoadState
    data object Loading : ZonesLoadState
    data class Success(val count: Int) : ZonesLoadState
    data class Error(val message: String) : ZonesLoadState
}

class WelcomeViewModel(
    private val repository: RiskZoneRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ZonesLoadState>(ZonesLoadState.Idle)
    val uiState: StateFlow<ZonesLoadState> = _uiState.asStateFlow()

    fun loadZones() {
        if (_uiState.value is ZonesLoadState.Loading) return
        viewModelScope.launch {
            _uiState.value = ZonesLoadState.Loading
            runCatching { repository.getAllZones() }
                .onSuccess { zones ->
                    Timber.d("Loaded ${zones.size} zones")
                    _uiState.value = ZonesLoadState.Success(zones.size)
                }
                .onFailure { e ->
                    Timber.e(e, "Failed to load zones")
                    _uiState.value = ZonesLoadState.Error(e.message ?: "Error desconocido")
                }
        }
    }
}
