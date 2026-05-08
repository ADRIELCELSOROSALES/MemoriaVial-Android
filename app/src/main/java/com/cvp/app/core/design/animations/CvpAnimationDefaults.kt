package com.cvp.app.core.design.animations

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing

/**
 * Shared animation constants for CVP.
 *
 * Centralizing durations and easings ensures that motion feels
 * consistent regardless of which screen or component is animating.
 */
object CvpAnimationDefaults {

    /** Quick micro-interactions: icon state changes, ripples. */
    const val shortDuration = 150

    /** Standard transitions: expanding cards, slide-ins. */
    const val mediumDuration = 250

    /** Elaborate entries: bottom-sheet, full-screen modal. */
    const val longDuration = 400

    /**
     * Standard easing for the majority of transitions.
     * Accelerates quickly and decelerates smoothly — natural for most UI motion.
     */
    val standardEasing: Easing = FastOutSlowInEasing

    /**
     * Emphasized easing for elements entering from outside the viewport.
     * Matches Material Motion's "Emphasized" curve for entrances:
     * starts fast, overshoots slightly, settles with a cushion.
     */
    val emphasizedEasing: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
}
