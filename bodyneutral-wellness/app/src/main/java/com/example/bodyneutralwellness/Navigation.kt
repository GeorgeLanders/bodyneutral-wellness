package com.example.bodyneutralwellness

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.bodyneutralwellness.ui.dashboard.DashboardScreen
import com.example.bodyneutralwellness.ui.movement.MovementScreen
import com.example.bodyneutralwellness.ui.welcome.WelcomeScreen
import com.example.bodyneutralwellness.ui.video.VideoPlayerScreen
import com.example.bodyneutralwellness.ui.gear.GearGuideScreen
import com.example.bodyneutralwellness.ui.tracker.TrackerScreen
import com.example.bodyneutralwellness.ui.coach.CoachScreen
import com.example.bodyneutralwellness.ui.onboarding.OnboardingScreen
import com.example.bodyneutralwellness.ui.profile.ProfileScreen
import com.example.bodyneutralwellness.ui.wins.WellnessWinScreen
import com.example.bodyneutralwellness.ui.journal.JournalScreen
import com.example.bodyneutralwellness.ui.breathing.BreathingScreen
import com.example.bodyneutralwellness.ui.nourish.NourishScreen
import com.example.bodyneutralwellness.ui.settings.SettingsScreen
import com.example.bodyneutralwellness.ui.sos.SosScreen
import com.example.bodyneutralwellness.ui.community.CommunityScreen
import com.example.bodyneutralwellness.ui.breathing.BreatheCustomizerScreen
import com.example.bodyneutralwellness.ui.journal.AudioDiaryScreen
import com.example.bodyneutralwellness.data.WellnessPreferences


@Composable
fun MainNavigation() {
  val context = LocalContext.current
  val prefs = remember { WellnessPreferences(context) }
  val backStack = rememberNavBackStack(Welcome)
  val currentKey = backStack.lastOrNull()
  val showBottomBar = currentKey != null && 
      currentKey !is Welcome && 
      currentKey !is Onboarding && 
      currentKey !is VideoPlayer && 
      currentKey !is GearGuide && 
      currentKey !is WellnessWins &&
      currentKey !is Journal &&
      currentKey !is Breathing &&
      currentKey !is Nourish &&
      currentKey !is Settings &&
      currentKey !is Sos &&
      currentKey !is Community &&
      currentKey !is BreatheCustomizer &&
      currentKey !is AudioDiary

  Scaffold(
      bottomBar = {
          if (showBottomBar) {
              val navColors = NavigationBarItemDefaults.colors(
                  selectedIconColor = MaterialTheme.colorScheme.primary,
                  selectedTextColor = MaterialTheme.colorScheme.primary,
                  indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.82f),
                  unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                  unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
              )
              NavigationBar(
                  containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                  tonalElevation = 6.dp
              ) {
                  NavigationBarItem(
                      selected = currentKey == Dashboard,
                      onClick = { 
                          if (currentKey != Dashboard) {
                              backStack.removeLastOrNull()
                              backStack.add(Dashboard) 
                          }
                      },
                      icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                      label = { Text("Home") },
                      colors = navColors
                  )
                  NavigationBarItem(
                      selected = currentKey == Movement,
                      onClick = { 
                          if (currentKey != Movement) {
                              backStack.removeLastOrNull()
                              backStack.add(Movement) 
                          }
                      },
                      icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Movement") },
                      label = { Text("Move") },
                      colors = navColors
                  )
                  NavigationBarItem(
                      selected = currentKey == Tracker,
                      onClick = { 
                          if (currentKey != Tracker) {
                              backStack.removeLastOrNull()
                              backStack.add(Tracker) 
                          }
                      },
                      icon = { Icon(Icons.Default.Favorite, contentDescription = "Track") },
                      label = { Text("Track") },
                      colors = navColors
                  )
                  NavigationBarItem(
                      selected = currentKey == Coach,
                      onClick = { 
                          if (currentKey != Coach) {
                              backStack.removeLastOrNull()
                              backStack.add(Coach) 
                          }
                      },
                      icon = { Icon(Icons.Default.Face, contentDescription = "Coach") },
                      label = { Text("Coach") },
                      colors = navColors
                  )
                  NavigationBarItem(
                      selected = currentKey == Profile,
                      onClick = { 
                          if (currentKey != Profile) {
                              backStack.removeLastOrNull()
                              backStack.add(Profile) 
                          }
                      },
                      icon = { Icon(Icons.Default.Person, contentDescription = "You") },
                      label = { Text("You") },
                      colors = navColors
                  )
              }
          }
      }
  ) { innerPadding ->
      Box(modifier = Modifier.padding(innerPadding)) {
          NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider =
              entryProvider {
                entry<Welcome> {
                  WelcomeScreen(
                    onNavigateToMain = { 
                       backStack.removeLastOrNull()
                       if (prefs.onboardingDone) {
                           backStack.add(Dashboard)
                       } else {
                           backStack.add(Onboarding)
                       }
                    }, 
                    modifier = Modifier.safeDrawingPadding()
                  )
                }
                entry<Onboarding> {
                  OnboardingScreen(
                    onComplete = {
                       backStack.removeLastOrNull()
                       backStack.add(Dashboard)
                    },
                    modifier = Modifier.safeDrawingPadding()
                  )
                }
                entry<Dashboard> {
                  DashboardScreen(onItemClick = { navKey -> backStack.add(navKey) }, modifier = Modifier.safeDrawingPadding())
                }
                entry<Movement> {
                  MovementScreen(onItemClick = { navKey -> backStack.add(navKey) }, modifier = Modifier.safeDrawingPadding())
                }
                entry<Tracker> {
                  TrackerScreen(modifier = Modifier.safeDrawingPadding())
                }
                entry<Coach> {
                  CoachScreen(
                    modifier = Modifier.safeDrawingPadding(),
                    onNavigateToSos = { backStack.add(Sos) },
                    onNavigateToBreathe = { backStack.add(Breathing) },
                    onNavigateToJournal = { backStack.add(Journal) }
                  )
                }
                entry<Profile> {
                  ProfileScreen(
                    onNavigateToSettings = { backStack.add(Settings) },
                    modifier = Modifier.safeDrawingPadding()
                  )
                }
                entry<WellnessWins> {
                  WellnessWinScreen(
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                  )
                }
                entry<GearGuide> {
                  GearGuideScreen(
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                  )
                }
                entry<VideoPlayer> { video ->
                  VideoPlayerScreen(
                    videoName = video.videoName,
                    videoResId = video.videoResId,
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                  )
                }
                entry<Journal> {
                  JournalScreen(
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                  )
                }
                entry<Breathing> {
                  BreathingScreen(
                    onBack = { backStack.removeLastOrNull() },
                    onNavigateToCustomizer = { backStack.add(BreatheCustomizer) },
                    modifier = Modifier.safeDrawingPadding()
                  )
                }
                entry<Nourish> {
                  NourishScreen(
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                  )
                }
                entry<Settings> {
                  SettingsScreen(
                    onBack = { backStack.removeLastOrNull() },
                    onResetOnboarding = {
                      backStack.clear()
                      backStack.add(Welcome)
                    },
                    modifier = Modifier.safeDrawingPadding()
                  )
                }
                entry<Sos> {
                  SosScreen(
                    onBack = { backStack.removeLastOrNull() },
                    onNavigateToBreathe = {
                      backStack.removeLastOrNull()
                      backStack.add(Breathing)
                    },
                    onNavigateToCoach = {
                      backStack.removeLastOrNull()
                      backStack.add(Coach)
                    },
                    modifier = Modifier.safeDrawingPadding()
                  )
                }
                entry<Community> {
                  CommunityScreen(
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                  )
                }
                entry<BreatheCustomizer> {
                  BreatheCustomizerScreen(
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                  )
                }
                entry<AudioDiary> {
                  AudioDiaryScreen(
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.safeDrawingPadding()
                  )
                }
              },
          )
      }
  }
}
