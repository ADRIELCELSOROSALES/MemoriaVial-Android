package com.cvp.app.domain.repository

import com.cvp.app.domain.model.UserLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun observeLocation(): Flow<UserLocation>
    suspend fun getLastKnownLocation(): UserLocation?
}
