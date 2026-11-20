package com.machinedashboard.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.machinedashboard.R
import com.machinedashboard.domain.DevicePositionState
import com.machinedashboard.domain.PositionStatus
import com.machinedashboard.domain.SimulationScenario
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

private val tabs = listOf("Inicio", "Tablero", "Mapa", "Diagnóstico", "Ajustes")
internal val clusterBackground = Color(0xFF0C1216)
internal val clusterGreen = Color(0xFF70D49C)

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    position: DevicePositionState,
    onLocation: () -> Unit,
    onScenario: (SimulationScenario) -> Unit,
    onTestAdapter: () -> Unit,
    onBluetooth: () -> Unit,
    onDisconnect: () -> Unit,
    onDemo: () -> Unit,
    onSimulateLoss: () -> Unit
) {
    var page by rememberSaveable { mutableStateOf(0) }
    var detail by rememberSaveable { mutableStateOf<String?>(null) }
    BackHandler(enabled = detail != null || page != 0) {
        if (detail != null) detail = null else page = 0
    }
    MaterialTheme(colorScheme = darkColorScheme(primary = clusterGreen)) {
        Surface(color = clusterBackground, contentColor = Color(0xFFF1F5F3)) {
            Column(
                Modifier.fillMaxSize().background(clusterBackground).displayCutoutPadding()
            ) {
                ClusterHeader(state, detail ?: tabs[page], detail != null) { detail = null }
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    when {
                        detail != null -> ClusterSurface(
                            state.sample,
                            detail = detail,
                            position = position,
                            onLocation = onLocation
                        ) {
                            detail = it
                        }
                        page <= 1 -> ClusterSurface(
                            state.sample,
                            dashboard = page == 1,
                            position = position,
                            onLocation = onLocation
                        ) {
                            detail = it
                        }
                        page == 4 -> Column(
                            Modifier.fillMaxSize()
                                .verticalScroll(rememberScrollState()).padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ConnectionControls(
                                state,
                                onTestAdapter,
                                onBluetooth,
                                onDisconnect,
                                onDemo,
                                onSimulateLoss
                            )
                            Button(
                                onClick = onLocation,
                                modifier = Modifier.testTag("location")
                            ) {
                                Text(
                                    if (position.status == PositionStatus.PERMISSION) {
                                        "Activar ubicación"
                                    } else {
                                        "Ubicación del dispositivo"
                                    }
                                )
                            }
                            Text(positionLabel(position), fontSize = 13.sp)
                            Row(Modifier.horizontalScroll(rememberScrollState())) {
                                SimulationScenario.entries.forEach { scenario ->
                                    FilterChip(
                                        selected = state.sample?.scenario == scenario,
                                        onClick = { onScenario(scenario) },
                                        label = { Text(stringResource(scenario.label())) },
                                        modifier = Modifier.testTag("scenario_$scenario")
                                    )
                                    Spacer(Modifier.size(8.dp))
                                }
                            }
                            Text(stringResource(R.string.vehicle_name), fontSize = 14.sp)
                            Text(stringResource(R.string.safety_notice), fontSize = 12.sp)
                        }
                        else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                if (page == 2) {
                                    "Sin datos de mapa"
                                } else {
                                    "Sin lectura de diagnóstico"
                                },
                                color = Color(0xFFA8B8BD)
                            )
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().height(52.dp)) {
                    tabs.forEachIndexed { index, label ->
                        val selected = page == index
                        Column(
                            Modifier.weight(1f).height(52.dp).clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                page = index
                                detail = null
                            }.testTag("tab_$index"),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                Modifier.fillMaxWidth(.4f).height(2.dp).background(
                                    if (selected) clusterGreen else Color.Transparent
                                )
                            )
                            Spacer(Modifier.height(4.dp))
                            ClusterTabIcon(index, if (selected) clusterGreen else Color(0xFFA8B8BD))
                            Text(
                                label,
                                color = if (selected) clusterGreen else Color(0xFFA8B8BD),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClusterHeader(
    state: DashboardUiState,
    title: String,
    hasBack: Boolean,
    onBack: () -> Unit
) {
    var clock by remember { mutableStateOf("") }
    var minuteOfDay by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            clock = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val calendar = Calendar.getInstance()
            minuteOfDay = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
            delay(1000)
        }
    }
    val source = when (state.source) {
        DashboardSource.DEMO -> R.string.simulation_badge
        DashboardSource.TEST_ADAPTER -> R.string.test_adapter_badge
        DashboardSource.BLUETOOTH -> R.string.bluetooth_badge
    }
    Row(
        Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.app_name), fontSize = 15.sp)
                Text(title, fontSize = 11.sp, color = Color(0xFFA8B8BD))
            }
            Box(Modifier.width(40.dp)) {
                if (hasBack) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back").semantics {
                            contentDescription = "Volver"
                        }
                    ) {
                        Canvas(Modifier.size(22.dp)) {
                            val w = size.width
                            val h = size.height
                            val path = Path().apply {
                                moveTo(w * .8f, h * .5f)
                                lineTo(w * .2f, h * .5f)
                                moveTo(w * .5f, h * .2f)
                                lineTo(w * .2f, h * .5f)
                                lineTo(w * .5f, h * .8f)
                            }
                            drawPath(
                                path,
                                Color.White,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx())
                            )
                        }
                    }
                }
            }
        }
        Box(Modifier.weight(.6f), contentAlignment = Alignment.Center) {
            Text(stringResource(source), color = Color(0xFFE7C87B), fontSize = 11.sp)
        }
        Row(
            Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val event = nextSolarEvent(
                minuteOfDay,
                state.sample?.cluster?.sunriseMinute,
                state.sample?.cluster?.sunsetMinute
            )
            SunsetSymbol(event?.sunrise)
            val solar = event?.minute?.let {
                String.format(Locale.getDefault(), "%02d:%02d", it / 60, it % 60)
            } ?: "—"
            Text(solar, fontSize = 14.sp)
            Spacer(Modifier.size(16.dp))
            Text(clusterNumber(state.sample?.cluster?.ambient, 1) + " °C", fontSize = 14.sp)
            Spacer(Modifier.size(16.dp))
            Text(clock, fontSize = 16.sp)
        }
    }
}

@Composable
private fun SunsetSymbol(sunrise: Boolean?) {
    Canvas(Modifier.size(22.dp).padding(end = 4.dp)) {
        val w = size.width
        val h = size.height
        drawLine(Color.White, Offset(0f, h * .7f), Offset(w, h * .7f), 1.5.dp.toPx())
        drawArc(
            Color.White,
            180f,
            180f,
            true,
            Offset(w * .2f, h * .4f),
            androidx.compose.ui.geometry.Size(w * .6f, h * .6f)
        )
        if (sunrise == null) return@Canvas
        val tip = if (sunrise) .05f else .32f
        val tail = if (sunrise) .32f else .05f
        val shoulder = if (sunrise) .17f else .2f
        val arrow = Path().apply {
            moveTo(w * .5f, h * tail)
            lineTo(w * .5f, h * tip)
            moveTo(w * .3f, h * shoulder)
            lineTo(w * .5f, h * tip)
            lineTo(w * .7f, h * shoulder)
        }
        drawPath(
            arrow,
            Color.White,
            style = androidx.compose.ui.graphics.drawscope.Stroke(1.5.dp.toPx())
        )
    }
}

private fun SimulationScenario.label(): Int = when (this) {
    SimulationScenario.ENGINE_STOPPED -> R.string.scenario_stopped
    SimulationScenario.RUNNING -> R.string.scenario_running
    SimulationScenario.MISSING_SENSOR -> R.string.scenario_missing
}
