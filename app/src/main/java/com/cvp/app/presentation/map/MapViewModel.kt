package com.cvp.app.presentation.map

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvp.app.domain.model.RiskZone
import com.cvp.app.domain.model.UserLocation
import com.cvp.app.domain.repository.LocationRepository
import com.cvp.app.domain.repository.RiskZoneRepository
import com.cvp.app.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Stable
data class MapUiState(
    val userLocation: UserLocation? = null,
    val riskZones: List<RiskZone> = emptyList(),
    val isLoadingZones: Boolean = false,
    val showZones: Boolean = true,
    val hasLocationPermission: Boolean = false,
    val errorMessage: String? = null,
    val loadFailed: Boolean = false,
    val selectedZone: RiskZone? = null,
    val alertRadiusMeters: Int = 150,
    val notificationsEnabled: Boolean = true,
)

sealed interface MapEvent {
    data class CenterOnUser(val location: UserLocation) : MapEvent
    data class FocusOnZone(val zone: RiskZone) : MapEvent
}

class MapViewModel(
    private val locationRepository: LocationRepository,
    private val riskZoneRepository: RiskZoneRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _events = Channel<MapEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadZones()
        collectSettings()
    }

    private fun collectSettings() {
        settingsRepository.settings
            .onEach { settings ->
                _uiState.update {
                    it.copy(
                        showZones = settings.showZones,
                        alertRadiusMeters = settings.alertRadiusMeters,
                        notificationsEnabled = settings.notificationsEnabled,
                    )
                }
            }
            .catch { /* settings errors are non-fatal */ }
            .launchIn(viewModelScope)
    }

    private fun loadZones() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingZones = true, errorMessage = null, loadFailed = false) }
            runCatching { riskZoneRepository.getAllZones() }
                .onSuccess { zones ->
                    _uiState.update { it.copy(riskZones = zones, isLoadingZones = false, loadFailed = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingZones = false,
                            loadFailed = true,
                            errorMessage = error.message ?: "Error al cargar las zonas de riesgo",
                        )
                    }
                }
        }
    }

    fun onPermissionGranted() {
        if (_uiState.value.hasLocationPermission) return
        _uiState.update { it.copy(hasLocationPermission = true) }
        locationRepository.observeLocation()
            .onEach { location -> _uiState.update { it.copy(userLocation = location) } }
            .catch { error -> _uiState.update { it.copy(errorMessage = error.message ?: "Error de ubicación") } }
            .launchIn(viewModelScope)
    }

    fun onToggleZones() {
        val newValue = !_uiState.value.showZones
        viewModelScope.launch { settingsRepository.setShowZones(newValue) }
    }

    fun onCenterOnUser() {
        val location = _uiState.value.userLocation ?: return
        viewModelScope.launch { _events.send(MapEvent.CenterOnUser(location)) }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onRetry() {
        _uiState.update { it.copy(loadFailed = false, errorMessage = null) }
        loadZones()
    }

    fun onZoneSelected(zone: RiskZone) {
        _uiState.update { it.copy(selectedZone = zone) }
        viewModelScope.launch { _events.send(MapEvent.FocusOnZone(zone)) }
    }

    fun onSheetDismissed() {
        _uiState.update { it.copy(selectedZone = null) }
    }
}
