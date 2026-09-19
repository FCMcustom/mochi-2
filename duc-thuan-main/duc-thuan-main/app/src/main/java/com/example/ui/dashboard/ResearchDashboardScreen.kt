package com.example.ui.dashboard

import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.DashboardStatsUiState
import com.example.data.MistakeStat
import com.example.data.ResearchRepository
import com.example.data.SessionSummary
import com.example.data.CategoryStat
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Màn hình Dashboard Thống kê Thực nghiệm KHKT.
 * Hiển thị KPI tổng hợp, biểu đồ Canvas, lịch sử phiên thí nghiệm,
 * và nút xuất dữ liệu CSV.
 */
@Composable
fun ResearchDashboardScreen(
    viewModel: ResearchDashboardViewModel = viewModel(
        factory = ResearchDashboardViewModelFactory()
    ),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isExporting by viewModel.isExporting.collectAsState()
    val exportSuccess by viewModel.exportSuccess.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle export success/error
    LaunchedEffect(exportSuccess) {
        exportSuccess?.let { uri ->
            snackbarHostState.showSnackbar("Đã xuất dữ liệu thành công! Nhấn nút chia sẻ để gửi file CSV.")
            viewModel.clearExportState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.exportToCsv() },
                containerColor = Color(0xFF00E5FF),
                contentColor = Color(0xFF002244),
                icon = {
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color(0xFF002244),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.FileDownload, contentDescription = "Xuất CSV")
                    }
                },
                text = { Text(if (isExporting) "Đang xuất..." else "Xuất dữ liệu nghiên cứu (CSV)", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = Color(0xFF070F1E),
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                DashboardHeader()
            }

            // KPI Cards Row 1
            item {
                KpiCardsRow1(uiState = uiState)
            }

            // KPI Cards Row 2
            item {
                KpiCardsRow2(uiState = uiState)
            }

            // Biểu đồ cột - Thí nghiệm theo chương
            item {
                CategoryBarChart(
                    categories = uiState.experimentsByCategory,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Biểu đồ tròn - Tỷ lệ tự giải thích vs xem đáp án
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SelfReasoningChart(
                        selfReasoningRatio = uiState.selfReasoningRatio,
                        modifier = Modifier.weight(1f)
                    )
                    SuccessRateChart(
                        successRate = uiState.successfulReactionRate,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Thống kê lỗi
            if (uiState.mistakeBreakdown.isNotEmpty()) {
                item {
                    MistakeBreakdownCard(mistakes = uiState.mistakeBreakdown)
                }
            }

            // Top phản ứng khó
            if (uiState.topDifficultReactions.isNotEmpty()) {
                item {
                    DifficultReactionsCard(reactions = uiState.topDifficultReactions)
                }
            }

            // Lịch sử phiên gần nhất
            item {
                Text(
                    text = "LỊCH SỬ PHIÊN THỰC HÀNH GẦN NHẤT",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (uiState.recentSessions.isEmpty() && !uiState.isLoading) {
                item {
                    EmptySessionsCard()
                }
            }

            items(uiState.recentSessions) { session ->
                SessionHistoryCard(session = session)
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// HEADER
// ══════════════════════════════════════════════════════

@Composable
private fun DashboardHeader() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F1E33),
        border = BorderStroke(1.dp, Color(0xFF1B3B60))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.BarChart,
                contentDescription = null,
                tint = Color(0xFF00E5FF),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "DASHBOARD THỐNG KÊ KHKT",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Theo dõi tiến độ thực nghiệm & phát triển năng lực GDPT 2018",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// KPI CARDS
// ══════════════════════════════════════════════════════

@Composable
private fun KpiCardsRow1(uiState: DashboardStatsUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        KpiCard(
            title = "Tổng phiên",
            value = "${uiState.totalSessions}",
            subtitle = "buổi thực hành",
            icon = Icons.Default.Science,
            color = Color(0xFF00E5FF),
            modifier = Modifier.weight(1f)
        )
        KpiCard(
            title = "Tỷ lệ đúng",
            value = "${(uiState.successfulReactionRate * 100).toInt()}%",
            subtitle = "thao tác thành công",
            icon = Icons.Default.CheckCircle,
            color = Color(0xFF00E676),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun KpiCardsRow2(uiState: DashboardStatsUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        KpiCard(
            title = "Câu hỏi Socratic",
            value = "%.1f".format(uiState.averageSocraticPromptsPerSession),
            subtitle = "trung bình / phiên",
            icon = Icons.Default.Psychology,
            color = Color(0xFFFFD54F),
            modifier = Modifier.weight(1f)
        )
        KpiCard(
            title = "Thời gian TB",
            value = "%.1f ph".format(uiState.averageSessionDurationMinutes),
            subtitle = "mỗi phiên",
            icon = Icons.Default.Timer,
            color = Color(0xFFFF8A80),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0F1E33),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 9.sp,
                color = Color.Gray
            )
        }
    }
}

// ══════════════════════════════════════════════════════
// CANVAS CHARTS
// ══════════════════════════════════════════════════════

/**
 * Biểu đồ cột Canvas minh họa số thí nghiệm theo từng chương.
 */
@Composable
private fun CategoryBarChart(
    categories: List<CategoryStat>,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F1E33),
        border = BorderStroke(1.dp, Color(0xFF1B3B60)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.BarChart, contentDescription = null, tint = Color(0xFF80D8FF), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SỐ THÍ NGHIỆM THEO CHƯƠNG",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF80D8FF)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (categories.isEmpty()) {
                Text(
                    text = "Chưa có dữ liệu thí nghiệm",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                val maxCount = categories.maxOfOrNull { it.count } ?: 1

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((categories.size * 52).coerceAtLeast(80).dp)
                ) {
                    val canvasW = size.width
                    val canvasH = size.height
                    val barAreaH = canvasH - 48.dp.toPx()
                    val barCount = categories.size
                    val barSpacing = 16.dp.toPx()
                    val barWidth = (canvasW - (barCount + 1) * barSpacing) / barCount

                    val barColors = listOf(
                        Color(0xFF00E5FF),
                        Color(0xFFFFD54F),
                        Color(0xFFFF8A80),
                        Color(0xFF00E676),
                        Color(0xFFB388FF)
                    )

                    categories.forEachIndexed { index, cat ->
                        val barHeight = (cat.count.toFloat() / maxCount) * barAreaH
                        val x = barSpacing + index * (barWidth + barSpacing)
                        val y = barAreaH - barHeight

                        // Bar
                        drawRoundRect(
                            color = barColors[index % barColors.size],
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight.coerceAtLeast(4f)),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                        )

                        // Count label
                        drawContext.canvas.nativeCanvas.apply {
                            drawText(
                                cat.count.toString(),
                                x + barWidth / 2,
                                y - 8.dp.toPx(),
                                android.graphics.Paint().apply {
                                    color = android.graphics.Color.WHITE
                                    textSize = 11.dp.toPx()
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }
                            )
                        }
                    }

                    // X-axis label
                    categories.forEachIndexed { index, cat ->
                        val x = barSpacing + index * (barWidth + barSpacing) + barWidth / 2
                        drawContext.canvas.nativeCanvas.apply {
                            drawText(
                                cat.label.take(6),
                                x,
                                canvasH - 4.dp.toPx(),
                                android.graphics.Paint().apply {
                                    color = android.graphics.Color.LTGRAY
                                    textSize = 9.dp.toPx()
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Biểu đồ tròn Canvas: Tỷ lệ tự giải thích vs xem đáp án.
 */
@Composable
private fun SelfReasoningChart(
    selfReasoningRatio: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0F1E33),
        border = BorderStroke(1.dp, Color(0xFF1B3B60)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TỰ SUY NGHĨ",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E676)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Canvas(modifier = Modifier.size(80.dp)) {
                val strokeW = 10.dp.toPx()
                val radius = (size.minDimension - strokeW) / 2f
                val center = Offset(size.width / 2f, size.height / 2f)

                // Background arc
                drawArc(
                    color = Color(0xFF1E3A5F),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeW)
                )

                // Self reasoning arc
                val sweepAngle = selfReasoningRatio * 360f
                drawArc(
                    color = Color(0xFF00E676),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeW)
                )
            }

            Text(
                text = "${(selfReasoningRatio * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E676)
            )
            Text(
                text = "tự giải thích",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 9.sp,
                color = Color.Gray
            )
        }
    }
}

/**
 * Biểu đồ tròn Canvas: Tỷ lệ phản ứng thành công.
 */
@Composable
private fun SuccessRateChart(
    successRate: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0F1E33),
        border = BorderStroke(1.dp, Color(0xFF1B3B60)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "THÀNH CÔNG",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E5FF)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Canvas(modifier = Modifier.size(80.dp)) {
                val strokeW = 10.dp.toPx()
                val radius = (size.minDimension - strokeW) / 2f
                val center = Offset(size.width / 2f, size.height / 2f)

                drawArc(
                    color = Color(0xFF1E3A5F),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeW)
                )

                val sweepAngle = successRate * 360f
                drawArc(
                    color = Color(0xFF00E5FF),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeW)
                )
            }

            Text(
                text = "${(successRate * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E5FF)
            )
            Text(
                text = "phản ứng xảy ra",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 9.sp,
                color = Color.Gray
            )
        }
    }
}

// ══════════════════════════════════════════════════════
// MISTAKE BREAKDOWN
// ══════════════════════════════════════════════════════

@Composable
private fun MistakeBreakdownCard(mistakes: List<MistakeStat>) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F1E33),
        border = BorderStroke(1.dp, Color(0xFF1B3B60))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF8A80), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "PHÂN TÍCH LỖI THƯỜNG GẶP",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF8A80)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            mistakes.take(4).forEach { mistake ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mistake.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFF8A80).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFFFF8A80).copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "${mistake.count} lần",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF8A80),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// DIFFICULT REACTIONS
// ══════════════════════════════════════════════════════

@Composable
private fun DifficultReactionsCard(
    reactions: List<com.example.data.DifficultStat>
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F1E33),
        border = BorderStroke(1.dp, Color(0xFF1B3B60))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "TOP PHẢN ỨNG CẦN LUYỆN TẬP THÊM",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            reactions.forEachIndexed { index, reaction ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFD54F).copy(alpha = 0.2f),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = reaction.chemicalsMixed.replace("+", " + "),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${reaction.mistakeCount} lỗi",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF8A80)
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// SESSION HISTORY CARD
// ══════════════════════════════════════════════════════

@Composable
private fun SessionHistoryCard(session: SessionSummary) {
    val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F1E33),
        border = BorderStroke(1.dp, Color(0xFF1B3B60))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status icon
            Surface(
                shape = CircleShape,
                color = if (session.isReactionOccurred) Color(0xFF00E676).copy(alpha = 0.2f)
                        else Color(0xFFFF8A80).copy(alpha = 0.2f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (session.isReactionOccurred) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        tint = if (session.isReactionOccurred) Color(0xFF00E676) else Color(0xFFFF8A80),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.chemicalsMixed.replace("+", " + "),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = session.reactionType,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = Color(0xFF80D8FF)
                    )
                    Text("•", color = Color.Gray, fontSize = 10.sp)
                    Text(
                        text = dateFormat.format(Date(session.startTime)),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${session.durationSeconds / 60}m ${session.durationSeconds % 60}s",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${session.socraticPromptCount}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySessionsCard() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F1E33),
        border = BorderStroke(1.dp, Color(0xFF1B3B60)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ScienceOutlined,
                contentDescription = null,
                tint = Color(0xFF1E3A5F),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Chưa có phiên thực hành nào",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Text(
                text = "Hãy bắt đầu thực hành trong Virtual Lab!",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
                fontSize = 11.sp
            )
        }
    }
}

// ══════════════════════════════════════════════════════
// COMPOSE PREVIEWS
// ══════════════════════════════════════════════════════

@Preview(name = "Dashboard with Data", showBackground = true, backgroundColor = 0xFF070F1E)
@Composable
private fun PreviewDashboardWithData() {
    MaterialTheme {
        ResearchDashboardScreen(
            viewModel = viewModel(
                factory = ResearchDashboardViewModelFactory(
                    repository = ResearchRepository(
                        dao = FakeLabDaoForPreview()
                    )
                )
            )
        )
    }
}

@Preview(name = "Empty Dashboard", showBackground = true, backgroundColor = 0xFF070F1E)
@Composable
private fun PreviewEmptyDashboard() {
    MaterialTheme {
        ResearchDashboardScreen(
            viewModel = viewModel(
                factory = ResearchDashboardViewModelFactory(
                    repository = ResearchRepository(
                        dao = FakeEmptyDaoForPreview()
                    )
                )
            )
        )
    }
}
