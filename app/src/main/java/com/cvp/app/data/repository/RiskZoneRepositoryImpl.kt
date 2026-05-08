package com.cvp.app.data.repository

import com.cvp.app.data.local.ZoneLoader
import com.cvp.app.domain.model.RiskZone
import com.cvp.app.domain.repository.RiskZoneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

internal class RiskZoneRepositoryImpl(
    private val loader: ZoneLoader,
) : RiskZoneRepository {

    private val cache = MutableStateFlow<List<RiskZone>>(emptyList())
    private val mutex = Mutex()

    override suspend fun getAllZones(): List<RiskZone> {
        if (cache.value.isNotEmpty()) return cache.value
        return loadAndCache()
    }

    override suspend fun refresh() {
        mutex.withLock {
            loader.loadRiskZones()
                .onSuccess { cache.value = it }
                .onFailure { Timber.e(it, "Failed to reload risk zones") }
        }
    }

    private suspend fun loadAndCache(): List<RiskZone> {
        if (cache.value.isNotEmpty()) return cache.value
        return mutex.withLock {
            // Double-checked: another coroutine may have populated the cache while we waited.
            if (cache.value.isNotEmpty()) return@withLock cache.value
            val result = loader.loadRiskZones()
            result.onSuccess { cache.value = it }
                .onFailure { Timber.e(it, "Failed to load risk zones") }
            // Propagate the failure so callers can surface a meaningful error state.
            result.getOrThrow()
        }
    }
}
