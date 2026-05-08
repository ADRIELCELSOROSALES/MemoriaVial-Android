package com.cvp.app.domain.repository

import com.cvp.app.domain.model.RiskZone

interface RiskZoneRepository {
    suspend fun getAllZones(): List<RiskZone>
    suspend fun refresh()
}
