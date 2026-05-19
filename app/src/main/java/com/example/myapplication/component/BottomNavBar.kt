package com.example.myapplication.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

enum class BottomTab(
    val label: String
) {
    CALCULATOR("계산"),
    EXERCISE("운동"),
    DIET("식단"),
    HISTORY("기록")
}

@Composable
fun BottomNavBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == BottomTab.CALCULATOR,
            onClick = { onTabSelected(BottomTab.CALCULATOR) },
            icon = {
                Icon(imageVector = Icons.Default.Calculate, contentDescription = null)
            },
            label = { Text("계산") }
        )

        NavigationBarItem(
            selected = selectedTab == BottomTab.DIET,
            onClick = { onTabSelected(BottomTab.DIET) },
            icon = {
                Icon(imageVector = Icons.Default.Restaurant, contentDescription = null)
            },
            label = { Text("식단") }
        )

        NavigationBarItem(
            selected = selectedTab == BottomTab.EXERCISE,
            onClick = { onTabSelected(BottomTab.EXERCISE) },
            icon = {
                Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null)
            },
            label = { Text("운동") }
        )



        NavigationBarItem(
            selected = selectedTab == BottomTab.HISTORY,
            onClick = { onTabSelected(BottomTab.HISTORY) },
            icon = {
                Icon(imageVector = Icons.Default.ShowChart, contentDescription = null)
            },
            label = { Text("기록") }
        )
    }
}