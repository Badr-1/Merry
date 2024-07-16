package com.example.merry.data

import java.time.LocalDate

var TODAY = LocalDate.now().toEpochDay()
const val ANIMATION_DURATION = 2000

data class MerryDates(
    val campStartDate: Long,
    val serviceStartDate: Long,
    val serviceEndDate: Long
) {
    val totalDays: Long = serviceEndDate - serviceStartDate
    var passedDays: Long = TODAY - campStartDate
    var servedDays: Long = TODAY - serviceStartDate

    var leftDays: Long = totalDays - servedDays
    fun updateMetrics(today: Long) {
        passedDays = today - campStartDate
        servedDays = today - serviceStartDate
        leftDays = totalDays - servedDays
    }
}