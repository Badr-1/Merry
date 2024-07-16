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
import com.example.merry.screens.MainScreen
import com.example.merry.screens.SettingsScreen


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
    var isSettingsVisible by remember { mutableStateOf(!isThereAnyPreferences(context)) }
    if (isSettingsVisible) {
        SettingsScreen(onUpdate = { isSettingsVisible = false })
    } else {
        MainScreen(onUpdate = { isSettingsVisible = true })
    }
}