package com.example.myapplication.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assessment
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
import kotlin.math.roundToInt

@Composable
fun HistoryTab(
    records: List<BmiRecord>,
    todayMealCheckedCount: Int = 0,
    todayExerciseCompletedCount: Int = 0
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

    val daysDiff =
        if (compareRecord != null && latestRecord != null && compareRecord != latestRecord) {
            ((latestRecord.date - compareRecord.date) / (24 * 60 * 60 * 1000L)).toInt()
        } else {
            null
        }

    val compareLabel = when {
        daysDiff == null -> "이전 BMI"
        daysDiff >= 7 -> "일주일 전 BMI"
        daysDiff > 0 -> "${daysDiff}일 전 BMI"
        else -> "이전 BMI"
    }

    val bmiChange =
        if (compareRecord != null && latestRecord != null && compareRecord != latestRecord) {
            Math.round((latestRecord.bmi - compareRecord.bmi) * 10.0) / 10.0
        } else {
            null
        }

    val dateFormat = SimpleDateFormat("MM.dd", Locale.KOREA)
    val fullDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)

    val mealProgressPercent = ((todayMealCheckedCount.toDouble() / 4.0) * 100)
        .roundToInt()
        .coerceAtMost(100)

    val exerciseProgressPercent = ((todayExerciseCompletedCount.toDouble() / 3.0) * 100)
        .roundToInt()
        .coerceAtMost(100)

    val bmiScore = when (latestRecord?.category) {
        "정상" -> 20
        "저체중" -> 14
        "과체중" -> 12
        else -> 10
    }

    val mealScore = (mealProgressPercent * 0.4).roundToInt()
    val exerciseScore = (exerciseProgressPercent * 0.4).roundToInt()
    val healthScore = (mealScore + exerciseScore + bmiScore).coerceIn(0, 100)

    val activityGrade = when {
        healthScore >= 85 -> "A"
        healthScore >= 70 -> "B"
        healthScore >= 50 -> "C"
        else -> "-"
    }

    val estimatedGoal = when (latestRecord?.category) {
        "저체중" -> "체중 증량"
        "정상" -> "체중 유지"
        "과체중" -> "체중 감량"
        "비만" -> "체중 감량"
        else -> "-"
    }

    val coachMessage = when {
        latestRecord == null -> "BMI를 계산하면 오늘의 건강 피드백이 표시됩니다."
        healthScore >= 85 -> "오늘은 식단과 운동 흐름이 아주 좋습니다. 현재 루틴을 유지해도 좋습니다."
        exerciseProgressPercent < 67 && mealProgressPercent >= 75 -> "식단은 잘 지키고 있습니다. 가벼운 운동을 조금 추가하면 점수가 더 올라갑니다."
        mealProgressPercent < 50 && exerciseProgressPercent >= 67 -> "운동은 잘 진행 중입니다. 식사를 조금 더 체크하면 건강 리포트가 더 좋아집니다."
        mealProgressPercent == 0 && exerciseProgressPercent == 0 -> "오늘은 아직 기록이 없습니다. 식사나 운동을 하나만 체크해도 리포트가 시작됩니다."
        latestRecord.category == "저체중" -> "저체중 상태이므로 식사를 거르지 않고 단백질과 탄수화물을 함께 챙기는 것이 좋습니다."
        latestRecord.category == "과체중" || latestRecord.category == "비만" -> "체중 관리가 필요한 상태이므로 식사 체크와 가벼운 유산소 운동을 함께 유지하는 것이 좋습니다."
        else -> "오늘 기록이 시작됐습니다. 식단과 운동을 조금씩 채우면 건강 점수가 올라갑니다."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 48.dp)
    ) {
        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Assessment, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "오늘 건강 리포트", style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (latestRecord == null) {
                Text(
                    text = "아직 분석할 기록이 없습니다",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "계산 탭에서 BMI를 계산하면 오늘 리포트가 생성됩니다",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = fullDateFormat.format(Date(latestRecord.date)),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${healthScore}점",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineLarge
                )

                Text(
                    text = "오늘 건강 점수",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(12.dp))

                SummaryRow(label = "현재 상태", value = latestRecord.category)
                SummaryRow(label = "현재 BMI", value = latestRecord.bmi.toString())
                SummaryRow(label = "추천 목표", value = estimatedGoal)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = coachMessage,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Text(text = "오늘 실천 요약", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            SummaryRow(label = "식사 체크", value = "$todayMealCheckedCount / 4회")
            SummaryRow(label = "식단 진행률", value = "$mealProgressPercent%")
            SummaryRow(label = "운동 완료", value = "$todayExerciseCompletedCount / 3개")
            SummaryRow(label = "운동 목표 달성률", value = "$exerciseProgressPercent%")

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            SummaryRow(label = "식단 점수", value = "$mealScore / 40점")
            SummaryRow(label = "운동 점수", value = "$exerciseScore / 40점")
            SummaryRow(label = "BMI 점수", value = "$bmiScore / 20점")
            SummaryRow(label = "오늘 활동 등급", value = activityGrade)
        }

        Spacer(modifier = Modifier.height(20.dp))

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
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
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

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = compareRecord?.bmi?.toString() ?: "-",
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
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

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = latestRecord?.bmi?.toString() ?: "-",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

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
                                imageVector = if (isIncrease)
                                    Icons.Default.ArrowUpward
                                else
                                    Icons.Default.ArrowDownward,
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

                SummaryRow(label = "전체 기록 수", value = "${records.size}개")
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

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}