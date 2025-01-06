package io.github.manganoito.squatter.logic.accelerometer

import kotlinx.coroutines.flow.StateFlow

interface Accelerometer {
    val acceleration: StateFlow<Float>

    fun listen()
    fun unlisten()
    suspend fun report()
}
