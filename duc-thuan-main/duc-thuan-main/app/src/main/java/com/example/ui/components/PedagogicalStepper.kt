package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class LabStep(val stepNumber: Int, val titleVi: String, val titleEn: String) {
    STEP_1_PREDICT(1, "1. Dự đoán", "1. Predict"),
    STEP_2_OPERATE(2, "2. Thao tác", "2. Setup"),
    STEP_3_OBSERVE(3, "3. Quan sát", "3. Observe"),
    STEP_4_EXPLAIN(4, "4. Giải thích", "4. Explain")
}

@Composable
fun PedagogicalStepper(
    currentStep: LabStep,
    onSelectStep: (LabStep) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F1E32))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LabStep.values().forEach { step ->
            val isActive = step == currentStep
            val isPassed = step.stepNumber < currentStep.stepNumber

            val bgColor = when {
                isActive -> Color(0xFF00E5FF)
                isPassed -> Color(0xFF00E676)
                else -> Color(0xFF1E3A5F)
            }

            val textColor = when {
                isActive -> Color(0xFF002244)
                isPassed -> Color.White
                else -> Color.Gray
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onSelectStep(step) }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(bgColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPassed) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Hoàn thành",
                            tint = Color(0xFF002244),
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Text(
                            text = "${step.stepNumber}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = step.titleVi.substringAfter(". "),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color = if (isActive) Color(0xFF80D8FF) else if (isPassed) Color(0xFFB9F6CA) else Color.Gray,
                    fontSize = 11.sp
                )
            }
        }
    }
}
