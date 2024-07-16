package com.example.merry

import android.content.Context
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.merry.data.MerryDates
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
fun DatePickerState.toEpochDay(): Long {
    return Instant.ofEpochMilli(this.selectedDateMillis!!)
        .atZone(ZoneId.systemDefault())
        .toLocalDate().toEpochDay()
}
fun Long.toDateString(): String {
    return if (this.toInt() != 0) LocalDate.ofEpochDay(this)
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) else ""
}

fun Long.toMilliseconds(): Long? {
    return if (this.toInt() != 0) this * 24 * 60 * 60 * 1000 else null
}

fun isThereAnyPreferences(context: Context): Boolean {
    val sharedPref = context.getSharedPreferences("dates", Context.MODE_PRIVATE)
    return sharedPref.contains("camp_start_date")
}

fun readPreferences(context: Context): MerryDates {
    val sharedPref = context.getSharedPreferences("dates", Context.MODE_PRIVATE)
    val campStartDate = sharedPref.getLong("camp_start_date", 0)
    val serviceStartDate = sharedPref.getLong("service_start_date", 0)
    val serviceEndDate = sharedPref.getLong("service_end_date", 0)
    return MerryDates(campStartDate, serviceStartDate, serviceEndDate)
}