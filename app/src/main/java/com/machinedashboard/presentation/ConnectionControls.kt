package com.machinedashboard.presentation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.machinedashboard.R
import com.machinedashboard.domain.ConnectionError
import com.machinedashboard.domain.ConnectionPhase

@Composable
internal fun ConnectionControls(
    state: DashboardUiState,
    onTestAdapter: () -> Unit,
    onBluetooth: () -> Unit,
    onDisconnect: () -> Unit,
    onDemo: () -> Unit,
    onSimulateLoss: () -> Unit
) {
    val busy =
        state.connection.phase in listOf(ConnectionPhase.CONNECTING, ConnectionPhase.INITIALIZING)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onBluetooth,
                enabled = !busy,
                modifier = Modifier.testTag("bluetooth")
            ) {
                Text(stringResource(R.string.bluetooth_connect))
            }
            Button(
                onClick = onTestAdapter,
                enabled = !busy,
                modifier = Modifier.testTag("test_adapter")
            ) {
                Text(stringResource(R.string.test_adapter_connect))
            }
        }
        if (state.source != DashboardSource.DEMO) {
            Text(
                stringResource(state.connection.phase.label(state.connection.error)),
                modifier = Modifier.testTag("connection_status")
            )
            if (state.connection.adapterName.isNotEmpty()) {
                Text(state.connection.adapterName, style = MaterialTheme.typography.bodySmall)
            }
            if (state.connection.protocol.isNotEmpty()) {
                Text(stringResource(R.string.active_protocol, state.connection.protocol))
            }
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                TextButton(onClick = onDisconnect, modifier = Modifier.testTag("disconnect")) {
                    Text(stringResource(R.string.disconnect))
                }
                TextButton(onClick = onDemo) { Text(stringResource(R.string.back_to_demo)) }
                if (state.source == DashboardSource.TEST_ADAPTER &&
                    state.connection.phase == ConnectionPhase.CONNECTED
                ) {
                    TextButton(
                        onClick = onSimulateLoss,
                        modifier = Modifier.testTag("simulate_loss")
                    ) {
                        Text(stringResource(R.string.simulate_loss))
                    }
                }
            }
        }
    }
}

private fun ConnectionPhase.label(error: ConnectionError?): Int = when (this) {
    ConnectionPhase.DISCONNECTED -> R.string.connection_disconnected
    ConnectionPhase.CONNECTING -> R.string.connection_connecting
    ConnectionPhase.INITIALIZING -> R.string.connection_initializing
    ConnectionPhase.CONNECTED -> R.string.connection_connected
    ConnectionPhase.ERROR -> when (error) {
        ConnectionError.BLUETOOTH_OFF -> R.string.bluetooth_enable
        ConnectionError.PERMISSION -> R.string.bluetooth_permission_denied
        ConnectionError.PROTOCOL -> R.string.connection_protocol_error
        ConnectionError.VEHICLE_UNAVAILABLE -> R.string.connection_vehicle_unavailable
        ConnectionError.TIMEOUT -> R.string.connection_timeout
        else -> R.string.connection_error
    }
}
