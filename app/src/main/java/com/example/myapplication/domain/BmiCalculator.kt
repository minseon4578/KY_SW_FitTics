package com.example.myapplication.domain

import kotlin.math.round
import kotlin.math.roundToInt

object BmiCalculator {

    fun calculate(
        heightCmText: String,
        weightKgText: String,
        ageText: String,
        gender: String
    ): BmiRecord? {
        val heightCm = heightCmText.toDoubleOrNull()
        val weightKg = weightKgText.toDoubleOrNull()
        val age = ageText.toIntOrNull()

        if (heightCm == null || weightKg == null || age == null) return null
        if (heightCm <= 0 || weightKg <= 0 || age <= 0) return null

        val heightM = heightCm / 100.0
        val bmiRaw = weightKg / (heightM * heightM)
        val bmi = round(bmiRaw * 10) / 10

        val category = getCategory(bmi)
        val bmr = calculateBmr(
            heightCm = heightCm,
            weightKg = weightKg,
            age = age,
            gender = gender
        )

        val bodyFatRate = calculateBodyFatRate(
            bmi = bmi,
            age = age,
            gender = gender
        )

        val recommendation = getRecommendation(category)

        return BmiRecord(
            heightCm = heightCmText,
            weightKg = weightKgText,
            age = ageText,
            gender = gender,
            bmi = bmi,
            category = category,
            bmr = bmr,
            bodyFatRate = bodyFatRate,
            recommendation = recommendation
        )
    }

    private fun getCategory(bmi: Double): String {
        return when {
            bmi < 18.5 -> "저체중"
            bmi < 23.0 -> "정상"
            bmi < 25.0 -> "과체중"
            else -> "비만"
        }
    }

    private fun calculateBmr(
        heightCm: Double,
        weightKg: Double,
        age: Int,
        gender: String
    ): Int {
        val bmr = if (gender == "남성") {
            10 * weightKg + 6.25 * heightCm - 5 * age + 5
        } else {
            10 * weightKg + 6.25 * heightCm - 5 * age - 161
        }

        return bmr.roundToInt()
    }

    private fun calculateBodyFatRate(
        bmi: Double,
        age: Int,
        gender: String
    ): Double {
        val sexValue = if (gender == "남성") 1 else 0

        val bodyFatRaw = 1.20 * bmi + 0.23 * age - 10.8 * sexValue - 5.4

        return round(bodyFatRaw * 10) / 10
    }

    private fun getRecommendation(category: String): String {
        return when (category) {
            "저체중" -> "근력 운동과 충분한 영양 섭취를 병행하세요."
            "정상" -> "현재 상태를 유지하기 위해 유산소와 근력 운동을 균형 있게 진행하세요."
            "과체중" -> "빠르게 걷기, 자전거, 가벼운 근력 운동을 추천합니다."
            else -> "관절 부담이 적은 걷기, 실내 자전거, 스트레칭부터 시작하세요."
        }
    }
}