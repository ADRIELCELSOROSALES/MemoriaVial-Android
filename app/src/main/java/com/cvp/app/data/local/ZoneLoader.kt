package com.cvp.app.data.local

import com.cvp.app.domain.model.RiskZone

/**
 * Internal abstraction over the source of zone data, making [RiskZoneRepositoryImpl] testable
 * without a real Android context. [GeoJsonLoader] is the production implementation.
 */
internal fun interface ZoneLoader {
    suspend fun loadRiskZones(): Result<List<RiskZone>>
}
