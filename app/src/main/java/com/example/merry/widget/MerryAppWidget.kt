package com.example.merry.widget

import android.content.Context
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.merry.isThereAnyPreferences
import com.example.merry.readPreferences
import com.example.merry.ui.theme.DarkGreen
import com.example.merry.ui.theme.LightGreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.time.LocalDate

class DaysLeftDataStore(private val context: Context) : DataStore<Long> {
    override val data: Flow<Long>
        get() = flow {
            emit(
                if (isThereAnyPreferences(context))
                    readPreferences(context).serviceEndDate - LocalDate.now().toEpochDay()
                else
                    0
            )
        }

    override suspend fun updateData(transform: suspend (t: Long) -> Long): Long {
        TODO("Not yet implemented")
    }

}

class MerryAppWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<Long>
        get() = object : GlanceStateDefinition<Long> {
            override suspend fun getDataStore(context: Context, fileKey: String): DataStore<Long> {
                return DaysLeftDataStore(context)
            }

            override fun getLocation(context: Context, fileKey: String): File {
                TODO("Not yet implemented")
            }
        }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                Scaffold(
                    backgroundColor = ColorProvider(DarkGreen),
                    titleBar = {}
                ) {
                    val textStyle = TextStyle(
                        color = ColorProvider(LightGreen),
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Box(
                        GlanceModifier.fillMaxSize().background(ColorProvider(DarkGreen)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            currentState<Long>().toString().padStart(3, '0'),
                            style = textStyle
                        )
                    }


//                    Column(
//                        modifier = GlanceModifier.fillMaxSize(),
//                        verticalAlignment = Alignment.Vertical.CenterVertically
//
//                    ) {
//                        Text(
//                            context.resources.getString(
//                                R.string.passed_days, DAYS_PASSED.toString().padStart(3, '0'))
//                            ,
//                            style = textStyle
//                        )
//                        Text(
//                            context.resources.getString(
//                                R.string.served_days,
//                                DAYS_SERVED.toString().padStart(3, '0')
//                            ),
//                            style = textStyle
//                        )
//                        Text(
//                            context.resources.getString(
//                                R.string.left_days,
//                                DAYS_LEFT.toString().padStart(3, '0')
//                            ),
//                            style = textStyle
//                        )
//                    }
                }
            }
        }
    }
}