package com.machinedashboard.transport

import java.io.IOException
import kotlinx.coroutines.delay

/** Exercises the real command scheduler/parser without a Bluetooth radio or vehicle. */
class TestObdTransport(
    private val responseDelayMillis: Long = 100,
    private val supportedMask: String = "18180000"
) : ObdTransport {
    @Volatile private var closed = false

    @Volatile private var lost = false
    val commands = mutableListOf<ObdCommand>()

    override suspend fun open() {
        delay(responseDelayMillis)
        if (closed) throw IOException()
    }

    override suspend fun exchange(command: ObdCommand): String {
        delay(responseDelayMillis)
        if (closed || lost) throw IOException()
        if (commands.size < 128) commands += command
        return when (command) {
            ObdCommand.RESET -> "ELM327 v1.5\r>"
            ObdCommand.PROTOCOL -> "A6\r>"
            ObdCommand.SUPPORTED -> "4100$supportedMask\r>"
            ObdCommand.RPM -> "410C1770\r>"
            ObdCommand.SPEED -> "410D23\r>"
            ObdCommand.COOLANT -> "41057C\r>"
            ObdCommand.LOAD -> "410466\r>"
            else -> "OK\r>"
        }
    }

    fun loseConnection() {
        lost = true
    }
    override fun close() {
        closed = true
    }
}
