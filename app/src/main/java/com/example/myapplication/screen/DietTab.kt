package com.example.myapplication.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.component.HealthCard
import com.example.myapplication.domain.BmiRecord

enum class DietGoal(val label: String) {
    LOSE("체중 감량"),
    MAINTAIN("체중 유지"),
    GAIN("체중 증량")
}

data class DietMealPlan(
    val breakfast: String,
    val breakfastCal: Int,
    val breakfastProtein: Int,
    val breakfastReason: String,
    val lunch: String,
    val lunchCal: Int,
    val lunchProtein: Int,
    val lunchReason: String,
    val dinner: String,
    val dinnerCal: Int,
    val dinnerProtein: Int,
    val dinnerReason: String,
    val snack: String,
    val snackCal: Int,
    val snackProtein: Int,
    val snackReason: String
) {
    val totalCal: Int get() = breakfastCal + lunchCal + dinnerCal + snackCal
}

// 체중 증량 식단
val gainPlans = listOf(
    DietMealPlan(
        breakfast = "현미밥 + 스크램블에그 + 바나나 + 저지방우유",
        breakfastCal = 550, breakfastProtein = 22,
        breakfastReason = "탄수화물과 단백질을 함께 섭취해 체중 증가에 도움을 줍니다",
        lunch = "잡곡밥 + 닭가슴살구이 + 된장찌개 + 나물 반찬",
        lunchCal = 650, lunchProtein = 38,
        lunchReason = "고단백 식단으로 근육량을 늘리는 데 효과적입니다",
        dinner = "현미밥 + 소고기뭇국 + 두부조림 + 시금치나물",
        dinnerCal = 600, dinnerProtein = 32,
        dinnerReason = "양질의 단백질과 철분을 보충해줍니다",
        snack = "고구마 + 그릭요거트 + 견과류 한 줌",
        snackCal = 350, snackProtein = 12,
        snackReason = "건강한 탄수화물과 지방으로 칼로리를 보충합니다"
    ),
    DietMealPlan(
        breakfast = "오트밀 + 삶은 달걀 2개 + 블루베리 + 두유",
        breakfastCal = 500, breakfastProtein = 20,
        breakfastReason = "소화가 잘 되는 탄수화물로 하루를 시작합니다",
        lunch = "보리밥 + 생선구이 + 콩나물국 + 나물 반찬",
        lunchCal = 620, lunchProtein = 35,
        lunchReason = "생선의 양질의 단백질과 오메가3를 섭취합니다",
        dinner = "잡곡밥 + 닭곰탕 + 계란찜 + 깻잎나물",
        dinnerCal = 580, dinnerProtein = 30,
        dinnerReason = "담백한 단백질로 소화 부담 없이 영양을 보충합니다",
        snack = "통밀빵 + 저지방우유 + 사과",
        snackCal = 380, snackProtein = 10,
        snackReason = "식사 사이 공복을 채워 체중 증가를 돕습니다"
    ),
    DietMealPlan(
        breakfast = "고구마 + 삶은 달걀 2개 + 딸기 + 저지방 요거트",
        breakfastCal = 480, breakfastProtein = 18,
        breakfastReason = "천천히 소화되는 탄수화물로 포만감을 유지합니다",
        lunch = "현미밥 + 소고기미역국 + 두부 + 버섯볶음",
        lunchCal = 600, lunchProtein = 33,
        lunchReason = "소고기와 두부로 단백질을 이중으로 보충합니다",
        dinner = "잡곡밥 + 삼치구이 + 된장찌개 + 무나물",
        dinnerCal = 620, dinnerProtein = 34,
        dinnerReason = "지방과 단백질이 풍부한 생선으로 체중 증가에 도움을 줍니다",
        snack = "바나나 + 두유 + 견과류 한 줌",
        snackCal = 340, snackProtein = 11,
        snackReason = "칼로리가 높은 간식으로 하루 섭취량을 채웁니다"
    )
)

// 체중 유지 식단
val maintainPlans = listOf(
    DietMealPlan(
        breakfast = "통밀빵 + 스크램블에그 + 방울토마토 + 저지방우유",
        breakfastCal = 420, breakfastProtein = 18,
        breakfastReason = "균형 잡힌 탄단지로 하루를 시작합니다",
        lunch = "현미밥 + 생선구이 + 콩나물국 + 나물 반찬",
        lunchCal = 530, lunchProtein = 28,
        lunchReason = "균형 잡힌 한식으로 체중을 유지합니다",
        dinner = "잡곡밥 소량 + 닭가슴살구이 + 된장찌개 + 브로콜리무침",
        dinnerCal = 480, dinnerProtein = 30,
        dinnerReason = "저녁은 탄수화물을 줄이고 단백질 위주로 먹습니다",
        snack = "삶은 달걀 + 사과",
        snackCal = 200, snackProtein = 8,
        snackReason = "가볍게 단백질과 비타민을 보충합니다"
    ),
    DietMealPlan(
        breakfast = "오트밀 + 삶은 달걀 2개 + 블루베리",
        breakfastCal = 380, breakfastProtein = 16,
        breakfastReason = "항산화 성분과 단백질로 건강하게 시작합니다",
        lunch = "보리밥 + 닭가슴살 샐러드 + 순두부찌개 + 오이무침",
        lunchCal = 510, lunchProtein = 32,
        lunchReason = "고단백 저칼로리 식단으로 체중을 유지합니다",
        dinner = "현미밥 소량 + 연두부 + 삼치구이 + 시금치나물",
        dinnerCal = 460, dinnerProtein = 26,
        dinnerReason = "소화가 잘 되는 식품으로 가볍게 마무리합니다",
        snack = "그릭요거트 + 키위",
        snackCal = 190, snackProtein = 9,
        snackReason = "프로바이오틱스와 비타민C를 보충합니다"
    ),
    DietMealPlan(
        breakfast = "고구마 + 삶은 달걀 2개 + 저지방 요거트",
        breakfastCal = 400, breakfastProtein = 17,
        breakfastReason = "혈당을 안정적으로 유지하며 시작합니다",
        lunch = "잡곡밥 + 소고기미역국 + 두부 + 버섯볶음",
        lunchCal = 520, lunchProtein = 30,
        lunchReason = "철분과 단백질을 균형 있게 섭취합니다",
        dinner = "현미밥 소량 + 북어국 + 계란찜 + 깻잎나물",
        dinnerCal = 440, dinnerProtein = 25,
        dinnerReason = "저칼로리 고단백으로 저녁을 마무리합니다",
        snack = "바나나 + 두유",
        snackCal = 210, snackProtein = 7,
        snackReason = "에너지를 보충하고 포만감을 유지합니다"
    )
)

// 체중 감량 식단
val losePlans = listOf(
    DietMealPlan(
        breakfast = "오트밀 + 삶은 달걀 2개 + 방울토마토",
        breakfastCal = 300, breakfastProtein = 18,
        breakfastReason = "포만감이 오래 가고 혈당을 안정시킵니다",
        lunch = "현미밥 소량 + 닭가슴살구이 + 미역국 + 브로콜리무침",
        lunchCal = 380, lunchProtein = 35,
        lunchReason = "고단백 저칼로리로 근육을 유지하며 체중을 줄입니다",
        dinner = "고구마 + 연두부 + 황태구이 + 나물 반찬",
        dinnerCal = 320, dinnerProtein = 24,
        dinnerReason = "밥 없이도 포만감을 주는 건강한 저녁입니다",
        snack = "오이스틱 + 삶은 달걀",
        snackCal = 120, snackProtein = 8,
        snackReason = "칼로리 부담 없이 단백질을 보충합니다"
    ),
    DietMealPlan(
        breakfast = "고구마 + 삶은 달걀 2개 + 토마토",
        breakfastCal = 290, breakfastProtein = 16,
        breakfastReason = "혈당을 천천히 올려 식욕을 억제합니다",
        lunch = "현미밥 소량 + 참치샐러드 + 순두부찌개 + 숙주나물",
        lunchCal = 370, lunchProtein = 30,
        lunchReason = "채소와 단백질로 칼로리 없이 포만감을 줍니다",
        dinner = "단호박찜 + 닭가슴살구이 + 콩나물무침 + 시금치나물",
        dinnerCal = 310, dinnerProtein = 28,
        dinnerReason = "밥 없이 채소와 단백질만으로 구성한 저칼로리 저녁입니다",
        snack = "방울토마토 + 삶은 메추리알",
        snackCal = 110, snackProtein = 7,
        snackReason = "100kcal 이하의 가벼운 간식으로 공복을 해소합니다"
    ),
    DietMealPlan(
        breakfast = "오트밀 + 두부구이 + 블루베리",
        breakfastCal = 310, breakfastProtein = 17,
        breakfastReason = "항산화 성분과 단백질로 건강하게 시작합니다",
        lunch = "현미밥 소량 + 북어국 + 계란찜 + 오이무침",
        lunchCal = 360, lunchProtein = 28,
        lunchReason = "북어의 고단백으로 포만감을 높이고 칼로리를 낮춥니다",
        dinner = "고구마 + 두부조림 + 황태구이 + 깻잎나물",
        dinnerCal = 330, dinnerProtein = 26,
        dinnerReason = "식물성 단백질과 생선으로 균형 있게 마무리합니다",
        snack = "당근스틱 + 그릭요거트",
        snackCal = 115, snackProtein = 8,
        snackReason = "식이섬유와 단백질로 배고픔을 건강하게 해소합니다"
    )
)

fun getDefaultGoal(category: String): DietGoal {
    return when (category) {
        "저체중" -> DietGoal.GAIN
        "정상"  -> DietGoal.MAINTAIN
        "과체중" -> DietGoal.LOSE
        else   -> DietGoal.LOSE
    }
}

fun getPlansByGoal(goal: DietGoal): List<DietMealPlan> {
    return when (goal) {
        DietGoal.GAIN     -> gainPlans
        DietGoal.MAINTAIN -> maintainPlans
        DietGoal.LOSE     -> losePlans
    }
}

fun getCarbRatio(goal: DietGoal): Int = when (goal) {
    DietGoal.GAIN     -> 60
    DietGoal.MAINTAIN -> 55
    DietGoal.LOSE     -> 40
}

fun getProteinRatio(goal: DietGoal): Int = when (goal) {
    DietGoal.GAIN     -> 20
    DietGoal.MAINTAIN -> 20
    DietGoal.LOSE     -> 35
}

fun getFatRatio(goal: DietGoal): Int = when (goal) {
    DietGoal.GAIN     -> 20
    DietGoal.MAINTAIN -> 25
    DietGoal.LOSE     -> 25
}

@Composable
fun DietTab(
    latestRecord: BmiRecord?,
    tdee: Int?
) {
    val todayIndex = remember {
        java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
    }

    var selectedGoal by remember(latestRecord) {
        mutableStateOf(
            latestRecord?.let { getDefaultGoal(it.category) } ?: DietGoal.MAINTAIN
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 48.dp)
    ) {
        // 목표 선택 카드
        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Text(text = "목표 설정", style = MaterialTheme.typography.titleMedium)

            if (latestRecord != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "BMI ${latestRecord.bmi} (${latestRecord.category}) 기준 기본값이 설정되었습니다",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DietGoal.entries.forEach { goal ->
                    Button(
                        onClick = { selectedGoal = goal },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedGoal == goal)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (selectedGoal == goal)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(
                            text = goal.label,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 내 기준 정보 카드
        if (latestRecord != null) {
            val carbRatio = getCarbRatio(selectedGoal)
            val proteinRatio = getProteinRatio(selectedGoal)
            val fatRatio = getFatRatio(selectedGoal)

            HealthCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "오늘 내 기준 정보",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (selectedGoal) {
                            DietGoal.GAIN -> "체중 증량 기준 섭취량"
                            DietGoal.MAINTAIN -> "하루 권장 섭취량"
                            DietGoal.LOSE -> "감량 기준 섭취량"
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${
                            when (selectedGoal) {
                                DietGoal.GAIN -> (tdee ?: (latestRecord.bmr * 1.2).toInt()) + 300
                                DietGoal.MAINTAIN -> tdee ?: (latestRecord.bmr * 1.2).toInt()
                                DietGoal.LOSE -> (tdee ?: (latestRecord.bmr * 1.2).toInt()) - 500
                            }
                        } kcal",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NutritionRatioItem(label = "탄수화물", ratio = carbRatio)
                    NutritionRatioItem(label = "단백질", ratio = proteinRatio)
                    NutritionRatioItem(label = "지방", ratio = fatRatio)
                }

                if (tdee == null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "계산 탭에서 활동 수준을 선택하면 더 정확해져요",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 식단 추천 카드
        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Text(text = "식단 추천", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            if (latestRecord == null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
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
                val plans = getPlansByGoal(selectedGoal)
                val todayPlan = plans[todayIndex % plans.size]
                var showCalories by remember { mutableStateOf(true) }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "추천 식단 예시", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { showCalories = !showCalories }) {
                        Text(
                            text = if (showCalories) "칼로리 숨기기" else "칼로리 보기",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                DietMealRow(
                    title = "아침",
                    description = todayPlan.breakfast,
                    reason = todayPlan.breakfastReason,
                    cal = todayPlan.breakfastCal,
                    protein = todayPlan.breakfastProtein,
                    showCal = showCalories

                )
                DietMealRow(
                    title = "점심",
                    description = todayPlan.lunch,
                    reason = todayPlan.lunchReason,
                    cal = todayPlan.lunchCal,
                    protein = todayPlan.lunchProtein,
                    showCal = showCalories
                )
                DietMealRow(
                    title = "저녁",
                    description = todayPlan.dinner,
                    reason = todayPlan.dinnerReason,
                    cal = todayPlan.dinnerCal,
                    protein = todayPlan.dinnerProtein,
                    showCal = showCalories
                )
                DietMealRow(
                    title = "간식",
                    description = todayPlan.snack,
                    reason = todayPlan.snackReason,
                    cal = todayPlan.snackCal,
                    protein = todayPlan.snackProtein,
                    showCal = showCalories
                )
            }
        }
    }
}

@Composable
fun NutritionRatioItem(
    label: String,
    ratio: Int
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$ratio%",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun DietMealRow(
    title: String,
    description: String,
    reason: String,
    cal: Int,
    protein: Int,
    showCal: Boolean = true
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
        Spacer(modifier = Modifier.height(4.dp))
        if (showCal) {
            Text(
                text = "$cal kcal · 단백질 ${protein}g",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Text(
            text = "✓ $reason",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}