package com.example.military

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.glance.appwidget.updateAll
import com.example.military.ui.theme.MilitaryTheme
import com.example.military.widget.MilitaryAppWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.example.military.ui.theme.MilitaryAppTypography
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.Locale

val STARTING_DATE: Long = LocalDate.of(2023, 10, 8).toEpochDay()
val SERVICE_START_DATE: Long = LocalDate.of(2023, 12, 1).toEpochDay()
val END_DATE: Long = LocalDate.of(2024, 12, 1).toEpochDay()
val TOTAL_DAYS: Long = END_DATE - SERVICE_START_DATE
var TODAY = LocalDate.now().toEpochDay()
val DAYS_SERVED = TODAY - SERVICE_START_DATE
val DAYS_PASSED = TODAY - STARTING_DATE
const val ANIMATION_DURATION = 2000

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MilitaryScreen()
//            SettingsScreen()
        }
    }
}


@Preview
@Preview(name = "darkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MilitaryScreen() {
    MilitaryTheme {
        Surface {
            MilitaryContent()
        }
    }
}

@Composable
fun MilitaryContent() {

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
                style = MilitaryAppTypography.displayLarge,
            )

        }
    }
}

@Preview
@Composable
fun PreviewYearProgress() {
    YearProgress(targetProgress = 1.0f)
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
            style = MilitaryAppTypography.headlineLarge,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            stringResource(R.string.served_days, servedDaysAnimated.toString().padStart(3, '0')),
            style = MilitaryAppTypography.headlineLarge,
            modifier = Modifier.padding(8.dp)

        )
        Text(
            stringResource(R.string.left_days, leftDaysAnimated.toString().padStart(3, '0')),
            style = MilitaryAppTypography.headlineLarge,
            modifier = Modifier.padding(8.dp)
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerTextField(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    datePickerState: DatePickerState,
    value: String,
    onValueChange: (String) -> Unit
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                Button(onClick =
                {   //TODO Take User Selected Date
                    onValueChange(dateFormatter.format(Date(datePickerState.selectedDateMillis!!)))
                    showDatePicker = false
                }
                ) {
                    Text(stringResource(R.string.select_date))
                }
            },
            dismissButton = {
                Button(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            content = {
                DatePicker(datePickerState)
            }
        )
    }

    TextField(
        modifier = modifier,
        value = value,
        onValueChange = {}, // Prevent direct text input
        readOnly = true,
        label = { Text(stringResource(label)) },
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Filled.DateRange, contentDescription = "Select Date")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Preview(name = "darkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingsScreen() {
    var campStartDate by remember { mutableStateOf("") }
    val campStartState by remember { mutableStateOf(DatePickerState(Locale.getDefault())) }
    var serviceStartDate by remember { mutableStateOf("") }
    val serviceStartState by remember { mutableStateOf(DatePickerState(Locale.getDefault())) }
    var serviceEndDate by remember { mutableStateOf("") }
    val serviceEndState by remember { mutableStateOf(DatePickerState(Locale.getDefault())) }
    MilitaryTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                DatePickerTextField(
                    label = R.string.camp_start_date,
                    datePickerState = campStartState,
                    value = campStartDate,
                    onValueChange = { campStartDate = it }
                )
                Spacer(Modifier.height(16.dp))
                DatePickerTextField(
                    label = R.string.service_start_date,
                    datePickerState = serviceStartState,
                    value = serviceStartDate,
                    onValueChange = { serviceStartDate = it }
                )
                Spacer(Modifier.height(16.dp))
                DatePickerTextField(
                    label = R.string.service_end_date,
                    datePickerState = serviceEndState,
                    value = serviceEndDate,
                    onValueChange = { serviceEndDate = it }
                )
                Spacer(Modifier.height(32.dp))
                Button(onClick = {
                    //TODO add user dates
                }) {
                    Text(
                        text = stringResource(R.string.done),
                        style = MilitaryAppTypography.bodyLarge
                    )
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun DatePickerState.toEpochDay(): Long {
    return Instant
        .ofEpochMilli(this.selectedDateMillis!!)
        .atZone(ZoneId.systemDefault())
        .toLocalDate().toEpochDay()
}