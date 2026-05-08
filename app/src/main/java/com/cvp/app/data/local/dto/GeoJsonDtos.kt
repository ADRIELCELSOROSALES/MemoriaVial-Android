package com.cvp.app.data.local.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeoJsonFeatureCollectionDto(
    val type: String,
    val features: List<GeoJsonFeatureDto>,
)

@Serializable
data class GeoJsonFeatureDto(
    val type: String,
    val geometry: GeoJsonGeometryDto,
    val properties: GeoJsonPropertiesDto,
)

@Serializable
data class GeoJsonGeometryDto(
    val type: String,
    /** GeoJSON coordinate order: [longitude, latitude] */
    val coordinates: List<Double>,
)

@Serializable
data class GeoJsonPropertiesDto(
    val id: String,
    @SerialName("incident_count") val incidentCount: Int? = null,
    @SerialName("leve_count") val leveCount: Int? = null,
    @SerialName("grave_count") val graveCount: Int? = null,
    @SerialName("mortal_count") val mortalCount: Int? = null,
    @SerialName("predominant_hour_range") val predominantHourRange: String? = null,
    @SerialName("predominant_weekday") val predominantWeekday: String? = null,
    @SerialName("via_type") val viaType: String? = null,
    @SerialName("predominant_victim_mode") val predominantVictimMode: String? = null,
    @SerialName("address_label") val addressLabel: String? = null,
    val comuna: String? = null,
    @SerialName("radius_meters") val radiusMeters: Double? = null,
)
