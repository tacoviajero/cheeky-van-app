package com.machinedashboard.transport

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import com.machinedashboard.domain.ConnectionError
import com.machinedashboard.domain.PairedAdapter
import java.io.IOException
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine

// Checked by the permission flow; revocation is handled by the session.
@SuppressLint("MissingPermission")
class BluetoothClassicTransport(
    private val adapter: BluetoothAdapter?,
    private val target: PairedAdapter
) : ObdTransport {
    private val socket = AtomicReference<BluetoothSocket?>()
    private val closed = AtomicBoolean(false)

    private suspend fun <T> blocking(block: () -> T): T =
        suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation { close() }
            Dispatchers.IO.dispatch(EmptyCoroutineContext) {
                if (continuation.isActive) {
                    try {
                        continuation.resume(block())
                    } catch (error: Exception) {
                        if (continuation.isActive) continuation.resumeWithException(error)
                    }
                }
            }
        }

    override suspend fun open() = blocking {
        val bluetooth = adapter ?: throw ObdException(ConnectionError.BLUETOOTH_OFF)
        if (!bluetooth.isEnabled) throw ObdException(ConnectionError.BLUETOOTH_OFF)
        val device = bluetooth.bondedDevices.firstOrNull { it.address == target.id }
            ?: throw ObdException(ConnectionError.CONNECTION)
        val created = device.createRfcommSocketToServiceRecord(SPP)
        socket.set(created)
        if (closed.get()) {
            socket.getAndSet(null)?.close()
            throw IOException()
        }
        created.connect()
    }

    override suspend fun exchange(command: ObdCommand): String = blocking {
        val active = socket.get() ?: throw IOException()
        active.outputStream.write((command.wire + "\r").toByteArray(Charsets.US_ASCII))
        active.outputStream.flush()
        val response = StringBuilder()
        while (true) {
            val byte = active.inputStream.read()
            if (byte < 0) throw IOException()
            if (byte == '>'.code) break
            if (response.length >= 4096 || (byte !in 9..13 && byte !in 32..126)) throw IOException()
            response.append(byte.toChar())
        }
        response.toString()
    }

    override fun close() {
        closed.set(true)
        try {
            socket.getAndSet(null)?.close()
        } catch (_: IOException) { }
    }

    private companion object {
        val SPP: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    }
}
