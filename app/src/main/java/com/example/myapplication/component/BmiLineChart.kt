package com.example.myapplication.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.BmiRecord
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun BmiLineChart(
    records: List<BmiRecord>,
    modifier: Modifier = Modifier
) {
    val bmiValues = records.map { it.bmi }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    if (bmiValues.isEmpty()) {
        return
    }

    val baseBmi = selectedIndex?.let { index ->
        bmiValues[index]
    } ?: bmiValues.last()

    val centerBmi = ((baseBmi / 5.0).roundToInt() * 5).toDouble()

    val minBmi = centerBmi - 10.0
    val maxBmi = centerBmi + 10.0
    val range = maxBmi - minBmi

    val yAxisLabels = listOf(
        maxBmi,
        centerBmi + 5.0,
        centerBmi,
        centerBmi - 5.0,
        minBmi
    )

    Column(
        modifier = modifier
    ) {
        selectedIndex?.let { index ->
            val selectedRecord = records[index]

            Text(
                text = "${index + 1}번째 기록  |  BMI ${selectedRecord.bmi}  |  ${selectedRecord.category}",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .width(42.dp)
                    .height(220.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                yAxisLabels.forEach { value ->
                    Text(
                        text = String.format("%.0f", value),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(16.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(bmiValues, minBmi, maxBmi) {
                            detectTapGestures { tapOffset ->
                                val chartWidth = size.width.toFloat()
                                val chartHeight = size.height.toFloat()

                                val points = bmiValues.mapIndexed { index, bmi ->
                                    val x = if (bmiValues.size == 1) {
                                        chartWidth / 2f
                                    } else {
                                        chartWidth / (bmiValues.size - 1) * index
                                    }

                                    val rawY = chartHeight - ((bmi - minBmi) / range * chartHeight).toFloat()
                                    val y = rawY.coerceIn(0f, chartHeight)

                                    Offset(x, y)
                                }

                                val nearestIndex = points
                                    .mapIndexed { index, point ->
                                        index to distanceBetween(tapOffset, point)
                                    }
                                    .minByOrNull { it.second }
                                    ?.first

                                if (nearestIndex != null) {
                                    val nearestPoint = points[nearestIndex]
                                    val distance = distanceBetween(tapOffset, nearestPoint)

                                    if (distance <= 70f) {
                                        selectedIndex = nearestIndex
                                    }
                                }
                            }
                        }
                ) {
                    val chartWidth = size.width
                    val chartHeight = size.height

                    val horizontalLineColor = androidx.compose.ui.graphics.Color.LightGray
                    val graphLineColor = androidx.compose.ui.graphics.Color(0xFF5167A3)
                    val normalPointColor = androidx.compose.ui.graphics.Color(0xFF5167A3)
                    val selectedPointColor = androidx.compose.ui.graphics.Color(0xFF1D4ED8)

                    val lineCount = yAxisLabels.size - 1

                    for (i in 0..lineCount) {
                        val y = chartHeight / lineCount * i

                        drawLine(
                            color = horizontalLineColor,
                            start = Offset(0f, y),
                            end = Offset(chartWidth, y),
                            strokeWidth = 1.5f
                        )
                    }

                    val points = bmiValues.mapIndexed { index, bmi ->
                        val x = if (bmiValues.size == 1) {
                            chartWidth / 2f
                        } else {
                            chartWidth / (bmiValues.size - 1) * index
                        }

                        val rawY = chartHeight - ((bmi - minBmi) / range * chartHeight).toFloat()
                        val y = rawY.coerceIn(0f, chartHeight)

                        Offset(x, y)
                    }

                    if (points.size >= 2) {
                        val path = Path()

                        points.forEachIndexed { index, point ->
                            if (index == 0) {
                                path.moveTo(point.x, point.y)
                            } else {
                                path.lineTo(point.x, point.y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = graphLineColor,
                            style = Stroke(width = 6f)
                        )
                    }

                    points.forEachIndexed { index, point ->
                        val isSelected = selectedIndex == index

                        if (isSelected) {
                            drawCircle(
                                color = selectedPointColor.copy(alpha = 0.20f),
                                radius = 22f,
                                center = point
                            )
                        }

                        drawCircle(
                            color = if (isSelected) selectedPointColor else normalPointColor,
                            radius = if (isSelected) 11f else 7f,
                            center = point
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 42.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "처음",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "최근",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun distanceBetween(
    first: Offset,
    second: Offset
): Float {
    val dx = first.x - second.x
    val dy = first.y - second.y
    return sqrt(dx * dx + dy * dy)
}