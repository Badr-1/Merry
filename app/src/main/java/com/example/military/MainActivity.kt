package com.example.military

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.glance.appwidget.updateAll
import com.example.military.ui.theme.Brown
import com.example.military.ui.theme.DarkGreen
import com.example.military.ui.theme.LightGreen
import com.example.military.widget.MilitaryAppWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

val STARTING_DATE: Long = LocalDate.of(2023, 10, 8).toEpochDay()
val SERVICE_START_DATE: Long = LocalDate.of(2023, 12, 1).toEpochDay()
val END_DATE: Long = LocalDate.of(2024, 12, 1).toEpochDay()
val TOTAL_DAYS: Long = END_DATE - SERVICE_START_DATE
var TODAY = LocalDate.now().toEpochDay()
val DAYS_SERVED = TODAY - SERVICE_START_DATE
val DAYS_PASSED = TODAY - STARTING_DATE

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MilitaryScreen()
        }
    }
}

const val ANIMATION_DURATION = 2000

@Composable
fun YearProgress(targetProgress: Float, modifier: Modifier = Modifier) {
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = ANIMATION_DURATION), label = ""
    )
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .size(300.dp)
                .background(LightGreen, CircleShape),
            trackColor = LightGreen,
            color = Brown,
            strokeWidth = 10.dp,
            strokeCap = StrokeCap.Round,

            )
        Text(
            text = String.format(Locale.getDefault(), "%.2f", animatedProgress * 100) + "%",
            fontSize = 64.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = Brown
        )

    }
}

@Preview
@Composable
fun PreviewYearProgress() {
    YearProgress(targetProgress = 1.0f)
}

const val TAG = "MainActivity"

@Composable
fun MilitaryScreen(modifier: Modifier = Modifier) {
    var progress by remember { mutableFloatStateOf(0f) }
    var passedDaysProgress by remember { mutableIntStateOf(0) }
    var servedDaysProgress by remember { mutableIntStateOf(0) }
    var leftDaysProgress by remember { mutableIntStateOf(TOTAL_DAYS.toInt()) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            while (passedDaysProgress < DAYS_PASSED) {
                passedDaysProgress += 1
                if (DAYS_PASSED - DAYS_SERVED >= passedDaysProgress)
                    continue
                servedDaysProgress += 1
                leftDaysProgress -= 1
                progress = (servedDaysProgress * 1f / TOTAL_DAYS)
            }
        }
    }
    DisposableEffect(Unit) {
        val receiver = registerDateChangeReceiver(context) {
            TODAY = LocalDate.now().toEpochDay()
            passedDaysProgress = (TODAY - STARTING_DATE).toInt()
            servedDaysProgress = (TODAY - SERVICE_START_DATE).toInt()
            leftDaysProgress = (END_DATE - TODAY).toInt()
            progress = (servedDaysProgress * 1f / TOTAL_DAYS)
            CoroutineScope(Dispatchers.Default).launch {
                MilitaryAppWidget().updateAll(context)
            }
        }
        onDispose {
            unregisterDateChangeReceiver(context, receiver)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGreen),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Text(
            stringResource(R.string.welcome_solider),
            color = LightGreen,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(32.dp))
        YearProgress(targetProgress = progress)
        Spacer(modifier = Modifier.weight(1f))
        Details(
            leftDays = leftDaysProgress,
            passedDays = passedDaysProgress,
            servedDays = servedDaysProgress
        )
        Spacer(modifier = Modifier.weight(1f))
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
            color = LightGreen,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            stringResource(R.string.served_days, servedDaysAnimated.toString().padStart(3, '0')),
            color = LightGreen,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(8.dp)

        )
        Text(
            stringResource(R.string.left_days, leftDaysAnimated.toString().padStart(3, '0')),
            color = LightGreen,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(8.dp)
        )
    }

}

