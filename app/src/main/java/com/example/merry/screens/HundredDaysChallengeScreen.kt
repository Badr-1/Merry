package com.example.merry.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.merry.R
import com.example.merry.data.MerryDates
import com.example.merry.data.TODAY
import com.example.merry.registerDateChangeReceiver
import com.example.merry.ui.theme.MerryAppTypography
import com.example.merry.ui.theme.MerryTheme
import com.example.merry.unregisterDateChangeReceiver
import java.time.LocalDate

@Composable
fun TheHundredDaysChallengeScreen(merryDates: MerryDates, onMetricsPressed: () -> Unit) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val receiver = registerDateChangeReceiver(context) {
            TODAY = LocalDate.now().toEpochDay()
            merryDates.updateMetrics(TODAY)
        }
        onDispose {
            unregisterDateChangeReceiver(context, receiver)
        }
    }
    MerryTheme {
        Surface {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    stringResource(R.string.the_100_merry_challenge),
                    style = MerryAppTypography.displayLarge.copy(fontSize = 26.sp)
                )
                Spacer(Modifier.height(32.dp))
                for (i in 1..10) {
                    Row {
                        if ((i * 10) % 20 == 0)
                            Spacer(Modifier.width(17.dp))
                        TenDaysRow(1 + (i - 1) * 10..i * 10, 100 - merryDates.leftDays.toInt())
                    }
                    Spacer(Modifier.height(5.dp))
                }
                Spacer(modifier = Modifier.height(64.dp))
                Button(onClick = onMetricsPressed) {
                    Text(
                        text = stringResource(R.string.metrics),
                        style = MerryAppTypography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun TenDaysRow(range: IntRange, reached: Int, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
        for (i in range) {
            Day(i.toString(), i <= reached)
            Spacer(Modifier.width(4.dp))

        }
    }
}

@Composable
fun Day(text: String, checked: Boolean, modifier: Modifier = Modifier) {

    Box(
        modifier = Modifier
            .background(
                if (checked) MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surface,
                CircleShape
            )
            .size(30.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MerryAppTypography.labelMedium,
            color = if (checked) MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.primary
        )
    }
}