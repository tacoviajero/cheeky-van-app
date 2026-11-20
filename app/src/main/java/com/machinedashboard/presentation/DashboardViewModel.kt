package com.machinedashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machinedashboard.domain.ConnectionPhase
import com.machinedashboard.domain.ConnectionStatus
import com.machinedashboard.domain.ConnectionTarget
import com.machinedashboard.domain.SimulationScenario
import com.machinedashboard.domain.TelemetryRepository
import com.machinedashboard.domain.TelemetrySample
import com.machinedashboard.domain.VehicleConnection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class DashboardSource { DEMO, TEST_ADAPTER, BLUETOOTH }

data class DashboardUiState(
    val source: DashboardSource = DashboardSource.DEMO,
    val sample: TelemetrySample? = null,
    val connection: ConnectionStatus = ConnectionStatus()
)

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    private val scenarios: MutableStateFlow<SimulationScenario>,
    repository: TelemetryRepository,
    private val connection: VehicleConnection
) : ViewModel() {
    private val source = MutableStateFlow(DashboardSource.DEMO)
    private var connectionJob: Job? = null
    val uiState = source.flatMapLatest { mode ->
        if (mode == DashboardSource.DEMO) {
            repository.samples.map { DashboardUiState(mode, it) }
        } else {
            combine(connection.samples, connection.status) { sample, status ->
                DashboardUiState(
                    mode,
                    sample.takeIf {
                        status.phase == ConnectionPhase.CONNECTED
                    },
                    status
                )
            }.onStart { emit(DashboardUiState(mode)) }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(stopTimeoutMillis = 0),
        DashboardUiState()
    )

    fun selectScenario(scenario: SimulationScenario) {
        showDemo()
        scenarios.value = scenario
    }

    fun connect(target: ConnectionTarget) {
        val previous = connectionJob
        previous?.cancel()
        connection.disconnect()
        source.value = if (target is ConnectionTarget.Bluetooth) {
            DashboardSource.BLUETOOTH
        } else {
            DashboardSource.TEST_ADAPTER
        }
        connectionJob = viewModelScope.launch {
            previous?.join()
            connection.run(target)
        }
    }

    fun disconnect() {
        connectionJob?.cancel()
        connection.disconnect()
    }

    fun showDemo() {
        disconnect()
        source.value = DashboardSource.DEMO
    }

    fun simulateLoss() = connection.simulateLoss()

    override fun onCleared() {
        connection.disconnect()
    }
}
