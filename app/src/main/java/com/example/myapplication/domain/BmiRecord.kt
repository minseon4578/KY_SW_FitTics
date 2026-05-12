package com.example.myapplication.domain

data class BmiRecord(
    val heightCm: String,
    val weightKg: String,
    val age: String,
    val gender: String,
    val bmi: Double,
    val category: String,
    val bmr: Int,
    val bodyFatRate: Double,
    val recommendation: String
)