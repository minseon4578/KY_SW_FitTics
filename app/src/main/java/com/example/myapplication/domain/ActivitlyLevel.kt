package com.example.myapplication.domain

enum class ActivityLevel(val label: String, val factor: Double, val description: String) {
    SEDENTARY("거의 안 움직임", 1.2, "앉아서 생활, 운동 거의 없음"),
    LIGHT("가벼운 활동", 1.375, "주 1~3회 가벼운 운동"),
    MODERATE("보통 활동", 1.55, "주 3~5회 운동"),
    ACTIVE("활발한 활동", 1.725, "주 6~7회 강도 높은 운동"),
    VERY_ACTIVE("매우 활발", 1.9, "하루 2번 운동, 육체노동")
}