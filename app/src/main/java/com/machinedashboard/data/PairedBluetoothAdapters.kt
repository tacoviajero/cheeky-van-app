package com.machinedashboard.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import com.machinedashboard.domain.ConnectionError
import com.machinedashboard.domain.PairedAdapter
import com.machinedashboard.transport.ObdException

class PairedBluetoothAdapters(context: Context) {
    val adapter = context.getSystemService(BluetoothManager::class.java)?.adapter

    // Caller requests CONNECT; SecurityException handles revocation.
    @SuppressLint("MissingPermission")
    fun list(): List<PairedAdapter> {
        val bluetooth = adapter ?: throw ObdException(ConnectionError.BLUETOOTH_OFF)
        if (!bluetooth.isEnabled) throw ObdException(ConnectionError.BLUETOOTH_OFF)
        return bluetooth.bondedDevices.filter { it.type != BluetoothDevice.DEVICE_TYPE_LE }
            .map { device ->
                PairedAdapter(
                    device.address,
                    device.name.orEmpty().filterNot { it.isISOControl() }.take(64)
                        .ifBlank { "Dispositivo Bluetooth" }
                )
            }.sortedBy { it.name }
    }
}
