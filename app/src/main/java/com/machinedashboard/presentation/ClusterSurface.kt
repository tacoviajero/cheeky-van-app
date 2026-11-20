package com.machinedashboard.presentation

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.graphics.withScale
import androidx.core.graphics.withTranslation
import com.machinedashboard.domain.DevicePositionState
import com.machinedashboard.domain.Metric
import com.machinedashboard.domain.PositionStatus
import com.machinedashboard.domain.Reading
import com.machinedashboard.domain.TelemetrySample
import com.machinedashboard.domain.UnavailableReason
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

internal fun positionLabel(position: DevicePositionState): String = when (position.status) {
    PositionStatus.PERMISSION -> "Activar ubicación"
    PositionStatus.DISABLED -> "Ubicación desactivada"
    PositionStatus.SEARCHING -> "Buscando ubicación"
    PositionStatus.STALE -> "Ubicación desactualizada"
    PositionStatus.AVAILABLE -> when {
        position.fix?.mock == true -> "Ubicación de prueba"
        position.fix?.approximate == true -> "Ubicación aproximada"
        else -> "Ubicación del dispositivo"
    }
}

internal fun clusterNumber(value: Double?, decimals: Int = 0): String =
    value?.takeIf { it.isFinite() }?.let {
        String.format(Locale.getDefault(), "%.$decimals" + "f", it)
    } ?: "—"

internal fun gaugeFraction(value: Double?, maximum: Double): Float? =
    value?.takeIf { it.isFinite() && maximum.isFinite() && maximum > 0 }
        ?.let { (it / maximum).coerceIn(0.0, 1.0).toFloat() }

internal fun gearDirection(current: String?, suggested: Int?): Int? {
    val actual = current?.toIntOrNull() ?: return null
    if (suggested == null || actual !in 1..6 || suggested !in 1..6 || actual == suggested) {
        return null
    }
    return if (suggested > actual) 1 else -1
}

private data class ClusterField(
    val id: String,
    val label: String,
    val value: String,
    val number: Double?,
    val unit: String,
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float = 100f,
    val low: Double = 0.0,
    val high: Double = 100.0,
    val band: String? = null,
    val detail: String? = null
)

@Composable
internal fun ClusterSurface(
    sample: TelemetrySample?,
    dashboard: Boolean = false,
    detail: String? = null,
    position: DevicePositionState = DevicePositionState(),
    onLocation: () -> Unit = {},
    onDetail: (String) -> Unit
) {
    val fields = clusterFields(sample, dashboard, detail)
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val scaleX = maxWidth.value / 1440f
        val scaleY = maxHeight.value / 700f
        Canvas(Modifier.fillMaxSize()) {
            drawIntoCanvas { target ->
                val canvas = target.nativeCanvas
                val k = size.height / 700f
                if (k > 0 && size.width > 0) {
                    canvas.withScale(k, k) {
                        ClusterPainter(this, size.width / (1440f * k))
                            .draw(sample, fields, dashboard, detail, position)
                    }
                }
            }
        }
        Box(
            Modifier.offset((28 * scaleX).dp, (644 * scaleY).dp)
                .size((1384 * scaleX).dp, (56 * scaleY).dp)
                .testTag("device_location").clickable(onClick = onLocation)
                .semantics { contentDescription = positionLabel(position) }
        )
        fields.forEach { field ->
            Box(
                Modifier.offset((field.x * scaleX).dp, (field.y * scaleY).dp)
                    .size((field.w * scaleX).dp, (field.h * scaleY).dp)
                    .testTag("value_${field.id}")
                    .semantics {
                        text = AnnotatedString(field.value)
                        contentDescription = "${field.label}: ${field.value} ${field.unit}"
                    }.then(
                        if (field.detail != null) {
                            Modifier.clickable { onDetail(field.detail) }
                        } else {
                            Modifier
                        }
                    )
            )
        }
        if (!dashboard && detail == null) {
            val gear = sample?.cluster?.gear ?: "—"
            val direction = gearDirection(gear, sample?.cluster?.suggestedGear)
            Box(
                Modifier.offset((120 * scaleX).dp, (250 * scaleY).dp)
                    .size((250 * scaleX).dp, (100 * scaleY).dp).testTag("gear")
                    .semantics {
                        text = AnnotatedString(gear)
                        contentDescription = if (direction == null) {
                            "Marcha $gear"
                        } else {
                            "Marcha $gear, ${if (direction > 0) "subir" else "bajar"} " +
                                "a ${sample?.cluster?.suggestedGear}"
                        }
                    }
            )
        }
    }
}

private fun clusterFields(
    sample: TelemetrySample?,
    dashboard: Boolean,
    detail: String?
): List<ClusterField> {
    val result = mutableListOf<ClusterField>()
    val extras = sample?.cluster
    fun metric(
        metric: Metric,
        label: String,
        unit: String,
        x: Float,
        y: Float,
        w: Float,
        low: Double = 0.0,
        high: Double = 100.0,
        band: String? = null,
        destination: String? = null,
        decimals: Int = 0
    ) {
        val reading = sample?.get(metric)
        val number = (reading as? Reading.Value)?.number
        val value = if (reading is Reading.Unavailable) {
            when (reading.reason) {
                UnavailableReason.NO_DATA -> "Sin datos"
                UnavailableReason.UNSUPPORTED -> "Sin soporte"
                UnavailableReason.MALFORMED -> "Respuesta inválida"
                UnavailableReason.STALE -> "Desactualizado"
                UnavailableReason.NOT_VERIFIED -> "—"
            }
        } else {
            clusterNumber(number, decimals)
        }
        result += ClusterField(
            metric.name, label, value, number, unit, x, y, w,
            low = low, high = high, band = band, detail = destination
        )
    }
    fun extra(
        id: String,
        label: String,
        value: Double?,
        unit: String,
        x: Float,
        y: Float,
        w: Float,
        decimals: Int = 0,
        high: Double = 100.0,
        band: String? = null,
        destination: String? = null
    ) {
        result += ClusterField(
            id, label, clusterNumber(value, decimals), value, unit, x, y, w,
            high = high, band = band, detail = destination
        )
    }
    if (detail != null) {
        if (detail == "Autonomía") {
            extra("RANGE", "ALCANCE ESTIMADO", extras?.rangeKm, "km", 60f, 90f, 430f)
            extra(
                "FUEL",
                "COMBUSTIBLE",
                extras?.fuelPercent,
                "%",
                530f,
                90f,
                400f,
                band = "green"
            )
            extra(
                "CONSUMPTION", "CONSUMO", extras?.consumption, "L/100 km", 970f, 90f,
                400f, 1, 20.0, "green"
            )
        } else {
            val metrics = listOf(
                Triple(Metric.BOOST_PRESSURE, "PRESIÓN TURBO", "bar"),
                Triple(Metric.MANIFOLD_PRESSURE, "PRESIÓN MÚLTIPLE", "kPa"),
                Triple(Metric.ATMOSPHERIC_PRESSURE, "PRESIÓN ATMOSFÉRICA", "kPa"),
                Triple(Metric.INTAKE_TEMPERATURE, "TEMPERATURA ADMISIÓN", "°C"),
                Triple(Metric.AIR_FLOW, "CAUDAL DE AIRE", "g/s"),
                Triple(Metric.TURBO_RPM, "RPM DEL TURBO", "rpm"),
                Triple(Metric.REQUESTED_BOOST, "PRESIÓN SOLICITADA", "bar"),
                Triple(Metric.TURBO_TEMPERATURE, "TEMPERATURA DEL TURBO", "°C"),
                Triple(Metric.VGT_POSITION, "GEOMETRÍA VARIABLE", "%")
            )
            metrics.forEachIndexed { i, item ->
                metric(
                    item.first, item.second, item.third, 40f + (i % 3) * 470f,
                    55f + (i / 3) * 170f, 430f, high = 300.0, band = "green",
                    decimals = if (item.third == "bar") 2 else 0
                )
            }
        }
        return result
    }
    if (dashboard) {
        metric(Metric.ENGINE_RPM, "REVOLUCIONES", "rpm", 72f, 17f, 370f)
        metric(Metric.SPEED, "VELOCIDAD", "km/h", 998f, 17f, 370f)
        metric(
            Metric.COOLANT_TEMPERATURE, "REFRIGERANTE", "°C", 506f, 125f, 206f,
            40.0, 130.0, "temperature", "Motor y turbo"
        )
        metric(
            Metric.OIL_TEMPERATURE, "ACEITE", "°C", 728f, 125f, 206f,
            40.0, 160.0, "temperature", "Motor y turbo"
        )
        extra(
            "BATTERY", "VOLTAJE DE BATERÍA", extras?.batteryVoltage, "V", 506f,
            244f, 206f, 1, band = "battery"
        )
        metric(Metric.ENGINE_LOAD, "CARGA MOTOR", "%", 728f, 244f, 206f, band = "green")
        metric(
            Metric.MANIFOLD_PRESSURE, "PRESIÓN MÚLTIPLE", "kPa", 28f, 393f, 265f,
            high = 300.0, band = "green", destination = "Motor y turbo"
        )
        extra(
            "THROTTLE",
            "ACELERADOR",
            extras?.throttle,
            "%",
            307.6f,
            393f,
            265f,
            band = "green"
        )
        extra(
            "CONSUMPTION", "CONSUMO INSTANTÁNEO", extras?.consumption, "L/100 km",
            587.2f, 393f, 265f, 1, 20.0, "green"
        )
        extra("STFT", "CORRECCIÓN STFT", null, "%", 866.8f, 393f, 265f, band = "green")
        extra("LTFT", "CORRECCIÓN LTFT", null, "%", 1146.4f, 393f, 265f, band = "green")
        val bottom = listOf(
            Triple("FUEL", "COMBUSTIBLE", extras?.fuelPercent),
            Triple("TRIP_A", "TRIP A", extras?.tripA),
            Triple("TRIP_B", "TRIP B", extras?.tripB),
            Triple("ODOMETER", "KILÓMETROS TOTALES", extras?.odometer),
            Triple("HOURS", "HORAS DE MOTOR", extras?.engineHours),
            Triple("RANGE", "AUTONOMÍA", extras?.rangeKm)
        )
        bottom.forEachIndexed { i, item ->
            extra(
                item.first, item.second, item.third,
                when (i) {
                    0 -> "%"
                    4 -> "h"
                    else -> "km"
                },
                28f + i * 230.66f, 535f, 216f,
                if (i in listOf(1, 2, 4)) 1 else 0,
                high = if (i == 5) 500.0 else 100.0,
                band = if (i == 0 || i == 5) "green" else null,
                destination = if (i == 0 || i == 5) "Autonomía" else null
            )
        }
    } else {
        metric(Metric.SPEED, "VELOCIDAD", "km/h", 475f, 14f, 490f)
        metric(Metric.ENGINE_RPM, "REVOLUCIONES", "rpm", 66f, 180f, 340f)
        metric(
            Metric.COOLANT_TEMPERATURE, "REFRIGERANTE", "°C", 1047f, 100f, 144f,
            40.0, 130.0, "temperature"
        )
        metric(
            Metric.OIL_TEMPERATURE, "ACEITE", "°C", 1240f, 100f, 124f,
            40.0, 160.0, "temperature"
        )
        extra(
            "RANGE",
            "AUTONOMÍA",
            extras?.rangeKm,
            "km",
            1040f,
            236f,
            345f,
            destination = "Autonomía"
        )
        extra(
            "FUEL", "COMBUSTIBLE", extras?.fuelPercent, "%", 1040f, 342f, 345f,
            band = "green", destination = "Autonomía"
        )
        extra("ODOMETER", "KILÓMETROS TOTALES", extras?.odometer, "km", 90f, 393f, 300f)
        val bottom = listOf(
            Triple("TRIP_A", "TRIP A", extras?.tripA),
            Triple("TRIP_B", "TRIP B", extras?.tripB),
            Triple("CONSUMPTION", "CONSUMO", extras?.consumption),
            Triple("HOURS", "HORAS DE MOTOR", extras?.engineHours)
        )
        bottom.forEachIndexed { i, item ->
            extra(
                item.first,
                item.second,
                item.third,
                when (i) {
                    2 -> "L/100 km"
                    3 -> "h"
                    else -> "km"
                },
                28f + i * 346f,
                535f,
                330f,
                1
            )
        }
    }
    return result
}

private class ClusterPainter(private val canvas: Canvas, private var stretch: Float) {
    private val ink = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL)
    }
    private val white = 0xFFF1F5F3.toInt()
    private val muted = 0xFFA8B8BD.toInt()
    private val green = 0xFF70D49C.toInt()
    private val red = 0xFFF35B65.toInt()
    private val amber = 0xFFE7C87B.toInt()
    private val track = 0xFF2C3B42.toInt()
    private val panel = 0xFF131E24.toInt()

    private fun x(value: Float): Float = value * stretch

    /** Reposition circular instruments, keeping their local geometry uniform. */
    private fun instrument(cx: Float, cy: Float, sizeScale: Float = 1f, draw: () -> Unit) {
        val previous = stretch
        val k = min(1f, previous) * sizeScale
        stretch = 1f
        try {
            canvas.withTranslation(cx * previous, cy) {
                withScale(k, k) {
                    withTranslation(-cx, -cy) { draw() }
                }
            }
        } finally {
            stretch = previous
        }
    }

    private fun line(x: Float, y: Float, xx: Float, yy: Float, color: Int, width: Float = 2f) {
        ink.color = color
        ink.strokeWidth = width
        canvas.drawLine(this.x(x), y, this.x(xx), yy, ink)
    }

    private fun text(
        value: String,
        x: Float,
        y: Float,
        size: Float,
        color: Int = white,
        align: Paint.Align = Paint.Align.LEFT
    ) {
        ink.color = color
        ink.textSize = size
        ink.style = Paint.Style.FILL
        ink.textAlign = align
        canvas.drawText(value, this.x(x), y, ink)
    }

    private fun arc(
        cx: Float,
        cy: Float,
        r: Float,
        start: Float,
        sweep: Float,
        color: Int,
        width: Float = 5f
    ) {
        ink.color = color
        ink.style = Paint.Style.STROKE
        ink.strokeWidth = width
        canvas.drawArc(RectF(cx - r, cy - r, cx + r, cy + r), start, sweep, false, ink)
        ink.style = Paint.Style.FILL
    }

    private fun polygon(points: List<Pair<Float, Float>>) {
        val path = android.graphics.Path()
        points.forEachIndexed { i, p ->
            if (i == 0) path.moveTo(x(p.first), p.second) else path.lineTo(x(p.first), p.second)
        }
        path.close()
        ink.color = panel
        canvas.drawPath(path, ink)
        ink.color = 0xFF405158.toInt()
        ink.style = Paint.Style.STROKE
        ink.strokeWidth = 2f
        canvas.drawPath(path, ink)
        ink.style = Paint.Style.FILL
    }

    private fun panelInner(left: Boolean): List<Pair<Float, Float>> {
        val radius = 245f * 1.14f * min(1f, stretch)
        return (0..78).map { step ->
            val y = 45f + step * 5
            val dy = y - 285f
            val horizontal = sqrt((radius * radius - dy * dy).coerceAtLeast(0f))
            val edge = x(720f) + (if (left) -1 else 1) * (horizontal + 18f)
            (edge / stretch) to y
        }
    }

    fun draw(
        sample: TelemetrySample?,
        fields: List<ClusterField>,
        dashboard: Boolean,
        detail: String?,
        position: DevicePositionState
    ) {
        fun number(metric: Metric) = (sample?.get(metric) as? Reading.Value)?.number
        if (detail == null) {
            if (dashboard) {
                instrument(257f, 203f) {
                    dial(257f, 203f, 185f, number(Metric.ENGINE_RPM), 6000.0, true)
                }
                instrument(1183f, 203f) {
                    dial(1183f, 203f, 185f, number(Metric.SPEED), 240.0, false)
                }
                fields.filterNot { it.id == "SPEED" || it.id == "ENGINE_RPM" }.forEach { card(it) }
            } else {
                polygon(
                    listOf(24f to 463f, 72f to 139f, 116f to 94f) +
                        panelInner(true) + listOf(44f to 480f)
                )
                polygon(
                    panelInner(false) + listOf(
                        1396f to 480f,
                        1416f to 463f,
                        1368f to 139f,
                        1324f to 94f
                    )
                )
                instrument(720f, 285f, 1.14f) {
                    dial(720f, 285f, 245f, number(Metric.SPEED), 240.0, false)
                }
                instrument(236f, 350f) {
                    rpmArc(number(Metric.ENGINE_RPM))
                    gears(sample)
                }
                fields.filterNot { it.id == "SPEED" || it.id == "ENGINE_RPM" }.forEach { field ->
                    when {
                        field.y >= 535 -> counter(field)
                        field.id == "ODOMETER" -> {
                            instrument(236f, 350f) {
                                line(130f, 375f, 342f, 375f, track)
                                text(
                                    field.value + " km",
                                    236f,
                                    425f,
                                    32f,
                                    align = Paint.Align.CENTER
                                )
                            }
                        }
                        field.id == "RANGE" -> {
                            text(field.label, 1213f, 246f, 15f, muted, Paint.Align.CENTER)
                            text(
                                field.value + " km",
                                1213f,
                                306f,
                                43f,
                                align = Paint.Align.CENTER
                            )
                        }
                        field.id == "FUEL" -> {
                            text(field.label, 1040f, 366f, 15f, muted)
                            text(field.value + " %", 1385f, 366f, 22f, align = Paint.Align.RIGHT)
                            bar(field.copy(y = 342f, h = 55f))
                            text("E", 1040f, 414f, 15f, muted)
                            instrument(1213f, 406f) { fuelIcon(1213f, 406f) }
                            text("F", 1385f, 414f, 15f, muted, Paint.Align.RIGHT)
                        }
                        else -> {
                            text(
                                field.label,
                                field.x + field.w / 2,
                                125f,
                                13f,
                                muted,
                                Paint.Align.CENTER
                            )
                            text(
                                field.value + " °C",
                                field.x + field.w / 2,
                                168f,
                                32f,
                                align = Paint.Align.CENTER
                            )
                            bar(field.copy(y = 110f, h = 95f))
                        }
                    }
                }
                line(1040f, 219f, 1386f, 219f, track)
                line(1040f, 331f, 1386f, 331f, track)
            }
        } else {
            fields.forEach { card(it) }
        }
        line(28f, 644f, 1412f, 644f, track)
        val fix = position.fix
        val coordinates = if (fix != null) {
            val decimals = if (fix.approximate) 2 else 4
            val latitude = clusterNumber(abs(fix.latitude), decimals)
            val longitude = clusterNumber(abs(fix.longitude), decimals)
            val source = if (fix.mock) "Prueba" else "Android"
            "$source · $latitude ${if (fix.latitude >= 0) "N" else "S"}  " +
                "$longitude ${if (fix.longitude >= 0) "E" else "O"}"
        } else {
            positionLabel(position)
        }
        text(coordinates, 30f, 679f, 15f, muted)
        text(
            (if (fix?.altitude != null && !fix.altitudeIsMsl) "ALTITUD GNSS  " else "ALTITUD  ") +
                "${clusterNumber(fix?.altitude)} m",
            770f,
            679f,
            15f,
            muted,
            Paint.Align.CENTER
        )
        text("OBD", 1410f, 679f, 15f, muted, Paint.Align.RIGHT)
    }

    private fun card(field: ClusterField) {
        ink.color = panel
        canvas.drawRoundRect(
            RectF(
                x(field.x),
                field.y,
                x(field.x + field.w),
                field.y + field.h
            ),
            10f,
            10f,
            ink
        )
        text(field.label, field.x + 16, field.y + 27, 14f, muted)
        val right = field.x + field.w - 16
        ink.textSize = 16f
        val unitWidth = if (field.unit.isEmpty()) 0f else ink.measureText(field.unit) + 9
        var valueSize = if (field.value.length > 9) 24f else 34f
        ink.textSize = valueSize
        val available = (field.w - 32) * stretch - unitWidth
        val measured = ink.measureText(field.value)
        if (measured > available) valueSize *= available / measured
        text(field.value, field.x + 16, field.y + 66, valueSize)
        text(field.unit, right, field.y + 66, 16f, muted, Paint.Align.RIGHT)
        bar(field)
        if (field.detail != null) {
            val xx = field.x + field.w - 23
            line(xx, field.y + 15, xx + 6, field.y + 21, green)
            line(xx + 6, field.y + 21, xx, field.y + 27, green)
        }
    }

    private fun counter(field: ClusterField) {
        if (field.x > 28) line(field.x - 14, 538f, field.x - 14, 623f, track)
        text(field.label, field.x + 10, 563f, 14f, muted)
        text(field.value, field.x + 10, 608f, 32f)
        text(field.unit, field.x + field.w - 10, 608f, 15f, muted, Paint.Align.RIGHT)
    }

    private fun bar(field: ClusterField) {
        if (field.band == null) return
        val x = field.x + 17
        val w = field.w - 34
        val y = field.y + field.h - 15
        if (field.id == "FUEL") {
            line(x, y, x + w, y, muted, 1.5f)
            val fraction = gaugeFraction(field.number, 100.0)
            if (fraction != null) line(x, y, x + w * fraction, y, green, 5f)
            for (tick in listOf(0f, .0625f, .125f, .1875f, .25f, .5f, .75f, 1f)) {
                val h = if (tick == .5f) {
                    6f
                } else if (tick % .25f == 0f) {
                    5f
                } else {
                    3f
                }
                line(x + w * tick, y - h, x + w * tick, y + h, muted, 1f)
            }
            if (fraction != null) {
                line(x + w * fraction, y - 7, x + w * fraction, y + 7, white, 2f)
            }
            return
        }
        if (field.number == null) {
            line(x, y, x + w, y, track, 5f)
            return
        }
        val bands = when (field.band) {
            "temperature" -> listOf(
                Triple(0f, .7f, green),
                Triple(.72f, .84f, amber),
                Triple(.86f, 1f, red)
            )
            "battery" -> listOf(
                Triple(0f, .43f, red),
                Triple(.45f, .615f, amber),
                Triple(.617f, .783f, green),
                Triple(.8f, .86f, amber),
                Triple(.88f, 1f, red)
            )
            else -> listOf(Triple(0f, 1f, green))
        }
        bands.forEach { (start, end, color) -> line(x + w * start, y, x + w * end, y, color, 5f) }
        val low = if (field.band == "battery") 10.0 else field.low
        val high = if (field.band == "battery") 16.0 else field.high
        val fraction = gaugeFraction(field.number - low, high - low) ?: return
        line(x + w * fraction, y - 7, x + w * fraction, y + 7, white, 2f)
    }

    private fun fuelIcon(cx: Float, cy: Float) {
        ink.color = muted
        ink.style = Paint.Style.STROKE
        ink.strokeWidth = 1.7f
        canvas.drawRoundRect(RectF(cx - 8, cy - 12, cx + 5, cy + 10), 1.5f, 1.5f, ink)
        canvas.drawRect(RectF(cx - 5, cy - 9, cx + 2, cy - 2), ink)
        line(cx - 11, cy + 11, cx + 8, cy + 11, muted, 1.7f)
        line(cx + 5, cy - 8, cx + 12, cy - 2, muted, 1.7f)
        line(cx + 12, cy - 2, cx + 12, cy + 6, muted, 1.7f)
        arc(cx + 9, cy + 6, 3f, 0f, 180f, muted, 1.7f)
        line(cx + 6, cy + 6, cx + 6, cy + 1, muted, 1.7f)
        ink.style = Paint.Style.FILL
    }

    private fun dial(
        cx: Float,
        cy: Float,
        r: Float,
        value: Double?,
        maximum: Double,
        rpm: Boolean
    ) {
        arc(cx, cy, r, 140f, 260f, track, 6f)
        val fraction = gaugeFraction(value, maximum)
        if (fraction != null) {
            arc(cx, cy, r, 140f, 260 * fraction, green, 6f)
            if (!rpm) arc(cx, cy, r - 65, 140f, 260 * fraction, green, 4f)
        }
        if (rpm) arc(cx, cy, r, 140f + 260f * 5 / 6, 260f / 6, red, 7f)
        val divisions = if (rpm) 30 else 60
        for (i in 0..divisions) {
            val a = Math.toRadians((140 + 260.0 * i / divisions))
            val major = i % 5 == 0
            val color = if (rpm && i >= 25) red else muted
            line(
                cx + (r - 12) * cos(a).toFloat(),
                cy + (r - 12) * sin(a).toFloat(),
                cx + (r - if (major) 28 else 20) * cos(a).toFloat(),
                cy + (r - if (major) 28 else 20) * sin(a).toFloat(),
                color
            )
            if (major) {
                val label = if (rpm) i / 5 else i * 4
                text(
                    label.toString(),
                    cx + (r - 49) * cos(a).toFloat(),
                    cy + (r - 49) * sin(a).toFloat() + 6,
                    17f,
                    color,
                    Paint.Align.CENTER
                )
            }
        }
        text(
            clusterNumber(value),
            cx,
            cy + 15,
            if (r > 200) 108f else 75f,
            align = Paint.Align.CENTER
        )
        text(if (rpm) "rpm" else "km/h", cx, cy + 66, 22f, muted, Paint.Align.CENTER)
    }

    private fun rpmArc(value: Double?) {
        val cx = 236f
        val cy = 350f
        val r = 170f
        text("× 1000", cx, 157f, 13f, muted, Paint.Align.CENTER)
        arc(cx, cy, r, 180f, 180f, track, 6f)
        gaugeFraction(value, 6000.0)?.let { arc(cx, cy, r, 180f, 180 * it, green, 6f) }
        arc(cx, cy, r, 330f, 30f, red, 7f)
        for (i in 0..30) {
            val a = Math.toRadians(180 + 180.0 * i / 30)
            val major = i % 5 == 0
            val color = if (i >= 25) red else muted
            line(
                cx + (r - 9) * cos(a).toFloat(),
                cy + (r - 9) * sin(a).toFloat(),
                cx + (r - if (major) 23 else 16) * cos(a).toFloat(),
                cy + (r - if (major) 23 else 16) * sin(a).toFloat(),
                color
            )
            if (major) {
                text(
                    (i / 5).toString(),
                    cx + (r - 39) * cos(a).toFloat(),
                    cy + (r - 39) * sin(a).toFloat() + 6,
                    16f,
                    color,
                    Paint.Align.CENTER
                )
            }
        }
    }

    private fun gears(sample: TelemetrySample?) {
        val gear = sample?.cluster?.gear
        val actual = if (gear?.toIntOrNull() != null) "${gear}ª" else gear ?: "—"
        val bounds = Rect()
        ink.textSize = 50.6f
        ink.getTextBounds(actual, 0, actual.length, bounds)
        val baseline = 326f - bounds.bottom
        val center = baseline + bounds.exactCenterY()
        text(actual, 236f, baseline, 50.6f, align = Paint.Align.CENTER)
        val target = sample?.cluster?.suggestedGear
        val direction = gearDirection(gear, target) ?: return
        val suggested = "${target}ª"
        ink.textSize = 32.25f
        ink.getTextBounds(suggested, 0, suggested.length, bounds)
        text(
            suggested,
            236f + direction * 60,
            center - bounds.exactCenterY(),
            32.25f,
            green,
            Paint.Align.CENTER
        )
        val x = 236f + direction * 32
        val tip = center - direction * 10.5f
        line(x, center + direction * 10.5f, x, tip, green)
        line(x, tip, x - 6, tip + direction * 6, green)
        line(x, tip, x + 6, tip + direction * 6, green)
    }
}
