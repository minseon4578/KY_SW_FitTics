package com.example.myapplication.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.component.BmiLineChart
import com.example.myapplication.component.HealthCard
import com.example.myapplication.domain.BmiRecord

@Composable
fun HistoryTab(
    records: List<BmiRecord>
) {
    val latestRecord = records.lastOrNull()
    val highestBmi = records.maxOfOrNull { it.bmi }
    val lowestBmi = records.minOfOrNull { it.bmi }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 48.dp)
    ) {
        HealthCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ShowChart,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "BMI 변화 기록",
                    style = MaterialTheme.typography.titleLarge
                )
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

                Text(
                    text = "최근 BMI",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = latestRecord?.bmi?.toString() ?: "-",
                    style = MaterialTheme.typography.headlineLarge
                )

                Text(
                    text = latestRecord?.category ?: "-",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        if (records.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))

            HealthCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "기록 요약",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                SummaryRow(
                    label = "총 기록 수",
                    value = "${records.size}개"
                )

                SummaryRow(
                    label = "최고 BMI",
                    value = highestBmi?.toString() ?: "-"
                )

                SummaryRow(
                    label = "최저 BMI",
                    value = lowestBmi?.toString() ?: "-"
                )

                SummaryRow(
                    label = "최근 상태",
                    value = latestRecord?.category ?: "-"
                )
                SummaryRow(
                    label = "최근 BMR",
                    value = latestRecord?.let { "${it.bmr} kcal" } ?: "-"
                )

                SummaryRow(
                    label = "최근 체지방률",
                    value = latestRecord?.let { "${it.bodyFatRate}%" } ?: "-"
                )
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

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}