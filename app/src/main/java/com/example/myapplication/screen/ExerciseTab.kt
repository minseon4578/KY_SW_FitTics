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
import com.example.myapplication.BuildConfig
import com.example.myapplication.component.HealthCard
import com.example.myapplication.domain.BmiRecord
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class ExerciseGoal(val label: String) {
    LOSE("체중 감량"),
    MAINTAIN("체중 유지"),
    GAIN("근육 증량")
}

enum class ExerciseLevel(val label: String) {
    BEGINNER("초급"),
    INTERMEDIATE("중급"),
    ADVANCED("고급")
}

data class Exercise(
    val name: String,
    val description: String,
    val calories: Int,
    val duration: String
)

fun getDefaultExerciseGoal(category: String): ExerciseGoal {
    return when (category) {
        "저체중" -> ExerciseGoal.GAIN
        "정상" -> ExerciseGoal.MAINTAIN
        "과체중" -> ExerciseGoal.LOSE
        else -> ExerciseGoal.LOSE
    }
}

fun extractMinutes(duration: String): Int {
    return Regex("\\d+")
        .find(duration)
        ?.value
        ?.toIntOrNull()
        ?: 30
}

fun getMetValue(exerciseName: String): Double {
    val name = exerciseName.lowercase()

    return when {
        name.contains("빠르게 걷기") || name.contains("파워워킹") -> 4.5
        name.contains("걷기") || name.contains("산책") -> 3.5
        name.contains("조깅") -> 7.0
        name.contains("러닝") || name.contains("달리기") || name.contains("인터벌") -> 8.0
        name.contains("자전거") -> 6.8
        name.contains("수영") -> 6.0
        name.contains("줄넘기") -> 10.0
        name.contains("버피") -> 10.0
        name.contains("스쿼트") -> 5.0
        name.contains("런지") -> 4.5
        name.contains("데드리프트") -> 6.0
        name.contains("벤치") || name.contains("벤치프레스") -> 3.5
        name.contains("바벨 로우") || name.contains("로우") -> 4.0
        name.contains("오버헤드") || name.contains("숄더프레스") -> 4.0
        name.contains("푸시업") || name.contains("팔굽혀펴기") -> 4.0
        name.contains("플랭크") -> 3.0
        name.contains("요가") -> 2.5
        name.contains("스트레칭") -> 2.3
        name.contains("필라테스") -> 3.0
        name.contains("복근") || name.contains("크런치") || name.contains("싯업") -> 3.8
        else -> 4.0
    }
}

fun getLevelMultiplier(level: ExerciseLevel): Double {
    return when (level) {
        ExerciseLevel.BEGINNER -> 0.9
        ExerciseLevel.INTERMEDIATE -> 1.0
        ExerciseLevel.ADVANCED -> 1.15
    }
}

fun getAgeMultiplier(ageText: String): Double {
    val age = ageText.toIntOrNull() ?: return 1.0

    return when {
        age < 20 -> 1.05
        age in 20..39 -> 1.0
        age in 40..59 -> 0.95
        else -> 0.9
    }
}

fun getGenderMultiplier(gender: String): Double {
    return when {
        gender.contains("남") || gender.equals("male", ignoreCase = true) -> 1.05
        gender.contains("여") || gender.equals("female", ignoreCase = true) -> 0.95
        else -> 1.0
    }
}

fun getBodyFatMultiplier(bodyFatRate: Double): Double {
    return when {
        bodyFatRate <= 0.0 -> 1.0
        bodyFatRate < 15.0 -> 1.08
        bodyFatRate < 25.0 -> 1.0
        bodyFatRate < 35.0 -> 0.95
        else -> 0.9
    }
}

fun getBmrMultiplier(bmr: Int): Double {
    return when {
        bmr <= 0 -> 1.0
        bmr < 1300 -> 0.95
        bmr < 1700 -> 1.0
        bmr < 2100 -> 1.05
        else -> 1.1
    }
}

fun calculateCalories(
    weight: Double,
    exerciseName: String,
    duration: String,
    level: ExerciseLevel,
    age: String,
    gender: String,
    bodyFatRate: Double,
    bmr: Int
): Int {
    val minutes = extractMinutes(duration)
    val met = getMetValue(exerciseName)

    val personalMultiplier =
        getLevelMultiplier(level) *
                getAgeMultiplier(age) *
                getGenderMultiplier(gender) *
                getBodyFatMultiplier(bodyFatRate) *
                getBmrMultiplier(bmr)

    return (met * weight * (minutes / 60.0) * personalMultiplier).roundToInt()
}

fun parseExercises(
    response: String,
    latestRecord: BmiRecord,
    level: ExerciseLevel
): List<Exercise> {
    val exercises = mutableListOf<Exercise>()
    val lines = response.trim().split("\n")
    val weight = latestRecord.weightKg.toDoubleOrNull() ?: 70.0

    for (line in lines) {
        if (line.isBlank()) continue

        val cleanedLine = line
            .trim()
            .replace(Regex("^\\d+[.)]\\s*"), "")

        val parts = cleanedLine.split("|")

        if (parts.size >= 3) {
            try {
                val name = parts[0].trim()
                val description = parts[1].trim()
                val duration = parts[2].trim()

                val calories = calculateCalories(
                    weight = weight,
                    exerciseName = name,
                    duration = duration,
                    level = level,
                    age = latestRecord.age,
                    gender = latestRecord.gender,
                    bodyFatRate = latestRecord.bodyFatRate,
                    bmr = latestRecord.bmr
                )

                exercises.add(
                    Exercise(
                        name = name,
                        description = description,
                        calories = calories,
                        duration = duration
                    )
                )
            } catch (e: Exception) {
                continue
            }
        }
    }

    return exercises
}

@Composable
fun ExerciseTab(
    latestRecord: BmiRecord?,
    onExerciseCaloriesChanged: (Int) -> Unit = {}
) {
    var selectedGoal by remember(latestRecord) {
        mutableStateOf(
            latestRecord?.let { getDefaultExerciseGoal(it.category) } ?: ExerciseGoal.MAINTAIN
        )
    }

    var selectedLevel by remember { mutableStateOf(ExerciseLevel.BEGINNER) }
    var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    val completedExercises = remember { mutableStateListOf<String>() }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val currentCalories = exercises
        .filter { completedExercises.contains(it.name) }
        .sumOf { it.calories }

    LaunchedEffect(currentCalories) {
        onExerciseCaloriesChanged(currentCalories)
    }

    fun loadExercises(levelToUse: ExerciseLevel = selectedLevel) {
        if (latestRecord == null) return

        selectedLevel = levelToUse
        isLoading = true
        errorMessage = ""
        completedExercises.clear()
        exercises = emptyList()

        coroutineScope.launch {
            try {
                val model = GenerativeModel(
                    modelName = "gemini-2.5-flash",
                    apiKey = BuildConfig.GEMINI_API_KEY
                )

                val prompt = """
                    사용자 정보:
                    - BMI: ${latestRecord.bmi} (${latestRecord.category})
                    - 체중: ${latestRecord.weightKg}kg
                    - 나이: ${latestRecord.age}세
                    - 성별: ${latestRecord.gender}
                    - 기초대사량: ${latestRecord.bmr}kcal
                    - 체지방률: ${latestRecord.bodyFatRate}%
                    - 운동 목표: ${selectedGoal.label}
                    - 난이도: ${levelToUse.label}

                    위 정보를 바탕으로 오늘 할 운동 5가지를 추천해주세요.

                    반드시 아래 형식으로만 답하세요.
                    다른 설명은 하지 마세요.
                    각 줄은 반드시 | 로 구분된 3개의 항목이어야 합니다.
                    칼로리는 절대 쓰지 마세요.

                    운동이름|운동설명(1줄)|운동시간(예:30분)
                    운동이름|운동설명(1줄)|운동시간(예:30분)
                    운동이름|운동설명(1줄)|운동시간(예:30분)
                    운동이름|운동설명(1줄)|운동시간(예:30분)
                    운동이름|운동설명(1줄)|운동시간(예:30분)
                """.trimIndent()

                val response = model.generateContent(prompt)
                val text = response.text ?: ""

                val parsed = parseExercises(
                    response = text,
                    latestRecord = latestRecord,
                    level = levelToUse
                )

                if (parsed.isEmpty()) {
                    errorMessage = "운동 추천을 불러오지 못했어요. 다시 시도해주세요."
                } else {
                    exercises = parsed
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "오류가 발생했어요. 다시 시도해주세요."
            } finally {
                isLoading = false
            }
        }
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
                ExerciseGoal.entries.forEach { goal ->
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

        Spacer(modifier = Modifier.height(16.dp))

        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Text(text = "난이도 선택", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExerciseLevel.entries.forEach { level ->
                    Button(
                        onClick = {
                            loadExercises(level)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading && latestRecord != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedLevel == level)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (selectedLevel == level)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(
                            text = level.label,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { loadExercises() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isLoading && latestRecord != null
            ) {
                Text(if (isLoading) "운동 추천 받는 중..." else "현재 난이도로 다시 추천 받기")
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Text(text = "오늘 운동 현황", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "운동 소모: ${currentCalories} kcal",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = "운동 완료: ${completedExercises.size}/${exercises.size}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )

            if (exercises.isNotEmpty()) {
                Text(
                    text = "목표 ${minOf(completedExercises.size, 3)}/3",
                    color = if (completedExercises.size >= 3)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (completedExercises.size >= 3 && exercises.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "목표 달성 완료 🎉",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "오늘의 운동 추천", style = MaterialTheme.typography.titleLarge)

                Text(
                    text = "${completedExercises.size}/${exercises.size} 완료",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${selectedGoal.label} · ${selectedLevel.label}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                latestRecord == null -> {
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
                            text = "BMI를 계산하면 맞춤 운동을 추천해드립니다",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "AI가 맞춤 운동을 추천하고 있어요...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                exercises.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "난이도를 선택하면",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = "바로 AI 운동 추천을 받을 수 있습니다",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                else -> {
                    exercises.forEach { exercise ->
                        val isCompleted = completedExercises.contains(exercise.name)

                        HorizontalDivider()

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = exercise.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isCompleted)
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = exercise.description,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(
                                        text = "🕐 ${exercise.duration}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodySmall
                                    )

                                    Text(
                                        text = "🔥 ${exercise.calories} kcal",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            IconButton(
                                onClick = {
                                    if (isCompleted) {
                                        completedExercises.remove(exercise.name)
                                    } else {
                                        completedExercises.add(exercise.name)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isCompleted)
                                        Icons.Default.CheckCircle
                                    else
                                        Icons.Default.RadioButtonUnchecked,
                                    contentDescription = "완료",
                                    tint = if (isCompleted)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}