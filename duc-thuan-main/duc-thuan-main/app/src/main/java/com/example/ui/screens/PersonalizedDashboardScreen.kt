package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExperimentRecord
import com.example.engine.BktEngine
import com.example.model.CompetencyMastery

@Composable
fun PersonalizedDashboardScreen(
    masteries: List<CompetencyMastery>,
    experimentHistory: List<ExperimentRecord>,
    onAnswerQuiz: (competencyId: String, isCorrect: Boolean, currentProb: Float, attempts: Int, correct: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeQuizQuestion by remember { mutableStateOf<Pair<String, Boolean>?>(null) } // (Question, CorrectAnswer)
    var quizResultText by remember { mutableStateOf("") }

    val recommendation = remember(masteries) {
        BktEngine.getAdaptiveRecommendation(masteries)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070F1E))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Column {
                Text(
                    text = "BẢNG NĂNG LỰC CÁ NHÂN HÓA (BKT)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Mô hình Bayesian Knowledge Tracing theo dõi độ vững kiến thức của học sinh theo thời gian thực",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Adaptive AI Recommendation Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0C243B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF00E5FF).copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Gợi ý thích ứng",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lộ trình học tập cá nhân hóa hôm nay",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF80D8FF)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = recommendation,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Competency Mastery Bars
        item {
            Text(
                text = "ĐỘ THÀNH THẠO NĂNG LỰC HÓA HỌC (P_mastery):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF80D8FF)
            )
        }

        items(masteries) { item ->
            val prob = item.currentProbability
            val animatedProb by animateFloatAsState(targetValue = prob, label = "prob")
            val percent = (prob * 100).toInt()

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF0E1E34),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.competency.nameVi,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "$percent% vững vàng",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (percent >= 70) Color(0xFF00E676) else if (percent >= 45) Color(0xFFFFD54F) else Color(0xFFFF5252)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { animatedProb },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (percent >= 70) Color(0xFF00E676) else if (percent >= 45) Color(0xFFFFD54F) else Color(0xFFFF5252),
                        trackColor = Color(0xFF162840)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Lượt luyện tập: ${item.totalAttempts} (Đúng: ${item.correctCount})",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "BKT P_learn = ${item.competency.pLearn}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF80D8FF),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Adaptive Practice Drill
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF14243B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3F66)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Quiz, contentDescription = "Trắc nghiệm thích ứng", tint = Color(0xFFFFD54F))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BÀI TẬP THÍCH ỨNG RÈN NĂNG LỰC YẾU",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Câu hỏi thích ứng: Cho mẩu đồng (Cu) vào dung dịch Sắt (II) sunfat (FeSO4), hiện tượng có xảy ra phản ứng kim loại đẩy nhau không?",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                // Cu + FeSO4 -> NO REACTION, so "Có phản ứng" is False
                                val comp = masteries.find { it.competency.id == "metal_series" }
                                if (comp != null) {
                                    onAnswerQuiz("metal_series", false, comp.currentProbability, comp.totalAttempts, comp.correctCount)
                                    quizResultText = "❌ Sai rồi! Trong dãy điện hóa, tính khử của Cu yếu hơn Fe (E° Cu2+/Cu = +0.34V > Fe2+/Fe = -0.44V). Cu không đẩy được Fe!"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A5F)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("A. Có phản ứng", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                val comp = masteries.find { it.competency.id == "metal_series" }
                                if (comp != null) {
                                    onAnswerQuiz("metal_series", true, comp.currentProbability, comp.totalAttempts, comp.correctCount)
                                    quizResultText = "✓ Chính xác tuyệt vời! Cu đứng sau Fe trong dãy điện hóa nên KHÔNG phản ứng. Xác suất BKT của em đã tăng!"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF001F3F)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("B. Không phản ứng", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (quizResultText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0B192A),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = quizResultText,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (quizResultText.startsWith("✓")) Color(0xFF69F0AE) else Color(0xFFFF8A80),
                                modifier = Modifier.padding(10.dp),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Experiment Log History
        item {
            Text(
                text = "NHẬT KÝ THÍ NGHIỆM ĐÃ THỰC HIỆN (${experimentHistory.size}):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF80D8FF)
            )
        }

        if (experimentHistory.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0D1B2E),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Chưa có lượt thí nghiệm nào. Hãy vào Phòng Lab để tiến hành thí nghiệm đầu tiên!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        } else {
            items(experimentHistory.take(10)) { rec ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0D1B2E),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = rec.experimentTitle,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (rec.isHypothesisConfirmed) Color(0xFF2E7D32) else Color(0xFFC62828)
                            ) {
                                Text(
                                    text = if (rec.isHypothesisConfirmed) "Khớp giả thuyết ✓" else "Chưa khớp",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Hiện tượng: ${rec.actualPhenomena}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                        if (rec.studentExplanation.isNotBlank()) {
                            Text(
                                text = "Giải thích của HS: \"${rec.studentExplanation}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF80D8FF),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
