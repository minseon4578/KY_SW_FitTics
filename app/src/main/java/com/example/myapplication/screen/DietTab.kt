package com.example.myapplication.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.component.HealthCard
import com.example.myapplication.domain.BmiRecord

enum class ActivityLevel(val label: String, val factor: Double, val description: String) {
    SEDENTARY("거의 안 움직임", 1.2, "앉아서 생활, 운동 거의 없음"),
    LIGHT("가벼운 활동", 1.375, "주 1~3회 가벼운 운동"),
    MODERATE("보통 활동", 1.55, "주 3~5회 운동"),
    ACTIVE("활발한 활동", 1.725, "주 6~7회 강도 높은 운동"),
    VERY_ACTIVE("매우 활발", 1.9, "하루 2번 운동, 육체노동")
}

@Composable
fun DietTab(
    latestRecord: BmiRecord?
) {
    var breakfastCalText by remember { mutableStateOf("") }
    var lunchCalText by remember { mutableStateOf("") }
    var dinnerCalText by remember { mutableStateOf("") }
    var snackCalText by remember { mutableStateOf("") }
    var selectedActivity by remember { mutableStateOf<ActivityLevel?>(null) }

    val breakfastCal = breakfastCalText.toIntOrNull() ?: 0
    val lunchCal = lunchCalText.toIntOrNull() ?: 0
    val dinnerCal = dinnerCalText.toIntOrNull() ?: 0
    val snackCal = snackCalText.toIntOrNull() ?: 0
    val totalCal = breakfastCal + lunchCal + dinnerCal + snackCal

    val bmr = latestRecord?.bmr ?: 0
    val tdee = selectedActivity?.let { (bmr * it.factor).toInt() }

    // 목표 칼로리: 활동 수준 선택 시 TDEE, 아니면 BMR
    val targetCal = tdee ?: bmr

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 48.dp)
    ) {
        // 칼로리 목표 카드
        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "오늘의 칼로리",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (latestRecord == null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "신체 데이터를 먼저 계산해주세요",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "BMR을 기반으로 목표 칼로리를 안내해드립니다",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                // BMR / TDEE 표시
                DietSummaryRow(
                    label = "최소 섭취량 (BMR)",
                    value = "${bmr} kcal"
                )
                if (tdee != null) {
                    DietSummaryRow(
                        label = "권장 섭취량 (TDEE)",
                        value = "${tdee} kcal"
                    )
                } else {
                    Text(
                        text = "아래에서 활동 수준을 선택하면 권장 섭취량이 계산됩니다",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(12.dp))

                DietSummaryRow(
                    label = "오늘 섭취 칼로리",
                    value = "${totalCal} kcal"
                )

                val remaining = targetCal - totalCal
                val isOver = remaining < 0

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isOver) "초과: ${-remaining} kcal" else "남은 칼로리: ${remaining} kcal",
                    color = if (isOver) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                val progress = if (targetCal > 0) {
                    (totalCal.toFloat() / targetCal.toFloat()).coerceIn(0f, 1f)
                } else 0f

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    color = if (isOver) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 활동 수준 선택 카드
        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "활동 수준 선택",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "선택하면 권장 섭취량(TDEE)이 자동 계산됩니다",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            ActivityLevel.entries.forEach { level ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedActivity == level,
                        onClick = { selectedActivity = level }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = level.label,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = level.description,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 식사별 칼로리 입력 카드
        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "식사별 칼로리 입력",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            DietCalorieInputRow(label = "아침", value = breakfastCalText, onValueChange = { breakfastCalText = it })
            Spacer(modifier = Modifier.height(12.dp))
            DietCalorieInputRow(label = "점심", value = lunchCalText, onValueChange = { lunchCalText = it })
            Spacer(modifier = Modifier.height(12.dp))
            DietCalorieInputRow(label = "저녁", value = dinnerCalText, onValueChange = { dinnerCalText = it })
            Spacer(modifier = Modifier.height(12.dp))
            DietCalorieInputRow(label = "간식", value = snackCalText, onValueChange = { snackCalText = it })

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "합계", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${totalCal} kcal",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 식단 추천 카드
        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "식단 추천",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (latestRecord == null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "신체 데이터를 먼저 계산해주세요",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "BMI, BMR, 체지방률을 바탕으로 식단을 추천합니다",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Text(
                    text = "현재 상태: ${latestRecord.category}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "기초대사량: ${latestRecord.bmr} kcal",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "오늘의 식단 방향", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = getDietGuide(latestRecord),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "추천 식단 예시", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(12.dp))

                DietMealRow(title = "아침", description = "삶은 달걀, 바나나, 오트밀 또는 통밀빵")
                DietMealRow(title = "점심", description = "현미밥, 닭가슴살 또는 생선, 채소 반찬")
                DietMealRow(title = "저녁", description = "단백질 위주의 식사와 가벼운 탄수화물")
                DietMealRow(title = "간식", description = "견과류, 그릭요거트, 과일 중 선택")

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "영양소 권장량", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (tdee != null) "TDEE 기준" else "BMR 기준 (활동 수준 선택 시 더 정확해집니다)",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                DietSummaryRow(label = "탄수화물 (50%)", value = "${(targetCal * 0.5 / 4).toInt()} g")
                DietSummaryRow(label = "단백질 (25%)", value = "${(targetCal * 0.25 / 4).toInt()} g")
                DietSummaryRow(label = "지방 (25%)", value = "${(targetCal * 0.25 / 9).toInt()} g")
            }
        }
    }
}

@Composable
fun DietCalorieInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.width(56.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = {
                Text(text = "kcal", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        )
    }
}

@Composable
fun DietSummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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

@Composable
fun DietMealRow(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

fun getDietGuide(record: BmiRecord): String {
    return when (record.category) {
        "저체중" -> "체중 증가와 근육량 향상을 위해 단백질과 탄수화물을 충분히 섭취하는 식단이 적합합니다."
        "정상"  -> "현재 체중을 유지할 수 있도록 균형 잡힌 식사를 유지하는 것이 좋습니다."
        "과체중" -> "섭취 칼로리를 조금 줄이고 단백질은 유지하면서 탄수화물과 당류 섭취를 조절하는 것이 좋습니다."
        else   -> "급격한 절식보다 규칙적인 식사와 저칼로리 고단백 식단을 유지하는 것이 좋습니다."
    }
}