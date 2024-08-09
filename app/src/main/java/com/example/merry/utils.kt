package com.example.merry

import android.content.Context
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.merry.data.MerryDates
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

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

class Presets {
    companion object {
        fun festive(drawable: Shape.DrawableShape? = null): List<Party> {
            val party = Party(
                speed = 30f,
                maxSpeed = 50f,
                damping = 0.9f,
                angle = Angle.TOP,
                spread = 45,
                size = listOf(Size.SMALL, Size.LARGE, Size.LARGE),
                shapes = listOf(Shape.Square, Shape.Circle, drawable).filterNotNull(),
                timeToLive = 3000L,
                rotation = Rotation(),
                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(30),
                position = Position.Relative(0.5, 1.0)
            )

            return listOf(
                party,
                party.copy(
                    speed = 55f,
                    maxSpeed = 65f,
                    spread = 10,
                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(10),
                ),
                party.copy(
                    speed = 50f,
                    maxSpeed = 60f,
                    spread = 120,
                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(40),
                ),
                party.copy(
                    speed = 65f,
                    maxSpeed = 80f,
                    spread = 10,
                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(10),
                )
            )
        }

        fun explode(): List<Party> {
            val party = Party(
                speed = 0f,
                maxSpeed = 30f,
                damping = 0.9f,
                spread = 360,
                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                position = Position.Relative(0.0, 0.0)
            )
            return listOf(
                party,
                party.copy(position = Position.Relative(1.0, 0.0)),
                party.copy(position = Position.Relative(1.0, 1.0)),
                party.copy(position = Position.Relative(0.0, 1.0)),
                party.copy(position = Position.Relative(0.5, 0.5)),

                )
        }

        fun parade(): List<Party> {
            val party = Party(
                speed = 10f,
                maxSpeed = 30f,
                damping = 0.9f,
                angle = Angle.RIGHT - 45,
                spread = Spread.SMALL,
                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                emitter = Emitter(duration = 5, TimeUnit.SECONDS).perSecond(30),
                position = Position.Relative(0.0, 0.5)
            )

            return listOf(
                party,
                party.copy(
                    angle = party.angle - 90, // flip angle from right to left
                    position = Position.Relative(1.0, 0.5)
                ),
            )
        }

        fun rain(): List<Party> {
            return listOf(
                Party(
                    speed = 0f,
                    maxSpeed = 15f,
                    damping = 0.9f,
                    angle = Angle.BOTTOM,
                    spread = Spread.ROUND,
                    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                    emitter = Emitter(duration = 5, TimeUnit.SECONDS).perSecond(100),
                    position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0))
                )
            )
        }
    }
}