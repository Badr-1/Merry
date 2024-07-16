package com.example.merry

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class DateChangeReceiver(private val onDateChanged: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_DATE_CHANGED || intent?.action == Intent.ACTION_TIME_CHANGED || intent?.action == Intent.ACTION_TIMEZONE_CHANGED)
            onDateChanged()
    }
}

fun registerDateChangeReceiver(context: Context, onDateChanged: () -> Unit): DateChangeReceiver {
    val receiver = DateChangeReceiver(onDateChanged)
    val filter = IntentFilter().apply {
        addAction(Intent.ACTION_DATE_CHANGED)
        addAction(Intent.ACTION_TIME_CHANGED)
        addAction(Intent.ACTION_TIMEZONE_CHANGED)
    }
    context.registerReceiver(receiver, filter)
    return receiver
}

fun unregisterDateChangeReceiver(context: Context, receiver: DateChangeReceiver) {
    context.unregisterReceiver(receiver)
}
