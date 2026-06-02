package com.example.myapplication.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.component.BmiLineChart
import com.example.myapplication.component.HealthCard
import com.example.myapplication.domain.BmiRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryTab(
    records: List<BmiRecord>
) {
    val latestRecord = records.lastOrNull()
    val highestBmi = records.maxOfOrNull { it.bmi }
    val lowestBmi = records.minOfOrNull { it.bmi }

    val oneWeekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L

    val compareRecord = records
        .filter { it.date <= oneWeekAgo }
        .maxByOrNull { it.date }
        ?: records.firstOrNull()?.let { first ->
            if (first != latestRecord) first else null
        }

    val daysDiff = if (compareRecord != null && latestRecord != null && compareRecord != latestRecord) {
        ((latestRecord.date - compareRecord.date) / (24 * 60 * 60 * 1000L)).toInt()
    } else null

    val compareLabel = when {
        daysDiff == null -> "이전 BMI"
        daysDiff >= 7 -> "일주일 전 BMI"
        daysDiff > 0 -> "${daysDiff}일 전 BMI"
        else -> "이전 BMI"
    }

    val bmiChange = if (compareRecord != null && latestRecord != null && compareRecord != latestRecord) {
        Math.round((latestRecord.bmi - compareRecord.bmi) * 10.0) / 10.0
    } else null

    val dateFormat = SimpleDateFormat("MM.dd", Locale.KOREA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 48.dp)
    ) {
        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ShowChart, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "BMI 변화 기록", style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (records.isEmpty()) {
                Text(
                    text = "아직 기록이 없습니다",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(120.dp))
                Text(
                    text = "BMI를 계산하면 자동으로 그래프가 생성됩니다",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(120.dp))
            } else {
                Text(
                    text = "BMI 계산 기록을 기준으로 변화 추이를 보여줍니다",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(20.dp))

                BmiLineChart(
                    records = records,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 이전 BMI 카드
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = compareLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (compareRecord != null) {
                                    Text(
                                        text = dateFormat.format(Date(compareRecord.date)),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = compareRecord?.bmi?.toString() ?: "-",
                                style = MaterialTheme.typography.headlineLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (compareRecord != null) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        text = compareRecord.category,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            } else {
                                Text(
                                    text = "-",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    // 최근 BMI 카드
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "최근 BMI",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (latestRecord != null) {
                                    Text(
                                        text = dateFormat.format(Date(latestRecord.date)),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = latestRecord?.bmi?.toString() ?: "-",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (latestRecord != null) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.tertiaryContainer
                                ) {
                                    Text(
                                        text = latestRecord.category,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            } else {
                                Text(
                                    text = "-",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }

                // 변화량
                if (bmiChange != null) {
                    Spacer(modifier = Modifier.height(12.dp))

                    val isIncrease = bmiChange > 0
                    val changeColor = if (isIncrease) Color(0xFFE57373) else Color(0xFF81C784)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isIncrease) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = changeColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (daysDiff != null && daysDiff >= 7) "1주일간 변화"
                                else if (daysDiff != null) "${daysDiff}일간 변화"
                                else "변화",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = if (isIncrease) "+$bmiChange" else "$bmiChange",
                            style = MaterialTheme.typography.bodyMedium,
                            color = changeColor
                        )
                    }
                }
            }
        }

        if (records.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))

            HealthCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = "기록 요약", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(16.dp))

                SummaryRow(label = "최고 BMI", value = highestBmi?.toString() ?: "-")
                SummaryRow(label = "최저 BMI", value = lowestBmi?.toString() ?: "-")
                SummaryRow(label = "최근 상태", value = latestRecord?.category ?: "-")
                SummaryRow(label = "최근 체지방률", value = latestRecord?.let { "${it.bodyFatRate}%" } ?: "-")
            }
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}