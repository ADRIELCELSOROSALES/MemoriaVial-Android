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

        // 3 colores × 3 tamaños = 9 layers independientes
        // Rojo (mortal ≥ 1): tamaño por mortalCount
        private const val LAYER_RED_SM    = "risk-red-sm"
        private const val LAYER_RED_MD    = "risk-red-md"
        private const val LAYER_RED_LG    = "risk-red-lg"
        // Naranja (grave ≥ 1, mortal = 0): tamaño por graveCount
        private const val LAYER_ORANGE_SM = "risk-orange-sm"
        private const val LAYER_ORANGE_MD = "risk-orange-md"
        private const val LAYER_ORANGE_LG = "risk-orange-lg"
        // Amarillo (solo leves): tamaño por incident_count
        private const val LAYER_YELLOW_SM = "risk-yellow-sm"
        private const val LAYER_YELLOW_MD = "risk-yellow-md"
        private const val LAYER_YELLOW_LG = "risk-yellow-lg"

        private val ALL_LAYERS = arrayOf(
            LAYER_RED_SM,    LAYER_RED_MD,    LAYER_RED_LG,
            LAYER_ORANGE_SM, LAYER_ORANGE_MD, LAYER_ORANGE_LG,
            LAYER_YELLOW_SM, LAYER_YELLOW_MD, LAYER_YELLOW_LG,
        )

        private const val COLOR_RED    = "#E53935"
        private const val COLOR_ORANGE = "#FB8C00"
        private const val COLOR_YELLOW = "#FFA726"

        // Curvas de radio por zoom: [z9, z12, z14, z16, z18]
        private val RADIUS_SM = floatArrayOf( 3f,  8f, 14f, 22f,  34f)
        private val RADIUS_MD = floatArrayOf( 8f, 18f, 30f, 48f,  72f)
        private val RADIUS_LG = floatArrayOf(16f, 34f, 56f, 82f, 115f)
    }

    private val clickListener = MapLibreMap.OnMapClickListener { latLng ->
        val map = mapLibreMap ?: return@OnMapClickListener false
        val screenPoint = map.projection.toScreenLocation(latLng)
        val features = map.queryRenderedFeatures(
            android.graphics.PointF(screenPoint.x, screenPoint.y),
            *ALL_LAYERS,
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
                Feature.fromGeometry(
                    Point.fromLngLat(zone.longitude, zone.latitude),
                    JsonObject().apply {
                        addProperty("zone_id",       zone.id)
                        addProperty("mortal_count",  zone.mortalCount)
                        addProperty("grave_count",   zone.graveCount)
                        addProperty("incident_count", zone.incidentCount)
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
            addLayers(style)
        }
    }

    private fun addLayers(style: org.maplibre.android.maps.Style) {
        val mortal   = Expression.get("mortal_count")
        val grave    = Expression.get("grave_count")
        val incident = Expression.get("incident_count")
        val zero     = Expression.literal(0)
        val noMortal = Expression.eq(mortal, zero)
        val noGrave  = Expression.eq(grave,  zero)

        // ── ROJO: mortal ≥ 1 ─────────────────────────────────────────────────
        // SM: exactamente 1 mortal
        addLayer(style, LAYER_RED_SM,
            Expression.eq(mortal, Expression.literal(1)),
            COLOR_RED, RADIUS_SM, 0.75f)
        // MD: 2–4 mortales
        addLayer(style, LAYER_RED_MD,
            Expression.all(
                Expression.gte(mortal, Expression.literal(2)),
                Expression.lte(mortal, Expression.literal(4)),
            ),
            COLOR_RED, RADIUS_MD, 0.82f)
        // LG: 5+ mortales
        addLayer(style, LAYER_RED_LG,
            Expression.gte(mortal, Expression.literal(5)),
            COLOR_RED, RADIUS_LG, 0.88f)

        // ── NARANJA: grave ≥ 1, mortal = 0 ───────────────────────────────────
        // SM: 1–2 graves
        addLayer(style, LAYER_ORANGE_SM,
            Expression.all(noMortal,
                Expression.gte(grave, Expression.literal(1)),
                Expression.lte(grave, Expression.literal(2)),
            ),
            COLOR_ORANGE, RADIUS_SM, 0.62f)
        // MD: 3–5 graves
        addLayer(style, LAYER_ORANGE_MD,
            Expression.all(noMortal,
                Expression.gte(grave, Expression.literal(3)),
                Expression.lte(grave, Expression.literal(5)),
            ),
            COLOR_ORANGE, RADIUS_MD, 0.70f)
        // LG: 6+ graves
        addLayer(style, LAYER_ORANGE_LG,
            Expression.all(noMortal,
                Expression.gte(grave, Expression.literal(6)),
            ),
            COLOR_ORANGE, RADIUS_LG, 0.78f)

        // ── AMARILLO: solo leves (mortal = 0, grave = 0) ─────────────────────
        // SM: < 10 siniestros
        addLayer(style, LAYER_YELLOW_SM,
            Expression.all(noMortal, noGrave,
                Expression.lt(incident, Expression.literal(10)),
            ),
            COLOR_YELLOW, RADIUS_SM, 0.48f)
        // MD: 10–29 siniestros
        addLayer(style, LAYER_YELLOW_MD,
            Expression.all(noMortal, noGrave,
                Expression.gte(incident, Expression.literal(10)),
                Expression.lt(incident,  Expression.literal(30)),
            ),
            COLOR_YELLOW, RADIUS_MD, 0.55f)
        // LG: 30+ siniestros
        addLayer(style, LAYER_YELLOW_LG,
            Expression.all(noMortal, noGrave,
                Expression.gte(incident, Expression.literal(30)),
            ),
            COLOR_YELLOW, RADIUS_LG, 0.62f)
    }

    private fun addLayer(
        style: org.maplibre.android.maps.Style,
        id: String,
        filter: Expression,
        color: String,
        radii: FloatArray,
        opacity: Float,
    ) {
        style.addLayer(CircleLayer(id, SOURCE_ID).apply {
            setFilter(filter)
            setProperties(
                PropertyFactory.circleRadius(
                    Expression.interpolate(Expression.linear(), Expression.zoom(),
                        Expression.stop(9,  radii[0]),
                        Expression.stop(12, radii[1]),
                        Expression.stop(14, radii[2]),
                        Expression.stop(16, radii[3]),
                        Expression.stop(18, radii[4]),
                    )
                ),
                PropertyFactory.circleBlur(blurByZoom()),
                PropertyFactory.circleColor(color),
                PropertyFactory.circleOpacity(opacity),
                PropertyFactory.circleStrokeWidth(strokeByZoom()),
                PropertyFactory.circleStrokeColor(color),
                PropertyFactory.circleStrokeOpacity(minOf(opacity + 0.15f, 0.95f)),
            )
        })
    }

    // Borroso al alejarse → nítido al acercarse
    private fun blurByZoom(): Expression =
        Expression.interpolate(Expression.linear(), Expression.zoom(),
            Expression.stop(9,  1.2f),
            Expression.stop(12, 0.6f),
            Expression.stop(13, 0.2f),
            Expression.stop(14, 0.0f),
        )

    // Borde invisible lejos, crece al acercarse
    private fun strokeByZoom(): Expression =
        Expression.interpolate(Expression.linear(), Expression.zoom(),
            Expression.stop(9,  0.0f),
            Expression.stop(13, 1.0f),
            Expression.stop(16, 2.5f),
        )

    fun destroy() {
        mapLibreMap?.removeOnMapClickListener(clickListener)
        zoneById.clear()
        mapLibreMap = null
        clickListenerAdded = false
    }
}
