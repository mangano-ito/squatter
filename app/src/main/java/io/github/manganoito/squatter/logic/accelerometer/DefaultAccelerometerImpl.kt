package io.github.manganoito.squatter.logic.accelerometer

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

class DefaultAccelerometerImpl @Inject constructor(
    private val sensorManager: SensorManager,
) : Accelerometer, SensorEventListener {
    private lateinit var accelerationSensor: Sensor
    private lateinit var gravitySensor: Sensor

    private val _accelerationVector = MutableStateFlow<FloatArray>(floatArrayOf(0f, 0f, 0f))
    private val _unitGravityVector = MutableStateFlow<FloatArray>(floatArrayOf(0f, 0f, 1f))
    override val acceleration = MutableStateFlow<Float>(0f)

    private fun FloatArray.calcNorm(): Float {
        return sqrt(map { it.pow(2) }.sum())
    }

    private operator fun FloatArray.div(other: Float): FloatArray {
        return map { it / other }.toFloatArray()
    }

    private fun FloatArray.toUnitVector(): FloatArray {
        return this / calcNorm()
    }

    override suspend fun report() {
        TODO()
    }

    override fun listen() {
        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)?.let {
            this.accelerationSensor = it
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)?.let {
            this.gravitySensor = it
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
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

            gravitySensor -> {
                _unitGravityVector.value = event.values.toUnitVector()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // noop
    }
}