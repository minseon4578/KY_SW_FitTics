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
import kotlin.math.abs

data class DietMealPlan(
    val breakfast: String,
    val breakfastCal: Int,
    val lunch: String,
    val lunchCal: Int,
    val dinner: String,
    val dinnerCal: Int,
    val snack: String,
    val snackCal: Int
) {
    val totalCal: Int get() = breakfastCal + lunchCal + dinnerCal + snackCal
}

val underweightPlans = listOf(
    DietMealPlan(
        breakfast = "현미밥 + 스크램블에그 + 바나나 + 저지방우유",
        breakfastCal = 550,
        lunch = "잡곡밥 + 닭가슴살구이 + 된장찌개 + 나물 반찬",
        lunchCal = 650,
        dinner = "현미밥 + 소고기뭇국 + 두부조림 + 시금치나물",
        dinnerCal = 600,
        snack = "고구마 + 그릭요거트 + 견과류 한 줌",
        snackCal = 350
    ),
    DietMealPlan(
        breakfast = "오트밀 + 삶은 달걀 2개 + 블루베리 + 두유",
        breakfastCal = 500,
        lunch = "보리밥 + 생선구이 + 콩나물국 + 나물 반찬",
        lunchCal = 620,
        dinner = "잡곡밥 + 닭곰탕 + 계란찜 + 깻잎나물",
        dinnerCal = 580,
        snack = "통밀빵 + 저지방우유 + 사과",
        snackCal = 380
    ),
    DietMealPlan(
        breakfast = "고구마 + 삶은 달걀 2개 + 딸기 + 저지방 요거트",
        breakfastCal = 480,
        lunch = "현미밥 + 소고기미역국 + 두부 + 버섯볶음",
        lunchCal = 600,
        dinner = "잡곡밥 + 삼치구이 + 된장찌개 + 무나물",
        dinnerCal = 620,
        snack = "바나나 + 두유 + 견과류 한 줌",
        snackCal = 340
    )
)

val normalPlans = listOf(
    DietMealPlan(
        breakfast = "통밀빵 + 스크램블에그 + 방울토마토 + 저지방우유",
        breakfastCal = 420,
        lunch = "현미밥 + 생선구이 + 콩나물국 + 나물 반찬",
        lunchCal = 530,
        dinner = "잡곡밥 소량 + 닭가슴살구이 + 된장찌개 + 브로콜리무침",
        dinnerCal = 480,
        snack = "삶은 달걀 + 사과",
        snackCal = 200
    ),
    DietMealPlan(
        breakfast = "오트밀 + 삶은 달걀 2개 + 블루베리",
        breakfastCal = 380,
        lunch = "보리밥 + 닭가슴살 샐러드 + 순두부찌개 + 오이무침",
        lunchCal = 510,
        dinner = "현미밥 소량 + 연두부 + 삼치구이 + 시금치나물",
        dinnerCal = 460,
        snack = "그릭요거트 + 키위",
        snackCal = 190
    ),
    DietMealPlan(
        breakfast = "고구마 + 삶은 달걀 2개 + 저지방 요거트",
        breakfastCal = 400,
        lunch = "잡곡밥 + 소고기미역국 + 두부 + 버섯볶음",
        lunchCal = 520,
        dinner = "현미밥 소량 + 북어국 + 계란찜 + 깻잎나물",
        dinnerCal = 440,
        snack = "바나나 + 두유",
        snackCal = 210
    )
)

val overweightPlans = listOf(
    DietMealPlan(
        breakfast = "오트밀 + 삶은 달걀 2개 + 방울토마토",
        breakfastCal = 350,
        lunch = "현미밥 소량 + 닭가슴살구이 + 순두부찌개 + 브로콜리",
        lunchCal = 450,
        dinner = "고구마 + 두부조림 + 북어국 + 나물 반찬",
        dinnerCal = 380,
        snack = "삶은 달걀 + 오이스틱",
        snackCal = 150
    ),
    DietMealPlan(
        breakfast = "고구마 + 그릭요거트 + 키위",
        breakfastCal = 330,
        lunch = "잡곡밥 소량 + 참치샐러드 + 콩나물국 + 오이무침",
        lunchCal = 430,
        dinner = "현미밥 소량 + 황태구이 + 된장찌개 + 시금치나물",
        dinnerCal = 390,
        snack = "방울토마토 + 스트링치즈",
        snackCal = 140
    ),
    DietMealPlan(
        breakfast = "통밀빵 + 삶은 달걀 2개 + 토마토",
        breakfastCal = 360,
        lunch = "현미밥 소량 + 닭가슴살 샐러드 + 미역줄기볶음 + 숙주나물",
        lunchCal = 420,
        dinner = "고구마 + 연두부 + 삼치구이 + 브로콜리무침",
        dinnerCal = 370,
        snack = "당근스틱 + 삶은 메추리알",
        snackCal = 130
    )
)

val obesePlans = listOf(
    DietMealPlan(
        breakfast = "오트밀 + 삶은 달걀 2개 + 방울토마토",
        breakfastCal = 300,
        lunch = "현미밥 소량 + 닭가슴살구이 + 미역국 + 브로콜리무침",
        lunchCal = 380,
        dinner = "고구마 + 연두부 + 황태구이 + 나물 반찬",
        dinnerCal = 320,
        snack = "오이스틱 + 삶은 달걀",
        snackCal = 120
    ),
    DietMealPlan(
        breakfast = "고구마 + 삶은 달걀 2개 + 토마토",
        breakfastCal = 290,
        lunch = "현미밥 소량 + 참치샐러드 + 순두부찌개 + 숙주나물",
        lunchCal = 370,
        dinner = "단호박찜 + 닭가슴살구이 + 콩나물무침 + 시금치나물",
        dinnerCal = 310,
        snack = "방울토마토 + 삶은 메추리알",
        snackCal = 110
    ),
    DietMealPlan(
        breakfast = "오트밀 + 두부구이 + 블루베리",
        breakfastCal = 310,
        lunch = "현미밥 소량 + 북어국 + 계란찜 + 오이무침",
        lunchCal = 360,
        dinner = "고구마 + 두부조림 + 황태구이 + 깻잎나물",
        dinnerCal = 330,
        snack = "당근스틱 + 그릭요거트",
        snackCal = 115
    )
)

fun getDietPlans(category: String): List<DietMealPlan> {
    return when (category) {
        "저체중" -> underweightPlans
        "정상"  -> normalPlans
        "과체중" -> overweightPlans
        else   -> obesePlans
    }
}

fun getTargetCalories(record: BmiRecord, tdee: Int?): Int {
    val base = tdee ?: (record.bmr * 1.2).toInt()
    return when (record.category) {
        "저체중" -> base + 300
        "정상"  -> base
        "과체중" -> base - 300
        else   -> base - 500
    }
}

fun selectBestPlan(plans: List<DietMealPlan>, targetCal: Int, dayIndex: Int): DietMealPlan {
    val sorted = plans.sortedBy { abs(it.totalCal - targetCal) }
    return sorted[dayIndex % sorted.size]
}

fun getDietGuide(record: BmiRecord): String {
    return when (record.category) {
        "저체중" -> "체중 증가와 근육량 향상을 위해 단백질과 탄수화물을 충분히 섭취하는 식단이 적합합니다. 하루 3끼를 규칙적으로 드시고 간식도 챙겨드세요."
        "정상"  -> "현재 체중을 유지할 수 있도록 균형 잡힌 식사를 유지하는 것이 좋습니다."
        "과체중" -> "탄수화물 섭취를 줄이고 단백질을 늘려 포만감을 유지하면서 체중을 조절하는 것이 좋습니다."
        else   -> "급격한 절식보다 규칙적인 식사를 유지하면서 탄수화물을 줄이고 단백질을 늘리는 것이 효과적입니다."
    }
}

@Composable
fun DietTab(
    latestRecord: BmiRecord?,
    tdee: Int?
) {
    val todayIndex = remember {
        java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
    }

    var showCalories by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 48.dp)
    ) {
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
                val targetCal = getTargetCalories(latestRecord, tdee)
                val plans = getDietPlans(latestRecord.category)
                val todayPlan = selectBestPlan(plans, targetCal, todayIndex)

                Text(text = "오늘의 식단 방향", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = getDietGuide(latestRecord),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(20.dp))

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

                if (showCalories) {
                    Text(
                        text = "총 ${todayPlan.totalCal} kcal",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                DietMealRow(title = "아침", description = todayPlan.breakfast, cal = todayPlan.breakfastCal, showCal = showCalories)
                DietMealRow(title = "점심", description = todayPlan.lunch, cal = todayPlan.lunchCal, showCal = showCalories)
                DietMealRow(title = "저녁", description = todayPlan.dinner, cal = todayPlan.dinnerCal, showCal = showCalories)
                DietMealRow(title = "간식", description = todayPlan.snack, cal = todayPlan.snackCal, showCal = showCalories)
            }
        }
    }
}

@Composable
fun DietMealRow(
    title: String,
    description: String,
    cal: Int,
    showCal: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            if (showCal) {
                Text(
                    text = "${cal} kcal",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}