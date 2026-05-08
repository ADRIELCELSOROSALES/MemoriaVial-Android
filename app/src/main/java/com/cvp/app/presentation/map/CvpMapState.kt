package com.cvp.app.presentation.map

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.cvp.app.domain.model.RiskZone
import com.cvp.app.domain.model.UserLocation
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

@Stable
class CvpMapState(
    val mapView: MapView,
    private val appContext: Context,
) {
    private var mapLibreMap: MapLibreMap? = null
    private var pendingStyleLoad: Pair<Boolean, () -> Unit>? = null
    private var pendingLocationEnabled: Pair<Boolean, Boolean>? = null
    private var previousZoomBeforeFocus: Double = 12.0

    companion object {
        const val STYLE_LIGHT = "https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json"
        const val STYLE_DARK = "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
    }

    init {
        mapView.getMapAsync { map ->
            mapLibreMap = map
            map.uiSettings.apply {
                isLogoEnabled = false
                isCompassEnabled = false
                isAttributionEnabled = false
            }
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(-34.6037, -58.3816), 12.0)
            )
            pendingStyleLoad?.let { (isDark, callback) ->
                pendingStyleLoad = null
                loadStyleOnMap(map, isDark, callback)
            }
        }
    }

    fun loadStyle(isDark: Boolean, onStyleLoaded: () -> Unit = {}) {
        val map = mapLibreMap ?: run {
            pendingStyleLoad = Pair(isDark, onStyleLoaded)
            return
        }
        loadStyleOnMap(map, isDark, onStyleLoaded)
    }

    private fun loadStyleOnMap(map: MapLibreMap, isDark: Boolean, onStyleLoaded: () -> Unit) {
        val uri = if (isDark) STYLE_DARK else STYLE_LIGHT
        map.setStyle(Style.Builder().fromUri(uri)) { _ ->
            pendingLocationEnabled?.let { (enabled, hasBearing) ->
                val style = map.style ?: return@let
                pendingLocationEnabled = null
                applyLocation(map, style, enabled, hasBearing)
            }
            onStyleLoaded()
        }
    }

    fun setLocationEnabled(enabled: Boolean, hasBearing: Boolean = false) {
        val map = mapLibreMap ?: run { pendingLocationEnabled = Pair(enabled, hasBearing); return }
        val style = map.style ?: run { pendingLocationEnabled = Pair(enabled, hasBearing); return }
        applyLocation(map, style, enabled, hasBearing)
    }

    // Permission is guaranteed to be granted before this is called (checked in MapScreen).
    @SuppressLint("MissingPermission")
    private fun applyLocation(map: MapLibreMap, style: Style, enabled: Boolean, hasBearing: Boolean) {
        try {
            val lc = map.locationComponent
            if (!lc.isLocationComponentActivated) {
                lc.activateLocationComponent(
                    LocationComponentActivationOptions.builder(appContext, style)
                        .useDefaultLocationEngine(true)
                        .build()
                )
            }
            lc.isLocationComponentEnabled = enabled
            if (enabled) {
                lc.renderMode = if (hasBearing) RenderMode.COMPASS else RenderMode.NORMAL
                lc.cameraMode = CameraMode.NONE
            }
        } catch (_: SecurityException) {
            // Permission was revoked between check and use — puck stays hidden.
        }
    }

    fun flyTo(location: UserLocation, zoom: Double = 15.0) {
        mapLibreMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), zoom),
            1500,
            null,
        )
    }

    fun focusOnZone(zone: RiskZone) {
        val map = mapLibreMap ?: return
        previousZoomBeforeFocus = map.cameraPosition.zoom
        val bottomPadding = (mapView.height / 2).coerceAtLeast(300)
        map.setPadding(0, 0, 0, bottomPadding)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(zone.latitude, zone.longitude), 15.0),
            800,
            null,
        )
    }

    fun restoreZoom() {
        val map = mapLibreMap ?: return
        map.setPadding(0, 0, 0, 0)
        map.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().zoom(previousZoomBeforeFocus).build()
            ),
            600,
            null,
        )
    }
}

@Composable
fun rememberCvpMapState(): CvpMapState {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember { MapView(context) }
    val state = remember(mapView) { CvpMapState(mapView, context.applicationContext) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    return state
}
