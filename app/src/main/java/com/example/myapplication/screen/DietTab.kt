package com.example.myapplication.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.component.HealthCard
import com.example.myapplication.domain.BmiRecord
import kotlin.math.abs
import kotlin.math.roundToInt

enum class DietGoal(val label: String) {
    LOSE("체중 감량"),
    MAINTAIN("체중 유지"),
    GAIN("체중 증량")
}

enum class RiceAmount(val label: String, val calorieAdjust: Int) {
    LESS("적게", -100),
    NORMAL("보통", 0),
    MORE("많이", 150)
}

enum class ProteinAddition(
    val label: String,
    val calorieAdjust: Int,
    val proteinAdjust: Int
) {
    NONE("없음", 0, 0),
    EGG("계란", 80, 6),
    CHICKEN("닭가슴살", 120, 23),
    PROTEIN("프로틴", 120, 20)
}

data class MealItem(
    val name: String,
    val calories: Int,
    val protein: Int,
    val reason: String
)

data class DietMealPlan(
    val breakfast: MealItem,
    val lunch: MealItem,
    val dinner: MealItem,
    val snack: MealItem
)

val gainPlans = listOf(
    DietMealPlan(
        breakfast = MealItem("김밥 1줄 + 삶은계란 2개 + 두유", 720, 30, "탄수화물과 단백질을 함께 보충하기 좋습니다"),
        lunch = MealItem("학식 일반식 + 밥 보통량 + 계란후라이 추가", 850, 35, "기존 식사를 유지하면서 섭취량을 자연스럽게 늘릴 수 있습니다"),
        dinner = MealItem("제육덮밥 + 된장국 또는 미소국", 850, 38, "증량에 필요한 열량을 확보하기 좋습니다"),
        snack = MealItem("바나나 + 프로틴 음료 또는 우유", 300, 22, "부족한 칼로리와 단백질을 간단히 채울 수 있습니다")
    ),
    DietMealPlan(
        breakfast = MealItem("토스트 2장 + 스크램블에그 + 우유", 620, 26, "준비 시간이 짧고 아침에 부담 없이 먹기 좋습니다"),
        lunch = MealItem("돈가스 정식 + 샐러드 + 밥", 950, 35, "활동량이 많은 날 에너지를 충분히 채울 수 있습니다"),
        dinner = MealItem("불고기덮밥 + 김치 + 계란찜", 820, 40, "탄수화물과 단백질 균형이 좋습니다"),
        snack = MealItem("그릭요거트 + 견과류 한 줌", 320, 18, "양은 적어도 칼로리와 단백질을 보충하기 좋습니다")
    ),
    DietMealPlan(
        breakfast = MealItem("삼각김밥 2개 + 삶은계란 1개 + 두유", 680, 24, "편의점에서도 쉽게 챙길 수 있는 현실적인 아침입니다"),
        lunch = MealItem("돼지고기 김치찌개 + 밥 + 계란말이", 850, 38, "일반 식당에서 쉽게 먹을 수 있고 단백질 보충도 가능합니다"),
        dinner = MealItem("닭갈비덮밥 또는 치킨마요덮밥 + 샐러드", 900, 42, "체중 증가에 필요한 열량을 확보하기 쉽습니다"),
        snack = MealItem("고구마 + 우유", 300, 10, "운동 전후 간식으로 부담 없이 먹기 좋습니다")
    )
)

val maintainPlans = listOf(
    DietMealPlan(
        breakfast = MealItem("삼각김밥 1개 + 삶은계란 1개 + 두유", 430, 19, "바쁜 아침에도 쉽게 챙길 수 있는 균형식입니다"),
        lunch = MealItem("학식 일반식 + 밥 보통량 + 국 + 반찬", 620, 26, "현실적으로 체중을 유지하기 좋습니다"),
        dinner = MealItem("김치찌개 + 밥 반 공기 + 계란말이", 520, 26, "포만감과 단백질을 함께 챙길 수 있습니다"),
        snack = MealItem("바나나 또는 그릭요거트", 150, 8, "과식 없이 가볍게 에너지를 보충할 수 있습니다")
    ),
    DietMealPlan(
        breakfast = MealItem("토스트 1장 + 우유 또는 두유", 330, 14, "꾸준히 먹기 쉬운 아침입니다"),
        lunch = MealItem("비빔밥 + 계란 추가", 650, 25, "채소, 밥, 단백질이 한 그릇에 들어 있어 균형이 좋습니다"),
        dinner = MealItem("된장찌개 + 밥 반 공기 + 두부 또는 생선구이", 560, 33, "체중 유지에 적합한 일반식입니다"),
        snack = MealItem("삶은계란 1개 + 과일", 180, 8, "단백질과 비타민을 부담 없이 보충할 수 있습니다")
    ),
    DietMealPlan(
        breakfast = MealItem("편의점 샌드위치 + 아메리카노", 380, 16, "학교나 출근길에 쉽게 구할 수 있습니다"),
        lunch = MealItem("제육볶음 정식 + 밥 반~보통량", 720, 35, "단백질과 탄수화물 균형이 좋은 편입니다"),
        dinner = MealItem("국밥 + 밥 반 공기 + 김치", 600, 28, "밥 양만 조절하면 유지식으로 활용 가능합니다"),
        snack = MealItem("프로틴 음료 또는 두유", 160, 18, "단백질이 부족한 날 간단히 보충할 수 있습니다")
    )
)

val losePlans = listOf(
    DietMealPlan(
        breakfast = MealItem("삼각김밥 1개 + 삶은계란 1개", 330, 13, "아침을 거르지 않으면서 점심 과식을 줄이는 데 도움이 됩니다"),
        lunch = MealItem("학식 일반식 + 밥 반 공기 + 단백질 반찬 위주", 520, 30, "평소 식사를 유지하되 밥 양을 줄입니다"),
        dinner = MealItem("김치찌개 또는 된장찌개 + 밥 반 공기 + 두부", 450, 24, "일반식에서 탄수화물 양만 조절합니다"),
        snack = MealItem("그릭요거트 또는 바나나 반 개", 120, 8, "폭식을 막고 공복감을 줄이는 데 좋습니다")
    ),
    DietMealPlan(
        breakfast = MealItem("삶은계란 2개 + 두유", 300, 20, "단백질 위주로 시작해 포만감을 오래 유지합니다"),
        lunch = MealItem("비빔밥 + 밥 조금 덜기 + 계란 추가", 600, 25, "채소와 단백질을 챙기면서 열량을 조절할 수 있습니다"),
        dinner = MealItem("닭가슴살 샐러드 + 고구마 작은 것 1개", 430, 35, "저녁 열량을 낮추면서 단백질을 확보할 수 있습니다"),
        snack = MealItem("방울토마토 또는 오이 + 삶은계란", 120, 8, "칼로리 부담을 줄이면서 허기를 달랠 수 있습니다")
    ),
    DietMealPlan(
        breakfast = MealItem("편의점 샌드위치 반쪽~1개 + 아메리카노", 350, 14, "현실적으로 구하기 쉽고 과한 아침을 피할 수 있습니다"),
        lunch = MealItem("국밥 + 밥 반 공기 + 건더기 위주", 600, 30, "외식 상황에서도 감량식으로 활용 가능합니다"),
        dinner = MealItem("순두부찌개 + 밥 반 공기", 450, 24, "포만감이 높고 단백질 섭취도 가능합니다"),
        snack = MealItem("프로틴 음료 또는 무가당 요거트", 160, 18, "단백질을 보충하면서 불필요한 간식을 줄일 수 있습니다")
    )
)

fun getDefaultGoal(category: String): DietGoal = when (category) {
    "저체중" -> DietGoal.GAIN
    "정상" -> DietGoal.MAINTAIN
    "과체중" -> DietGoal.LOSE
    else -> DietGoal.LOSE
}

fun getPlansByGoal(goal: DietGoal): List<DietMealPlan> = when (goal) {
    DietGoal.GAIN -> gainPlans
    DietGoal.MAINTAIN -> maintainPlans
    DietGoal.LOSE -> losePlans
}

fun getCarbRatio(goal: DietGoal): Int = when (goal) {
    DietGoal.GAIN -> 55
    DietGoal.MAINTAIN -> 50
    DietGoal.LOSE -> 40
}

fun getProteinRatio(goal: DietGoal): Int = when (goal) {
    DietGoal.GAIN -> 25
    DietGoal.MAINTAIN -> 25
    DietGoal.LOSE -> 35
}

fun getFatRatio(goal: DietGoal): Int = when (goal) {
    DietGoal.GAIN -> 20
    DietGoal.MAINTAIN -> 25
    DietGoal.LOSE -> 25
}

fun getTargetCalories(
    latestRecord: BmiRecord,
    tdee: Int?,
    goal: DietGoal
): Int {
    val baseTdee = tdee ?: (latestRecord.bmr * 1.2).roundToInt()
    return when (goal) {
        DietGoal.GAIN -> baseTdee + 300
        DietGoal.MAINTAIN -> baseTdee
        DietGoal.LOSE -> (baseTdee - 500).coerceAtLeast(1200)
    }
}

fun getTargetProtein(
    latestRecord: BmiRecord,
    goal: DietGoal
): Int {
    val weight = latestRecord.weightKg.toDoubleOrNull() ?: 70.0
    val proteinPerKg = when (goal) {
        DietGoal.GAIN -> 1.8
        DietGoal.MAINTAIN -> 1.4
        DietGoal.LOSE -> 1.6
    }
    return (weight * proteinPerKg).roundToInt()
}

fun getExerciseExtraCalories(exerciseBurnedCalories: Int): Int {
    return when {
        exerciseBurnedCalories >= 500 -> 300
        exerciseBurnedCalories >= 300 -> 200
        exerciseBurnedCalories >= 150 -> 100
        else -> 0
    }
}

fun getExerciseExtraProtein(exerciseBurnedCalories: Int): Int {
    return when {
        exerciseBurnedCalories >= 500 -> 20
        exerciseBurnedCalories >= 300 -> 15
        exerciseBurnedCalories >= 150 -> 10
        else -> 0
    }
}

fun getDefaultRiceAmount(goal: DietGoal, targetCalories: Int): RiceAmount {
    return when {
        goal == DietGoal.LOSE -> RiceAmount.LESS
        goal == DietGoal.GAIN && targetCalories >= 2300 -> RiceAmount.MORE
        goal == DietGoal.GAIN -> RiceAmount.NORMAL
        goal == DietGoal.MAINTAIN && targetCalories < 1800 -> RiceAmount.LESS
        goal == DietGoal.MAINTAIN && targetCalories >= 2300 -> RiceAmount.MORE
        else -> RiceAmount.NORMAL
    }
}

fun getAdjustedMealCalories(
    baseCalories: Int,
    mealType: String,
    riceAmount: RiceAmount,
    proteinAddition: ProteinAddition
): Int {
    val riceAdjust = if (mealType == "간식") 0 else riceAmount.calorieAdjust
    return (baseCalories + riceAdjust + proteinAddition.calorieAdjust).coerceAtLeast(0)
}

fun getAdjustedMealProtein(baseProtein: Int, proteinAddition: ProteinAddition): Int {
    return (baseProtein + proteinAddition.proteinAdjust).coerceAtLeast(0)
}

fun getPlanAdjustedCalories(
    plan: DietMealPlan,
    riceAmount: RiceAmount,
    proteinAddition: ProteinAddition
): Int {
    return getAdjustedMealCalories(plan.breakfast.calories, "아침", riceAmount, proteinAddition) +
            getAdjustedMealCalories(plan.lunch.calories, "점심", riceAmount, proteinAddition) +
            getAdjustedMealCalories(plan.dinner.calories, "저녁", riceAmount, proteinAddition) +
            getAdjustedMealCalories(plan.snack.calories, "간식", riceAmount, proteinAddition)
}

fun getSortedPlansByTargetCalories(
    plans: List<DietMealPlan>,
    targetCalories: Int,
    riceAmount: RiceAmount,
    proteinAddition: ProteinAddition
): List<DietMealPlan> {
    return plans.sortedBy { plan ->
        abs(getPlanAdjustedCalories(plan, riceAmount, proteinAddition) - targetCalories)
    }
}

@Composable
fun DietTab(
    latestRecord: BmiRecord?,
    tdee: Int?,
    exerciseBurnedCalories: Int = 0,
    onMealCheckedCountChanged: (Int) -> Unit = {}
) {
    val todayIndex = remember {
        java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
    }

    var selectedGoal by remember(latestRecord) {
        mutableStateOf(latestRecord?.let { getDefaultGoal(it.category) } ?: DietGoal.MAINTAIN)
    }

    var selectedPlanOffset by remember { mutableStateOf(0) }

    var selectedRiceAmount by remember(selectedGoal) {
        mutableStateOf<RiceAmount?>(null)
    }

    var selectedProteinAddition by remember {
        mutableStateOf(ProteinAddition.NONE)
    }

    val checkedMeals = remember(
        selectedGoal,
        selectedRiceAmount,
        selectedProteinAddition,
        exerciseBurnedCalories
    ) {
        mutableStateMapOf(
            "아침" to false,
            "점심" to false,
            "저녁" to false,
            "간식" to false
        )
    }

    val lockedMeals = remember(selectedGoal, selectedRiceAmount, selectedProteinAddition) {
        mutableStateMapOf<String, Triple<Int, Int, MealItem>>()
    }

    val mealCheckedCount = checkedMeals.values.count { it }

    LaunchedEffect(mealCheckedCount) {
        onMealCheckedCountChanged(mealCheckedCount)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 48.dp)
    ) {
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
                        onClick = {
                            selectedGoal = goal
                            selectedPlanOffset = 0
                            selectedRiceAmount = null
                            selectedProteinAddition = ProteinAddition.NONE
                        },
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

        if (latestRecord == null) {
            HealthCard(modifier = Modifier.fillMaxWidth()) {
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
            }
            return@Column
        }

        val targetCalories = getTargetCalories(latestRecord, tdee, selectedGoal)
        val targetProtein = getTargetProtein(latestRecord, selectedGoal)
        val exerciseExtraCalories = getExerciseExtraCalories(exerciseBurnedCalories)
        val exerciseExtraProtein = getExerciseExtraProtein(exerciseBurnedCalories)
        val adjustedTargetCalories = targetCalories + exerciseExtraCalories
        val adjustedTargetProtein = targetProtein + exerciseExtraProtein
        val effectiveRiceAmount = selectedRiceAmount ?: getDefaultRiceAmount(selectedGoal, adjustedTargetCalories)

        val plans = getSortedPlansByTargetCalories(
            plans = getPlansByGoal(selectedGoal),
            targetCalories = adjustedTargetCalories,
            riceAmount = effectiveRiceAmount,
            proteinAddition = selectedProteinAddition
        )

        val selectedPlanIndex = (todayIndex + selectedPlanOffset) % plans.size
        val todayPlan = plans[selectedPlanIndex]

        val breakfastCal = getAdjustedMealCalories(todayPlan.breakfast.calories, "아침", effectiveRiceAmount, selectedProteinAddition)
        val lunchCal = getAdjustedMealCalories(todayPlan.lunch.calories, "점심", effectiveRiceAmount, selectedProteinAddition)
        val dinnerCal = getAdjustedMealCalories(todayPlan.dinner.calories, "저녁", effectiveRiceAmount, selectedProteinAddition)
        val snackCal = getAdjustedMealCalories(todayPlan.snack.calories, "간식", effectiveRiceAmount, selectedProteinAddition)
        val breakfastProtein = getAdjustedMealProtein(todayPlan.breakfast.protein, selectedProteinAddition)
        val lunchProtein = getAdjustedMealProtein(todayPlan.lunch.protein, selectedProteinAddition)
        val dinnerProtein = getAdjustedMealProtein(todayPlan.dinner.protein, selectedProteinAddition)
        val snackProtein = getAdjustedMealProtein(todayPlan.snack.protein, selectedProteinAddition)

        val displayBreakfast = if (checkedMeals["아침"] == true) lockedMeals["아침"] else null
        val displayLunch = if (checkedMeals["점심"] == true) lockedMeals["점심"] else null
        val displayDinner = if (checkedMeals["저녁"] == true) lockedMeals["저녁"] else null
        val displaySnack = if (checkedMeals["간식"] == true) lockedMeals["간식"] else null

        val actualBreakfastCal = displayBreakfast?.first ?: breakfastCal
        val actualLunchCal = displayLunch?.first ?: lunchCal
        val actualDinnerCal = displayDinner?.first ?: dinnerCal
        val actualSnackCal = displaySnack?.first ?: snackCal
        val actualBreakfastProtein = displayBreakfast?.second ?: breakfastProtein
        val actualLunchProtein = displayLunch?.second ?: lunchProtein
        val actualDinnerProtein = displayDinner?.second ?: dinnerProtein
        val actualSnackProtein = displaySnack?.second ?: snackProtein

        val expectedCalories = breakfastCal + lunchCal + dinnerCal + snackCal
        val expectedProtein = breakfastProtein + lunchProtein + dinnerProtein + snackProtein

        val actualCalories =
            (if (checkedMeals["아침"] == true) actualBreakfastCal else 0) +
                    (if (checkedMeals["점심"] == true) actualLunchCal else 0) +
                    (if (checkedMeals["저녁"] == true) actualDinnerCal else 0) +
                    (if (checkedMeals["간식"] == true) actualSnackCal else 0)

        val actualProtein =
            (if (checkedMeals["아침"] == true) actualBreakfastProtein else 0) +
                    (if (checkedMeals["점심"] == true) actualLunchProtein else 0) +
                    (if (checkedMeals["저녁"] == true) actualDinnerProtein else 0) +
                    (if (checkedMeals["간식"] == true) actualSnackProtein else 0)

        val expectedCalorieRate = ((expectedCalories.toDouble() / adjustedTargetCalories) * 100).roundToInt()
        val expectedProteinRate = ((expectedProtein.toDouble() / adjustedTargetProtein) * 100).roundToInt()
        val actualCalorieRate = ((actualCalories.toDouble() / adjustedTargetCalories) * 100).roundToInt()
        val actualProteinRate = ((actualProtein.toDouble() / adjustedTargetProtein) * 100).roundToInt()
        val calorieGap = expectedCalories - adjustedTargetCalories

        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Text(text = "오늘 내 기준 정보", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "목표 섭취량: $adjustedTargetCalories kcal",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "목표 단백질: ${adjustedTargetProtein}g",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            if (exerciseBurnedCalories > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "운동 ${exerciseBurnedCalories} kcal 소모 반영 → +${exerciseExtraCalories} kcal, 단백질 +${exerciseExtraProtein}g",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutritionRatioItem(label = "탄수화물", ratio = getCarbRatio(selectedGoal))
                NutritionRatioItem(label = "단백질", ratio = getProteinRatio(selectedGoal))
                NutritionRatioItem(label = "지방", ratio = getFatRatio(selectedGoal))
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "추천 식단 기준 영양값", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "칼로리: $expectedCalories / $adjustedTargetCalories kcal (${expectedCalorieRate}%)",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "단백질: $expectedProtein / ${adjustedTargetProtein}g (${expectedProteinRate}%)",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (calorieGap > 0) "목표보다 약 ${calorieGap} kcal 높습니다"
                else "목표보다 약 ${abs(calorieGap)} kcal 낮습니다",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "체크한 실제 섭취", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "칼로리: $actualCalories / $adjustedTargetCalories kcal (${actualCalorieRate}%)",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "단백질: $actualProtein / ${adjustedTargetProtein}g (${actualProteinRate}%)",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall
            )
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

        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Text(text = "오늘 식단 설정", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "밥 양",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RiceAmount.entries.forEach { rice ->
                    FilterChip(
                        selected = effectiveRiceAmount == rice,
                        onClick = {
                            selectedRiceAmount = rice
                            selectedPlanOffset = 0
                        },
                        label = {
                            Text(text = rice.label, style = MaterialTheme.typography.bodySmall)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "단백질 보충",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProteinAddition.entries.forEach { protein ->
                    FilterChip(
                        selected = selectedProteinAddition == protein,
                        onClick = {
                            selectedProteinAddition = protein
                            selectedPlanOffset = 0
                        },
                        label = {
                            Text(
                                text = protein.label,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "현실적인 추천 식단", style = MaterialTheme.typography.titleLarge)
                TextButton(
                    onClick = {
                        selectedPlanOffset = (selectedPlanOffset + 1) % plans.size
                    }
                ) {
                    Text(text = "다른 식단 보기", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "목표 칼로리에 가까운 식단부터 보여줍니다",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(12.dp))

            DietMealRow(
                title = "아침",
                meal = displayBreakfast?.third ?: todayPlan.breakfast,
                cal = actualBreakfastCal,
                protein = actualBreakfastProtein,
                checked = checkedMeals["아침"] == true,
                onCheckedChange = { checked ->
                    checkedMeals["아침"] = checked
                    if (checked) lockedMeals["아침"] = Triple(breakfastCal, breakfastProtein, todayPlan.breakfast)
                    else lockedMeals.remove("아침")
                }
            )
            DietMealRow(
                title = "점심",
                meal = displayLunch?.third ?: todayPlan.lunch,
                cal = actualLunchCal,
                protein = actualLunchProtein,
                checked = checkedMeals["점심"] == true,
                onCheckedChange = { checked ->
                    checkedMeals["점심"] = checked
                    if (checked) lockedMeals["점심"] = Triple(lunchCal, lunchProtein, todayPlan.lunch)
                    else lockedMeals.remove("점심")
                }
            )
            DietMealRow(
                title = "저녁",
                meal = displayDinner?.third ?: todayPlan.dinner,
                cal = actualDinnerCal,
                protein = actualDinnerProtein,
                checked = checkedMeals["저녁"] == true,
                onCheckedChange = { checked ->
                    checkedMeals["저녁"] = checked
                    if (checked) lockedMeals["저녁"] = Triple(dinnerCal, dinnerProtein, todayPlan.dinner)
                    else lockedMeals.remove("저녁")
                }
            )
            DietMealRow(
                title = "간식",
                meal = displaySnack?.third ?: todayPlan.snack,
                cal = actualSnackCal,
                protein = actualSnackProtein,
                checked = checkedMeals["간식"] == true,
                onCheckedChange = { checked ->
                    checkedMeals["간식"] = checked
                    if (checked) lockedMeals["간식"] = Triple(snackCal, snackProtein, todayPlan.snack)
                    else lockedMeals.remove("간식")
                }
            )
        }
    }
}

@Composable
fun NutritionRatioItem(label: String, ratio: Int) {
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
    meal: MealItem,
    cal: Int,
    protein: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = meal.name,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "기준 영양값: 약 ${cal} kcal · 단백질 ${protein}g",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "✓ ${meal.reason}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { onCheckedChange(!checked) }) {
                Icon(
                    imageVector = if (checked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "식사 체크",
                    tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}