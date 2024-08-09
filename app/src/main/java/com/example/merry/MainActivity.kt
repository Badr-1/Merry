package com.example.merry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.merry.screens.FinishingScreen
import com.example.merry.screens.MainScreen
import com.example.merry.screens.SettingsScreen
import com.example.merry.screens.TheHundredDaysChallengeScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MerryApp()
        }
    }
}

@Composable
fun MerryApp() {
    val context = LocalContext.current
    val merryDates = readPreferences(context)
    var isSettingsVisible by remember { mutableStateOf(!isThereAnyPreferences(context)) }
    var viewChallenge by remember { mutableStateOf(false) }
    if (merryDates.leftDays <= 0)
        FinishingScreen()
    else {
        if (isSettingsVisible) {
            SettingsScreen(onUpdate = { isSettingsVisible = false })
        } else if (merryDates.leftDays <= 100 && viewChallenge) {
            TheHundredDaysChallengeScreen(merryDates, onMetricsPressed = { viewChallenge = false })
        } else {
            MainScreen(
                onSettingsPressed = { isSettingsVisible = true },
                onChallengePressed = { viewChallenge = true })
        }
    }
}