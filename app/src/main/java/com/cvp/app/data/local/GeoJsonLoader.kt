package com.cvp.app.data.local

import android.content.Context
import com.cvp.app.data.local.dto.GeoJsonFeatureCollectionDto
import com.cvp.app.data.local.mapper.RiskZoneMapper
import com.cvp.app.domain.model.RiskZone
import com.cvp.app.domain.model.Severity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber

private const val ASSET_PATH = "risk_zones.geojson"

class GeoJsonLoader(
    private val context: Context,
    private val json: Json,
) : ZoneLoader {

    override suspend fun loadRiskZones(): Result<List<RiskZone>> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = context.assets.open(ASSET_PATH).bufferedReader().use { it.readText() }
            parseGeoJsonString(json, raw)
        }.onFailure {
            Timber.e(it, "GeoJsonLoader: failed to load $ASSET_PATH")
        }
    }
}

/**
 * Pure parsing function exposed as internal so unit tests can exercise it
 * without a real [Context] or asset file system.
 */
internal fun parseGeoJsonString(json: Json, raw: String): List<RiskZone> {
    val startMs = System.currentTimeMillis()
    val collection = json.decodeFromString<GeoJsonFeatureCollectionDto>(raw)
    val zones = collection.features.mapNotNull { RiskZoneMapper.fromDto(it) }
    val parseMs = System.currentTimeMillis() - startMs

    val discarded = collection.features.size - zones.size
    val low = zones.count { it.severity == Severity.LOW }
    val medium = zones.count { it.severity == Severity.MEDIUM }
    val high = zones.count { it.severity == Severity.HIGH }

    Timber.i(
        "GeoJSON loaded in ${parseMs}ms — " +
            "${zones.size} zones (LOW=$low, MEDIUM=$medium, HIGH=$high), $discarded discarded",
    )
    return zones
}
