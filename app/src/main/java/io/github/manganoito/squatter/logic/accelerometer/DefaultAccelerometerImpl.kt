package io.github.manganoito.squatter.logic.accelerometer

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class DefaultAccelerometerImpl @Inject constructor(
    private val sensorManager: SensorManager,
) : Accelerometer, SensorEventListener {
    private lateinit var accelerationSensor: Sensor

    private val _accelerationVector = MutableStateFlow<FloatArray>(floatArrayOf(0f, 0f, 0f))
    override val acceleration = MutableStateFlow<Float>(0f)

    override suspend fun report() {
        TODO()
    }

    override fun listen() {
        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)?.let {
            this.accelerationSensor = it
        }
        sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun unlisten() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.values.size != 3) return
        when (event.sensor) {
            accelerationSensor -> {
                Log.d(
                    "squatter-debug",
                    "[${event.values[0]}, ${event.values[1]}, ${event.values[2]}]"
                )
                _accelerationVector.value = event.values
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // noop
    }
}