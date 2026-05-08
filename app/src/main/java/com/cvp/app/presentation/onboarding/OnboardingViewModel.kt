package com.cvp.app.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvp.app.data.local.preferences.OnboardingPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface OnboardingNavEvent {
    data object NavigateToMap : OnboardingNavEvent
}

class OnboardingViewModel(
    private val preferences: OnboardingPreferences,
) : ViewModel() {

    private val _navEvents = Channel<OnboardingNavEvent>()
    val navEvents = _navEvents.receiveAsFlow()

    fun onCompleteOnboarding() {
        viewModelScope.launch {
            preferences.setOnboardingCompleted()
            _navEvents.send(OnboardingNavEvent.NavigateToMap)
        }
    }

    fun onSkip() = onCompleteOnboarding()
}
