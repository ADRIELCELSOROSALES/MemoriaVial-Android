package com.cvp.app.presentation.map.rendering

import com.cvp.app.domain.model.RiskZone
import com.cvp.app.domain.model.Severity
import com.google.gson.JsonObject
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

class RiskZoneRenderer(
    private val mapView: MapView,
    private val onZoneClick: (RiskZone) -> Unit,
) {
    private var mapLibreMap: MapLibreMap? = null
    private val zoneById = HashMap<String, RiskZone>()
    private var clickListenerAdded = false

    private var cachedZones: List<RiskZone> = emptyList()
    private var cachedShow: Boolean = true
    private var cachedColors: Map<Severity, Int> = emptyMap()

    companion object {
        private const val SOURCE_ID = "risk-zones"
        private const val LAYER_ID = "risk-zones-circles"
    }

    private val clickListener = MapLibreMap.OnMapClickListener { latLng ->
        val map = mapLibreMap ?: return@OnMapClickListener false
        val screenPoint = map.projection.toScreenLocation(latLng)
        val features = map.queryRenderedFeatures(
            android.graphics.PointF(screenPoint.x, screenPoint.y),
            LAYER_ID,
        )
        if (features.isNotEmpty()) {
            val id = features[0].getStringProperty("zone_id")
            id?.let { zoneById[it]?.let { zone -> onZoneClick(zone) } }
            true
        } else {
            false
        }
    }

    fun onStyleLoaded() {
        mapView.getMapAsync { map ->
            mapLibreMap = map
            if (!clickListenerAdded) {
                map.addOnMapClickListener(clickListener)
                clickListenerAdded = true
            }
            render(map)
        }
    }

    fun updateZones(zones: List<RiskZone>, show: Boolean, colors: Map<Severity, Int>) {
        cachedZones = zones
        cachedShow = show
        cachedColors = colors
        mapLibreMap?.let { render(it) }
    }

    private fun render(map: MapLibreMap) {
        val style = map.style ?: return
        zoneById.clear()

        val features = if (cachedShow && cachedZones.isNotEmpty()) {
            cachedZones.map { zone ->
                zoneById[zone.id] = zone
                val argb = cachedColors[zone.severity] ?: zone.severity.defaultColor()
                Feature.fromGeometry(
                    Point.fromLngLat(zone.longitude, zone.latitude),
                    JsonObject().apply {
                        addProperty("zone_id", zone.id)
                        addProperty("severity", zone.severity.name)
                        addProperty("color", argb.toHexColor())
                    },
                )
            }
        } else {
            emptyList()
        }

        val collection = FeatureCollection.fromFeatures(features)
        val existingSource = style.getSourceAs<GeoJsonSource>(SOURCE_ID)
        if (existingSource != null) {
            existingSource.setGeoJson(collection)
        } else {
            style.addSource(GeoJsonSource(SOURCE_ID, collection))
            addLayer(style)
        }
    }

    private fun addLayer(style: org.maplibre.android.maps.Style) {
        style.addLayer(
            CircleLayer(LAYER_ID, SOURCE_ID).apply {
                setProperties(
                    PropertyFactory.circleRadius(
                        Expression.interpolate(
                            Expression.linear(), Expression.zoom(),
                            Expression.stop(10, 12f),
                            Expression.stop(14, 16f),
                            Expression.stop(17, 28f),
                            Expression.stop(19, 40f),
                        )
                    ),
                    PropertyFactory.circleColor(Expression.get("color")),
                    PropertyFactory.circleOpacity(
                        Expression.match(
                            Expression.get("severity"),
                            Expression.literal("LOW"), Expression.literal(0.50),
                            Expression.literal("MEDIUM"), Expression.literal(0.55),
                            Expression.literal(0.65),
                        )
                    ),
                    PropertyFactory.circleStrokeWidth(1.5f),
                    PropertyFactory.circleStrokeColor(Expression.get("color")),
                    PropertyFactory.circleStrokeOpacity(
                        Expression.match(
                            Expression.get("severity"),
                            Expression.literal("LOW"), Expression.literal(0.70),
                            Expression.literal("MEDIUM"), Expression.literal(0.75),
                            Expression.literal(0.85),
                        )
                    ),
                )
            }
        )
    }

    fun destroy() {
        mapLibreMap?.removeOnMapClickListener(clickListener)
        zoneById.clear()
        mapLibreMap = null
        clickListenerAdded = false
    }

    private fun Severity.defaultColor(): Int = when (this) {
        Severity.LOW -> 0xFFFFA726.toInt()
        Severity.MEDIUM -> 0xFFFB8C00.toInt()
        Severity.HIGH -> 0xFFE53935.toInt()
    }

    private fun Int.toHexColor(): String {
        val r = (this ushr 16) and 0xFF
        val g = (this ushr 8) and 0xFF
        val b = this and 0xFF
        return "#%02X%02X%02X".format(r, g, b)
    }
}
