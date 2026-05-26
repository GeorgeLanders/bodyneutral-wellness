package com.example.bodyneutralwellness

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Welcome : NavKey
@Serializable data object Onboarding : NavKey
@Serializable data object Dashboard : NavKey
@Serializable data object Movement : NavKey
@Serializable data object Tracker : NavKey
@Serializable data object Coach : NavKey
@Serializable data object Profile : NavKey
@Serializable data object WellnessWins : NavKey
@Serializable data object GearGuide : NavKey
@Serializable data class VideoPlayer(val videoName: String, val videoResId: Int) : NavKey

@Serializable data object Journal : NavKey
@Serializable data object Breathing : NavKey
@Serializable data object Nourish : NavKey
@Serializable data object Settings : NavKey

@Serializable data object Sos : NavKey
@Serializable data object Community : NavKey
@Serializable data object BreatheCustomizer : NavKey
@Serializable data object AudioDiary : NavKey


