package com.example.myapplication.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.myapplication.component.AppHeader
import com.example.myapplication.component.BottomNavBar
import com.example.myapplication.component.BottomTab
import com.example.myapplication.domain.ActivityLevel
import com.example.myapplication.domain.BmiRecord

@Composable
fun HealthFitnessScreen() {
    var selectedTab by remember { mutableStateOf(BottomTab.CALCULATOR) }
    var latestRecord by remember { mutableStateOf<BmiRecord?>(null) }

    var records by remember {
        mutableStateOf<List<BmiRecord>>(
            listOf(
                BmiRecord(
                    heightCm = "170",
                    weightKg = "70",
                    age = "25",
                    gender = "남성",
                    bmi = 24.2,
                    category = "정상",
                    bmr = 1700,
                    bodyFatRate = 18.5,
                    recommendation = "균형 잡힌 식단을 유지하세요",
                    date = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L
                )
            )
        )
    }

    var selectedActivity by remember { mutableStateOf<ActivityLevel?>(null) }

    var heightText by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var isEditing by remember { mutableStateOf(true) }

    var todayMealCheckedCount by remember { mutableStateOf(0) }
    var todayExerciseCompletedCount by remember { mutableStateOf(0) }
    var todayExerciseCalories by remember { mutableStateOf(0) }

    val tdee = latestRecord?.let { record ->
        selectedActivity?.let { activity ->
            (record.bmr * activity.factor).toInt()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AppHeader()

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                when (selectedTab) {
                    BottomTab.CALCULATOR -> {
                        CalculatorTab(
                            latestRecord = latestRecord,
                            selectedActivity = selectedActivity,
                            heightText = heightText,
                            weightText = weightText,
                            ageText = ageText,
                            selectedGender = selectedGender,
                            isEditing = isEditing,
                            onHeightChanged = { heightText = it },
                            onWeightChanged = { weightText = it },
                            onAgeChanged = { ageText = it },
                            onGenderChanged = { selectedGender = it },
                            onEditingChanged = { isEditing = it },
                            onActivitySelected = { selectedActivity = it },
                            onBmiCalculated = { record ->
                                latestRecord = record
                                records = records + record
                                todayMealCheckedCount = 0
                                todayExerciseCompletedCount = 0
                                todayExerciseCalories = 0
                            },
                            onReset = {
                                latestRecord = null
                                records = emptyList()
                                selectedActivity = null
                                heightText = ""
                                weightText = ""
                                ageText = ""
                                selectedGender = null
                                isEditing = true
                                todayMealCheckedCount = 0
                                todayExerciseCompletedCount = 0
                                todayExerciseCalories = 0
                            }
                        )
                    }

                    BottomTab.DIET -> {
                        DietTab(
                            latestRecord = latestRecord,
                            tdee = tdee,
                            exerciseBurnedCalories = todayExerciseCalories,
                            onMealCheckedCountChanged = { count ->
                                todayMealCheckedCount = count
                            }
                        )
                    }

                    BottomTab.EXERCISE -> {
                        ExerciseTab(
                            latestRecord = latestRecord,
                            onExerciseCaloriesChanged = { calories ->
                                todayExerciseCalories = calories
                            },
                            onExerciseCompletedCountChanged = { count ->
                                todayExerciseCompletedCount = count
                            }
                        )
                    }

                    BottomTab.HISTORY -> {
                        HistoryTab(
                            records = records,
                            todayMealCheckedCount = todayMealCheckedCount,
                            todayExerciseCompletedCount = todayExerciseCompletedCount
                        )
                    }
                }
            }
        }
    }
}