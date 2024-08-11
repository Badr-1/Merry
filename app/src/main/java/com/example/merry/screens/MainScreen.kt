package com.example.merry.screens

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.updateAll
import com.example.merry.R
import com.example.merry.data.ANIMATION_DURATION
import com.example.merry.data.TODAY
import com.example.merry.readPreferences
import com.example.merry.registerDateChangeReceiver
import com.example.merry.ui.theme.MerryTheme
import com.example.merry.ui.theme.MerryAppTypography
import com.example.merry.unregisterDateChangeReceiver
import com.example.merry.widget.MerryAppWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

@Composable
fun MainScreen(onSettingsPressed: () -> Unit, onChallengePressed: () -> Unit) {
    val context = LocalContext.current
    val merryDates = readPreferences(context)
    var progress by remember { mutableFloatStateOf(0f) }
    var passedDaysProgress by remember { mutableIntStateOf(0) }
    var servedDaysProgress by remember { mutableIntStateOf(0) }
    var leftDaysProgress by remember { mutableIntStateOf(merryDates.totalDays.toInt()) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            while (passedDaysProgress < merryDates.passedDays) {
                passedDaysProgress += 1
                if (merryDates.passedDays - merryDates.servedDays >= passedDaysProgress)
                    continue
                servedDaysProgress += 1
                leftDaysProgress -= 1
                progress = (servedDaysProgress * 1f / merryDates.totalDays)
            }
        }
    }
    DisposableEffect(Unit) {
        val receiver = registerDateChangeReceiver(context) {
            TODAY = LocalDate.now().toEpochDay()
            merryDates.updateMetrics(TODAY)
            passedDaysProgress = merryDates.passedDays.toInt()
            servedDaysProgress = merryDates.servedDays.toInt()
            leftDaysProgress = merryDates.leftDays.toInt()
            progress = (servedDaysProgress * 1f / merryDates.totalDays)
            if(leftDaysProgress <= 100)
                onChallengePressed()
            CoroutineScope(Dispatchers.Default).launch {
                MerryAppWidget().updateAll(context)
            }
        }
        onDispose {
            unregisterDateChangeReceiver(context, receiver)
        }
    }

    MerryTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(64.dp))
                YearProgress(targetProgress = progress)
                Spacer(modifier = Modifier.weight(1f))
                Details(
                    leftDays = leftDaysProgress,
                    passedDays = passedDaysProgress,
                    servedDays = servedDaysProgress
                )
                Spacer(modifier = Modifier.height(64.dp))
                Button(onClick = onSettingsPressed) {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MerryAppTypography.bodyLarge
                    )
                }
                if(merryDates.leftDays <= 100) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onChallengePressed) {
                        Text(
                            text = stringResource(R.string.challenge),
                            style = MerryAppTypography.bodyLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }

}

@Composable
fun YearProgress(targetProgress: Float, modifier: Modifier = Modifier) {
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = ANIMATION_DURATION), label = ""
    )

    Surface {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .size(300.dp),
                strokeWidth = 15.dp,
                strokeCap = StrokeCap.Round,

                )
            Text(
                text = String.format(Locale.getDefault(), "%.2f", animatedProgress * 100) + "%",
                style = MerryAppTypography.displayLarge,
            )

        }
    }
}

@Composable
fun Details(
    leftDays: Int,
    passedDays: Int,
    servedDays: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val leftDaysAnimated by animateIntAsState(
            targetValue = leftDays,
            animationSpec = tween(durationMillis = ANIMATION_DURATION), label = "days_left"
        )
        val passedDaysAnimated by animateIntAsState(
            targetValue = passedDays,
            animationSpec = tween(durationMillis = ANIMATION_DURATION), label = "days_passed"
        )
        val servedDaysAnimated by animateIntAsState(
            targetValue = servedDays,
            animationSpec = tween(durationMillis = ANIMATION_DURATION), label = "days_served"
        )
        Text(
            stringResource(
                id = R.string.passed_days,
                passedDaysAnimated.toString().padStart(3, '0')
            ),
            style = MerryAppTypography.headlineLarge,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            stringResource(R.string.served_days, servedDaysAnimated.toString().padStart(3, '0')),
            style = MerryAppTypography.headlineLarge,
            modifier = Modifier.padding(8.dp)

        )
        Text(
            stringResource(R.string.left_days, leftDaysAnimated.toString().padStart(3, '0')),
            style = MerryAppTypography.headlineLarge,
            modifier = Modifier.padding(8.dp)
        )
    }

}