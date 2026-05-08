package com.cvp.app.data.repository

import com.cvp.app.data.location.LocationService
import com.cvp.app.domain.model.UserLocation
import com.cvp.app.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

internal class LocationRepositoryImpl(
    private val service: LocationService,
) : LocationRepository {
    override fun observeLocation(): Flow<UserLocation> = service.observeLocation()
    override suspend fun getLastKnownLocation(): UserLocation? = service.getLastKnownLocation()
}
