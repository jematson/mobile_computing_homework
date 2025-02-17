package com.example.homework4

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp


@Composable
fun TemperatureDisplay(notificationHelper: NotificationHelper) {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

    var currentTemp by remember { mutableFloatStateOf(0.0f) }

    val listener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor:Sensor?, accuracy: Int){}

        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                currentTemp = it.values[0]

                // send notification at certain temps
                if (currentTemp > 40.0f) {
                    //sendTempNotification
                    notificationHelper.createNotification("High Temperature", "Woah, it's getting awfully hot! Have some water.")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        sensorManager.registerListener(listener, tempSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    DisposableEffect(Unit) {
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Box (contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()) {
        Text(text = "Ambient Temperature",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "$currentTemp °C",
            modifier = Modifier.padding(top = 100.dp),
            style = MaterialTheme.typography.headlineLarge
        )
    }
}