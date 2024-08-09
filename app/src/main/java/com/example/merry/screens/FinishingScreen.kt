package com.example.merry.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.merry.Presets
import com.example.merry.R
import com.example.merry.ui.theme.MerryAppTypography
import com.example.merry.ui.theme.MerryTheme
import nl.dionsegijn.konfetti.compose.KonfettiView


@Preview
@Composable
fun FinishingScreen(modifier: Modifier = Modifier) {
    MerryTheme {
        Surface {
            Column(modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
                ) {
                Text(
                    text = stringResource(R.string.congrats_title),
                    style = MerryAppTypography.titleLarge
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    stringResource(R.string.congrats_subtitle),
                    style = MerryAppTypography.titleMedium
                )
            }
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = Presets.explode()
            )
        }
    }
}