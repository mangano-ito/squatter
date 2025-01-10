package io.github.manganoito.squatter.logic.detector

import android.util.Log
import io.github.manganoito.squatter.logic.accelerometer.Accelerometer
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val threshold: Float = 1.5f
private const val upThreshold: Float = -0.5f

class SquatDetector @Inject constructor(
    private val accelerometer: Accelerometer,
) {
    private val buffer = ArrayDeque<Float>(16)
    private var phase: Phase = Phase.StandStill
    private var count: Int = 0
    private var lastUpdate: Long = 0

    private val _counter = MutableStateFlow(0)
    val counter = _counter.asStateFlow()

    suspend fun run() {
        accelerometer.listen()
        coroutineScope {
            async {
                accelerometer.report()
            }
            accelerometer.acceleration.onEach {
                add(it)
                checkPhase()
            }.launchIn(this)
        }
    }

    private fun add(value: Float) {
        Log.d(
            "squatter-sensor-events",
            value.toString(),
        )
        buffer.addLast(value)
        if (buffer.size > 16) {
            buffer.removeFirst()
        }
    }

    private fun checkPhase() {
        val nextPhase = phase.nextPhase(buffer)
        if (phase == Phase.GoingUp && nextPhase == Phase.StandStill) {
            ++count
            _counter.value = count
        }
        if (phase != nextPhase) {
            phase = nextPhase
            lastUpdate = System.currentTimeMillis()
        } else if (lastUpdate + 10000 < System.currentTimeMillis()) {
            phase = Phase.StandStill
            lastUpdate = System.currentTimeMillis()
        }
    }

    sealed interface Phase {
        fun nextPhase(buffer: ArrayDeque<Float>): Phase

        data object StandStill : Phase {
            override fun nextPhase(buffer: ArrayDeque<Float>): Phase {
                val startedToGoDown = buffer.takeLast(4).average() > threshold
                return if (startedToGoDown) {
                    GoingDown
                } else {
                    StandStill
                }
            }
        }

        data object GoingDown : Phase {
            override fun nextPhase(buffer: ArrayDeque<Float>): Phase {
                val wasStillOrDown = buffer.take(12).average() >= 0
                val startedToGoUp = buffer.takeLast(4).average() < upThreshold
                return if (wasStillOrDown && startedToGoUp) {
                    GoingUp
                } else {
                    GoingDown
                }
            }
        }

        data object GoingUp : Phase {
            override fun nextPhase(buffer: ArrayDeque<Float>): Phase {
                val goingUp = buffer.takeLast(16).average() <= upThreshold
                return if (goingUp) {
                    GoingUp
                } else {
                    StandStill
                }
            }
        }
    }
}