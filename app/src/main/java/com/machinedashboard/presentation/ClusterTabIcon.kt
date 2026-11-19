package com.machinedashboard.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun ClusterTabIcon(index: Int, color: Color) {
    Canvas(Modifier.size(14.dp)) {
        val k = size.width / 24
        fun stroke(vararg points: Pair<Float, Float>) {
            points.toList().zipWithNext().forEach { (a, b) ->
                drawLine(
                    color,
                    Offset(a.first * k, a.second * k),
                    Offset(b.first * k, b.second * k),
                    1.5f * k
                )
            }
        }
        when (index) {
            0 -> stroke(
                1f to 11f,
                12f to 1f,
                23f to 11f,
                20f to 11f,
                20f to 23f,
                4f to 23f,
                4f to 11f
            )
            1 -> {
                drawArc(
                    color,
                    180f,
                    180f,
                    false,
                    Offset(1f * k, 1f * k),
                    androidx.compose.ui.geometry.Size(22f * k, 22f * k),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(1.5f * k)
                )
                stroke(1f to 12f, 1f to 23f, 23f to 23f, 23f to 12f)
                stroke(12f to 16f, 18f to 7f)
            }
            2 -> {
                stroke(
                    1f to 4f, 8f to 1f, 16f to 5f, 23f to 2f, 23f to 21f,
                    16f to 24f, 8f to 20f, 1f to 23f, 1f to 4f
                )
                stroke(8f to 1f, 8f to 20f)
                stroke(16f to 5f, 16f to 24f)
            }
            3 -> {
                stroke(4f to 1f, 20f to 1f, 20f to 23f, 4f to 23f, 4f to 1f)
                for (y in listOf(7f, 12f, 17f)) stroke(8f to y, 16f to y)
            }
            else -> for ((y, x) in listOf(4f to 6f, 12f to 17f, 21f to 10f)) {
                stroke(0f to y, 24f to y)
                drawCircle(color, 2.7f * k, Offset(x * k, y * k))
            }
        }
    }
}
