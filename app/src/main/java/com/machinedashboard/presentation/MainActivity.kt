package com.machinedashboard.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.machinedashboard.R
import com.machinedashboard.data.DeviceLocationSource
import com.machinedashboard.data.ObdVehicleConnection
import com.machinedashboard.data.PairedBluetoothAdapters
import com.machinedashboard.data.SimulatedTelemetryRepository
import com.machinedashboard.domain.ConnectionTarget
import com.machinedashboard.domain.PairedAdapter
import com.machinedashboard.domain.PositionStatus
import com.machinedashboard.domain.SimulationScenario
import com.machinedashboard.transport.BluetoothClassicTransport
import com.machinedashboard.transport.TestObdTransport
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    private lateinit var dashboard: DashboardViewModel
    private lateinit var deviceLocation: DeviceLocationSource
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        deviceLocation = DeviceLocationSource(this)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
        val paired = PairedBluetoothAdapters(this)
        dashboard =
            ViewModelProvider(this, DashboardFactory(paired))[DashboardViewModel::class.java]
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                var adapters by remember { mutableStateOf<List<PairedAdapter>?>(null) }
                var problem by remember { mutableStateOf<String?>(null) }
                fun showPaired() {
                    try {
                        adapters = paired.list()
                    } catch (_: SecurityException) {
                        problem = getString(R.string.bluetooth_permission_denied)
                    } catch (_: Exception) {
                        problem = getString(R.string.bluetooth_enable)
                    }
                }
                val permission =
                    rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { allowed ->
                        if (allowed) {
                            showPaired()
                        } else {
                            problem =
                                getString(R.string.bluetooth_permission_denied)
                        }
                    }
                val locationPermission = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { deviceLocation.start() }
                DashboardScreen(
                    state = dashboard.uiState.collectAsStateWithLifecycle().value,
                    position = deviceLocation.state.collectAsStateWithLifecycle().value,
                    onLocation = {
                        when {
                            deviceLocation.state.value.status == PositionStatus.DISABLED ->
                                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED &&
                                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED -> locationPermission.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            )
                            else -> deviceLocation.start()
                        }
                    },
                    onScenario = dashboard::selectScenario,
                    onTestAdapter = { dashboard.connect(ConnectionTarget.TestAdapter) },
                    onBluetooth = {
                        if (Build.VERSION.SDK_INT >= 31 &&
                            checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) !=
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            permission.launch(Manifest.permission.BLUETOOTH_CONNECT)
                        } else {
                            showPaired()
                        }
                    },
                    onDisconnect = dashboard::disconnect,
                    onDemo = dashboard::showDemo,
                    onSimulateLoss = dashboard::simulateLoss
                )
                if (adapters != null || problem != null) {
                    AlertDialog(
                        onDismissRequest = {
                            adapters = null
                            problem = null
                        },
                        title = { Text(getString(R.string.paired_adapters)) },
                        text = {
                            Column(Modifier.verticalScroll(rememberScrollState())) {
                                if (problem != null) {
                                    Text(requireNotNull(problem))
                                } else if (adapters.isNullOrEmpty()) {
                                    Text(getString(R.string.no_paired_adapters))
                                } else {
                                    adapters.orEmpty().forEach { adapter ->
                                        TextButton(onClick = {
                                            adapters = null
                                            dashboard.connect(ConnectionTarget.Bluetooth(adapter))
                                        }) { Text(adapter.name + " · " + adapter.id.takeLast(5)) }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                adapters = null
                                problem = null
                            }) { Text(getString(R.string.close)) }
                        }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (::deviceLocation.isInitialized) deviceLocation.start()
    }

    override fun onStop() {
        if (::deviceLocation.isInitialized) deviceLocation.stop()
        if (::dashboard.isInitialized) dashboard.disconnect()
        super.onStop()
    }
}

private class DashboardFactory(private val paired: PairedBluetoothAdapters) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(DashboardViewModel::class.java))
        val scenarios = MutableStateFlow(SimulationScenario.RUNNING)

        val connection = ObdVehicleConnection(factory = { target ->
            when (target) {
                ConnectionTarget.TestAdapter -> TestObdTransport()
                is ConnectionTarget.Bluetooth -> BluetoothClassicTransport(
                    paired.adapter,
                    target.adapter
                )
            }
        })
        @Suppress("UNCHECKED_CAST")
        return DashboardViewModel(
            scenarios,
            SimulatedTelemetryRepository(scenarios),
            connection
        ) as T
    }
}
