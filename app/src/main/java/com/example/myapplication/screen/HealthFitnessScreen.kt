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
import com.example.myapplication.domain.BmiRecord

@Composable
fun HealthFitnessScreen() {
    var selectedTab by remember { mutableStateOf(BottomTab.CALCULATOR) }
    var latestRecord by remember { mutableStateOf<BmiRecord?>(null) }
    var records by remember { mutableStateOf<List<BmiRecord>>(emptyList()) }

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
                            onBmiCalculated = { record ->
                                latestRecord = record
                                records = records + record
                            }
                        )
                    }

                    BottomTab.EXERCISE -> {
                        ExerciseTab(
                            latestRecord = latestRecord
                        )
                    }

                    BottomTab.HISTORY -> {
                        HistoryTab(
                            records = records
                        )
                    }
                }
            }
        }
    }
}