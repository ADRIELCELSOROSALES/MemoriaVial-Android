package com.cvp.app.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvp.app.data.local.preferences.OnboardingPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(preferences: OnboardingPreferences) : ViewModel() {

    // null = loading, true = onboarding done, false = show onboarding
    val onboardingCompleted: StateFlow<Boolean?> = preferences
        .isOnboardingCompleted()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )
}
