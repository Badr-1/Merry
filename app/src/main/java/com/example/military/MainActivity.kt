package com.example.military

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.military.ui.theme.Brown
import com.example.military.ui.theme.DarkGreen
import com.example.military.ui.theme.LightGreen
import java.time.LocalDate
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MilitaryScreen()
        }
    }
}

@Composable
fun YearProgress(progress: Float, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .size(300.dp)
                .background(LightGreen, CircleShape),
            trackColor = LightGreen,
            color = Brown,
            strokeWidth = 10.dp,
            strokeCap = StrokeCap.Round,

            )
        Text(
            text = String.format(Locale.getDefault(), "%.2f", progress * 100) + "%",
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
    YearProgress(progress = 1.0f)
}

@Composable
@Preview(showBackground = true)
fun MilitaryScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGreen),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Text(
            "Welcome, Solider",
            color = LightGreen,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(32.dp))
        YearProgress(progress = calculateProgress())
        Spacer(modifier = Modifier.weight(1f))
        Details(
            leftDays = stringResource(R.string.remaining_days, getLeftDays()),
            passedDays = stringResource(R.string.passed_days, getPassedDays()),
            servedDays = stringResource(R.string.served_days, getServedDays())
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun Details(
    leftDays: String,
    passedDays: String,
    servedDays: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        Text(
            passedDays,
            color = LightGreen,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            servedDays,
            color = LightGreen,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(8.dp)

        )
        Text(
            leftDays,
            color = LightGreen,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(8.dp)
        )
    }

}


fun calculateProgress(): Float {
    val startDate = LocalDate.of(2023, 12, 1)
    val today = LocalDate.now()
    val daysPassed = today.toEpochDay() - startDate.toEpochDay()
    val progress = (daysPassed / 365.0f) // Assuming 365 days in a year
    return progress.coerceIn(0f, 1f)
}

fun getLeftDays(): Int {
    val endDate = LocalDate.of(2024, 12, 1)
    val today = LocalDate.now()
    val daysRemaining = endDate.toEpochDay() - today.toEpochDay()
    return daysRemaining.toInt()
}

fun getServedDays(): Int {
    val startDate = LocalDate.of(2023, 12, 1)
    val today = LocalDate.now()
    val daysServed = today.toEpochDay() - startDate.toEpochDay()
    return daysServed.toInt()
}

fun getPassedDays(): Int {
    val endDate = LocalDate.of(2023, 10, 8)
    val today = LocalDate.now()
    val daysPassed = today.toEpochDay() - endDate.toEpochDay()
    return daysPassed.toInt()


}
