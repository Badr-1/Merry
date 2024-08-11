package com.example.merry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
    var screen by rememberSaveable {
        mutableStateOf(
            if (!isThereAnyPreferences(context))
                Screens.SETTINGS
            else {
                if (merryDates.leftDays <= 0)
                    Screens.FINISHING
                else if (merryDates.leftDays <= 100)
                    Screens.CHALLENGE
                else
                    Screens.MAIN
            }
        )
    }
    when (screen) {
        Screens.MAIN -> MainScreen(
            onSettingsPressed = { screen = Screens.SETTINGS },
            onChallengePressed = { screen = Screens.CHALLENGE }
        )
        Screens.SETTINGS -> SettingsScreen(
            onUpdate = {
                screen = Screens.MAIN
            }
        )
        Screens.CHALLENGE -> TheHundredDaysChallengeScreen(
            merryDates,
            onMetricsPressed = { screen = Screens.MAIN }
        )
        Screens.FINISHING -> FinishingScreen()
    }
}