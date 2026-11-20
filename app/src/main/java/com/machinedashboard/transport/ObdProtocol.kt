package com.machinedashboard.transport

import com.machinedashboard.domain.ConnectionError
import com.machinedashboard.domain.Metric
import com.machinedashboard.domain.Reading
import com.machinedashboard.domain.UnavailableReason
import java.io.Closeable
import java.io.IOException
import java.util.Locale

/** The complete wire-command allowlist. There is no arbitrary-command entry point. */
enum class ObdCommand(val wire: String, val pid: Int? = null, val metric: Metric? = null) {
    RESET("ATZ"),
    ECHO_OFF("ATE0"),
    LINEFEEDS_OFF("ATL0"),
    SPACES_OFF("ATS0"),
    HEADERS_OFF("ATH0"),
    AUTO_PROTOCOL("ATSP0"),
    PROTOCOL("ATDPN"),
    SUPPORTED("0100", 0),
    RPM("010C", 12, Metric.ENGINE_RPM),
    SPEED("010D", 13, Metric.SPEED),
    COOLANT("0105", 5, Metric.COOLANT_TEMPERATURE),
    LOAD("0104", 4, Metric.ENGINE_LOAD)
}

interface ObdTransport : Closeable {
    suspend fun open()
    suspend fun exchange(command: ObdCommand): String
}

class ObdException(val reason: ConnectionError) : IOException()

object ObdProtocol {
    val initialization = listOf(
        ObdCommand.RESET,
        ObdCommand.ECHO_OFF,
        ObdCommand.LINEFEEDS_OFF,
        ObdCommand.SPACES_OFF,
        ObdCommand.HEADERS_OFF,
        ObdCommand.AUTO_PROTOCOL
    )
    val telemetry = listOf(ObdCommand.RPM, ObdCommand.SPEED, ObdCommand.COOLANT, ObdCommand.LOAD)

    private fun lines(command: ObdCommand, raw: String): List<String> {
        require(raw.length <= 4096 && raw.all { it.code in 9..13 || it.code in 32..126 })
        val lines = raw.trim().removeSuffix(">").uppercase(Locale.ROOT).split('\r', '\n')
            .map { it.trim().replace(" ", "").replace("\t", "") }
            .filter { it.isNotEmpty() && it != command.wire && !it.startsWith("SEARCHING") }
        if (lines.any { it == "UNABLETOCONNECT" || it == "BUSINIT:ERROR" }) {
            throw ObdException(ConnectionError.VEHICLE_UNAVAILABLE)
        }
        return lines
    }

    fun validateInitialization(command: ObdCommand, raw: String) {
        val lines = lines(command, raw)
        require(
            if (command ==
                ObdCommand.RESET
            ) {
                lines.any { it.startsWith("ELM327") }
            } else {
                lines == listOf("OK")
            }
        )
    }

    fun protocol(raw: String): String {
        val response = lines(ObdCommand.PROTOCOL, raw)
        require(response.size == 1 && response.first().matches(Regex("A?[1-9]")))
        return response.first()
    }

    private fun payload(command: ObdCommand, raw: String, bytes: Int): String? {
        val lines = lines(command, raw)
        if (lines == listOf("NODATA") || lines == listOf("STOPPED")) return null
        val prefix = "41%02X".format(Locale.ROOT, requireNotNull(command.pid))
        require(lines.isNotEmpty())
        require(
            lines.all {
                it.length == 4 + bytes * 2 && it.startsWith(prefix) &&
                    it.matches(Regex("[0-9A-F]+"))
            }
        )
        // With headers disabled, conflicting ECU replies are ambiguous and rejected.
        require(lines.distinct().size == 1)
        return lines.first().substring(4)
    }

    fun supported(raw: String): Set<ObdCommand> {
        val data = payload(ObdCommand.SUPPORTED, raw, 4)
            ?: throw ObdException(ConnectionError.VEHICLE_UNAVAILABLE)
        val mask = data.toLong(16)
        return telemetry.filter { mask and (1L shl (32 - requireNotNull(it.pid))) != 0L }.toSet()
    }

    fun reading(command: ObdCommand, raw: String): Reading {
        require(command in telemetry)
        return try {
            val data = payload(command, raw, if (command == ObdCommand.RPM) 2 else 1)
                ?: return Reading.Unavailable(UnavailableReason.NO_DATA)
            val number = data.toInt(16).toDouble()
            Reading.Value(
                when (command) {
                    ObdCommand.RPM -> number / 4.0
                    ObdCommand.COOLANT -> number - 40.0
                    ObdCommand.LOAD -> number * 100.0 / 255.0
                    else -> number
                }
            )
        } catch (_: IllegalArgumentException) {
            Reading.Unavailable(UnavailableReason.MALFORMED)
        }
    }
}
