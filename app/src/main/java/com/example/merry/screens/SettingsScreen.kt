package com.example.merry.screens

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.merry.R
import com.example.merry.readPreferences
import com.example.merry.toDateString
import com.example.merry.toEpochDay
import com.example.merry.toMilliseconds
import com.example.merry.ui.theme.MerryTheme
import com.example.merry.ui.theme.MerryAppTypography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onUpdate: () -> Unit) {
    val context = LocalContext.current
    val merryDates = readPreferences(context)
    var campStartDate by remember { mutableStateOf(merryDates.campStartDate.toDateString()) }
    val campStartState by remember {
        mutableStateOf(
            DatePickerState(
                Locale.getDefault(),
                merryDates.campStartDate.toMilliseconds()
            )
        )
    }
    var serviceStartDate by remember { mutableStateOf(merryDates.serviceStartDate.toDateString()) }
    val serviceStartState by remember {
        mutableStateOf(
            DatePickerState(
                Locale.getDefault(),
                merryDates.serviceStartDate.toMilliseconds()
            )
        )
    }
    var serviceEndDate by remember { mutableStateOf(merryDates.serviceEndDate.toDateString()) }
    val serviceEndState by remember {
        mutableStateOf(
            DatePickerState(
                Locale.getDefault(),
                merryDates.serviceEndDate.toMilliseconds()
            )
        )
    }
    MerryTheme {
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
                    onValueChange = { campStartDate = it },
                )
                Spacer(Modifier.height(16.dp))
                DatePickerTextField(
                    label = R.string.service_start_date,
                    datePickerState = serviceStartState,
                    value = serviceStartDate,
                    onValueChange = { serviceStartDate = it },
                )
                Spacer(Modifier.height(16.dp))
                DatePickerTextField(
                    label = R.string.service_end_date,
                    datePickerState = serviceEndState,
                    value = serviceEndDate,
                    onValueChange = { serviceEndDate = it },
                )
                Spacer(Modifier.height(32.dp))
                Button(enabled = campStartState.selectedDateMillis != null && serviceStartState.selectedDateMillis != null && serviceEndState.selectedDateMillis != null,
                    onClick = {
                        val sharedPref = context.getSharedPreferences("dates", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putLong("camp_start_date", campStartState.toEpochDay())
                        editor.putLong("service_start_date", serviceStartState.toEpochDay())
                        editor.putLong("service_end_date", serviceEndState.toEpochDay())
                        editor.apply()
                        onUpdate()
                    }) {
                    Text(
                        text = stringResource(R.string.done),
                        style = MerryAppTypography.bodyLarge
                    )
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerTextField(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    datePickerState: DatePickerState,
    value: String,
    onValueChange: (String) -> Unit,
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
                {
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