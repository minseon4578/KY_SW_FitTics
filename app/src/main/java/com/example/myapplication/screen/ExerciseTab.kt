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
        "정상"  -> ExerciseGoal.MAINTAIN
        "과체중" -> ExerciseGoal.LOSE
        else   -> ExerciseGoal.LOSE
    }
}

fun parseExercises(response: String): List<Exercise> {
    val exercises = mutableListOf<Exercise>()
    val lines = response.trim().split("\n")
    for (line in lines) {
        if (line.isBlank()) continue
        val parts = line.split("|")
        if (parts.size >= 4) {
            try {
                exercises.add(
                    Exercise(
                        name = parts[0].trim(),
                        description = parts[1].trim(),
                        calories = parts[2].trim().toInt(),
                        duration = parts[3].trim()
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
    latestRecord: BmiRecord?
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

    fun loadExercises() {
        if (latestRecord == null) return
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
                    - 나이: ${latestRecord.age}세
                    - 성별: ${latestRecord.gender}
                    - 체지방률: ${latestRecord.bodyFatRate}%
                    - 운동 목표: ${selectedGoal.label}
                    - 난이도: ${selectedLevel.label}
                    
                    위 정보를 바탕으로 오늘 할 운동 5가지를 추천해주세요.
                    반드시 아래 형식으로만 답하세요. 다른 말은 하지 마세요.
                    각 줄은 반드시 | 로 구분된 4개의 항목이어야 합니다.
                    
                    운동이름|운동설명(1줄)|소모칼로리(숫자만)|운동시간(예:30분)
                    운동이름|운동설명(1줄)|소모칼로리(숫자만)|운동시간(예:30분)
                    운동이름|운동설명(1줄)|소모칼로리(숫자만)|운동시간(예:30분)
                    운동이름|운동설명(1줄)|소모칼로리(숫자만)|운동시간(예:30분)
                    운동이름|운동설명(1줄)|소모칼로리(숫자만)|운동시간(예:30분)
                """.trimIndent()

                val response = model.generateContent(prompt)
                val text = response.text ?: ""
                val parsed = parseExercises(text)

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

        // 난이도 선택 카드
        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Text(text = "난이도 선택", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExerciseLevel.entries.forEach { level ->
                    Button(
                        onClick = { selectedLevel = level },
                        modifier = Modifier.weight(1f),
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
                Text(if (isLoading) "운동 추천 받는 중..." else "AI 운동 추천 받기")
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

        // 오늘의 운동 추천 카드
        HealthCard(modifier = Modifier.fillMaxWidth()) {
            // 제목 + 완료/목표 표시
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "오늘의 운동 추천", style = MaterialTheme.typography.titleLarge)
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${completedExercises.size}/${exercises.size} 완료",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
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
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${selectedGoal.label} · ${selectedLevel.label}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )

            if (exercises.isNotEmpty()) {
                Text(
                    text = "현재 소모 칼로리: ${currentCalories} kcal",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

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
                            text = "목표와 난이도를 선택하고",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "AI 운동 추천 받기를 눌러주세요",
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

                    if (completedExercises.size >= 3 && exercises.isNotEmpty()) {
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "🎉 오늘의 운동을 모두 완료했어요!",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}