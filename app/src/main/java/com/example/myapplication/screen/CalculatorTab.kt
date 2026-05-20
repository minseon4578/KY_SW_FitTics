package com.example.myapplication.screen

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.component.HealthCard
import com.example.myapplication.domain.ActivityLevel
import com.example.myapplication.domain.BmiCalculator
import com.example.myapplication.domain.BmiRecord

@Composable
fun CalculatorTab(
    latestRecord: BmiRecord?,
    selectedActivity: ActivityLevel?,
    heightText: String,
    weightText: String,
    ageText: String,
    selectedGender: String?,
    isEditing: Boolean,
    onHeightChanged: (String) -> Unit,
    onWeightChanged: (String) -> Unit,
    onAgeChanged: (String) -> Unit,
    onGenderChanged: (String) -> Unit,
    onEditingChanged: (Boolean) -> Unit,
    onActivitySelected: (ActivityLevel?) -> Unit,
    onBmiCalculated: (BmiRecord) -> Unit,
    onReset: () -> Unit
) {
    var errorMessage by remember { androidx.compose.runtime.mutableStateOf("") }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var resultCardOffset by remember { mutableIntStateOf(0) }

    val tdee = latestRecord?.let { record ->
        selectedActivity?.let { activity ->
            (record.bmr * activity.factor).toInt()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
            .padding(bottom = 48.dp)
    ) {
        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "▦", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "신체 데이터를 입력하세요", style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "키, 몸무게, 나이, 성별을 입력하여 BMI, BMR, 체지방률을 계산하세요",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(text = "키 (cm)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = heightText,
                onValueChange = { if (isEditing) onHeightChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = isEditing,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "몸무게 (kg)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = weightText,
                onValueChange = { if (isEditing) onWeightChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = isEditing,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "나이", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = ageText,
                onValueChange = { if (isEditing) onAgeChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = isEditing,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "성별", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                FilterChip(
                    selected = selectedGender == "남성",
                    onClick = { if (isEditing) onGenderChanged("남성") },
                    label = { Text("남성") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = selectedGender == "여성",
                    onClick = { if (isEditing) onGenderChanged("여성") },
                    label = { Text("여성") }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 수정 / 초기화 버튼 (계산 결과 있을 때만)
            if (latestRecord != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onEditingChanged(true) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("수정")
                    }
                    Button(
                        onClick = { onReset() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("초기화")
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val gender = selectedGender
                    if (gender == null) {
                        errorMessage = "성별을 선택하세요."
                        return@Button
                    }
                    val result = BmiCalculator.calculate(
                        heightCmText = heightText,
                        weightKgText = weightText,
                        ageText = ageText,
                        gender = gender
                    )
                    if (result == null) {
                        errorMessage = "키, 몸무게, 나이를 올바르게 입력하세요."
                    } else {
                        errorMessage = ""
                        onEditingChanged(false)
                        onActivitySelected(null)
                        onBmiCalculated(result)
                        coroutineScope.launch {
                            scrollState.animateScrollTo(resultCardOffset)
                        }
                    }
                },
                enabled = isEditing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("신체 데이터 계산하기")
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 계산 결과 카드
        HealthCard(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 220.dp)
                .onGloballyPositioned { coordinates ->
                    resultCardOffset = coordinates.positionInParent().y.toInt()
                }
        ) {
            if (latestRecord == null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "신체 정보를 입력하고",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "BMI, BMR, 체지방률을 확인하세요",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(text = "계산 결과", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(16.dp))

                ResultRow(label = "상태", value = latestRecord.category)
                ResultRow(label = "BMI", value = latestRecord.bmi.toString())
                ResultRow(label = "BMR (최소 섭취량)", value = "${latestRecord.bmr} kcal")
                ResultRow(label = "체지방률", value = "${latestRecord.bodyFatRate}%")

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = latestRecord.recommendation,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 활동 수준 선택 카드
        if (latestRecord != null) {
            Spacer(modifier = Modifier.height(24.dp))

            HealthCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = "활동 수준 선택", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "선택하면 위 결과에 권장 섭취량(TDEE)이 추가됩니다",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                ActivityLevel.entries.forEach { level ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedActivity == level,
                            onClick = { onActivitySelected(level) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = level.label,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = level.description,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                if (tdee != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "권장 섭취량 (TDEE)",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${tdee} kcal",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}