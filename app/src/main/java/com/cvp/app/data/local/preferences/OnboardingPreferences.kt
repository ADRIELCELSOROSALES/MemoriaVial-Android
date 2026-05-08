package com.cvp.app.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "cvp_prefs")

class OnboardingPreferences(private val context: Context) {

    fun isOnboardingCompleted(): Flow<Boolean> = context.dataStore.data
        .catch { cause -> if (cause is IOException) emit(emptyPreferences()) else throw cause }
        .map { prefs -> prefs[ONBOARDING_COMPLETED] ?: false }

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { prefs -> prefs[ONBOARDING_COMPLETED] = true }
    }

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
}
