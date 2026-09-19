package com.example.ui.lab

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Component ống nghiệm vẽ bằng Canvas với animation mượt mà.
 *
 * Hiệu ứng bao gồm:
 * - Sóng bề mặt chất lỏng dao động liên tục
 * - Bọt khí nổi lên khi có gas
 * - Lớp kết tủa lắng ở đáy
 * - Ngọn lửa đun nóng
 * - Đổi màu chất lỏng mượt mà theo animateColorAsState
 * - Nhiệt kế hiển thị nhiệt độ thực
 */
@Composable
fun TestTubeCanvas(
    testTubeState: TestTubeState,
    temperature: Float,
    isHeating: Boolean,
    selectedChemicals: List<com.example.model.Chemical>,
    modifier: Modifier = Modifier
) {
    // Wave animation
    val infiniteTransition = rememberInfiniteTransition(label = "tube_wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    // Flame pulse animation
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame"
    )

    // Màu chất lỏng animated
    val animatedLiquidColor by animateColorAsState(
        targetValue = Color(testTubeState.liquidColor),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "liquid_color"
    )

    // Màu kết tủa animated
    val animatedPptColor by animateColorAsState(
        targetValue = Color(testTubeState.precipitateColor),
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "ppt_color"
    )

    // Nhiệt độ animated
    val animatedTemp by animateFloatAsState(
        targetValue = temperature,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "temp"
    )

    // Bubble state
    val bubbles = remember {
        mutableStateListOf(
            *Array(20) {
                Bubble(
                    xRatio = 0.25f + Random.nextFloat() * 0.5f,
                    yRatio = 0.5f + Random.nextFloat() * 0.4f,
                    radius = 2f + Random.nextFloat() * 4f,
                    speed = 0.003f + Random.nextFloat() * 0.007f,
                    alpha = 0.5f + Random.nextFloat() * 0.5f
                )
            }
        )
    }

    // Cập nhật bọt khí khi có gas
    LaunchedEffect(testTubeState.hasGas, testTubeState.gasRate) {
        if (testTubeState.hasGas) {
            while (true) {
                bubbles.forEach { b ->
                    b.yRatio -= b.speed * (1f + (testTubeState.gasRate * 2.5f))
                    if (b.yRatio < 0.28f) {
                        b.yRatio = 0.82f + Random.nextFloat() * 0.08f
                        b.xRatio = 0.28f + Random.nextFloat() * 0.44f
                        b.radius = 2f + Random.nextFloat() * 4f
                    }
                }
                kotlinx.coroutines.delay(28)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0D1B2A))
            .border(1.dp, Color(0xFF1E3A5F), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        // Header: nhãn và nhiệt kế
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ỐNG NGHIỆM ẢO",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF80D8FF),
                letterSpacing = 1.sp
            )

            // Thermometer badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1B2A47),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E4C7E))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Thermostat,
                        contentDescription = "Nhiệt độ",
                        tint = if (animatedTemp > 30f) Color(0xFFFF5252) else Color(0xFF00E5FF),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "%.1f°C".format(animatedTemp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (animatedTemp > 30f) Color(0xFFFF8A80) else Color(0xFFE0F7FA)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Canvas ống nghiệm
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasW = size.width
                val canvasH = size.height

                // Lưới nền phòng thí nghiệm
                drawLabGrid(canvasW, canvasH)

                // Giá ống nghiệm
                drawTestTubeStand(canvasW, canvasH)

                // Ống nghiệm
                val tubeLeft = (canvasW - 64.dp.toPx()) / 2f
                val tubeTop = 16.dp.toPx()
                val tubeH = 165.dp.toPx()
                val tubeBottom = tubeTop + tubeH
                val tubeW = 64.dp.toPx()
                val tubeRadius = tubeW / 2f

                // Khói/khí bay lên từ miệng ống
                if (testTubeState.hasGas) {
                    drawGasCloud(tubeLeft, tubeTop, tubeW, waveOffset)
                }

                // Chất lỏng
                if (testTubeState.liquidLevel > 0f) {
                    drawLiquid(
                        tubeLeft = tubeLeft,
                        tubeTop = tubeTop,
                        tubeW = tubeW,
                        tubeH = tubeH,
                        tubeRadius = tubeRadius,
                        tubeBottom = tubeBottom,
                        liquidColor = animatedLiquidColor,
                        liquidLevel = testTubeState.liquidLevel,
                        waveOffset = waveOffset,
                        bubbles = bubbles,
                        showBubbles = testTubeState.hasGas
                    )

                    // Kết tủa ở đáy
                    if (testTubeState.hasPrecipitate) {
                        drawPrecipitate(
                            tubeLeft = tubeLeft,
                            tubeBottom = tubeBottom,
                            tubeW = tubeW,
                            pptColor = animatedPptColor,
                            precipitateFormula = testTubeState.precipitateFormula
                        )
                    }

                    // Kim loại rắn (nếu có)
                    drawSolidPellets(tubeLeft, tubeBottom, tubeW, selectedChemicals, testTubeState.hasPrecipitate)
                }

                // Thân ống nghiệm (thủy tinh)
                drawGlassTube(tubeLeft, tubeTop, tubeW, tubeH, tubeRadius, tubeBottom)

                // Thanh nhiệt kế
                drawThermometer(tubeLeft, tubeTop, tubeBottom, tubeW, animatedTemp)

                // Đèn cồn nếu đun nóng
                if (isHeating) {
                    drawAlcoholBurner(canvasW, tubeBottom, flameScale)
                }
            }
        }

        // Chú thích trạng thái
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (testTubeState.hasGas) {
                StatusChip(
                    label = "Khí ${testTubeState.gasFormula ?: ""}",
                    color = Color(0xFF00E5FF)
                )
            }
            if (testTubeState.hasPrecipitate) {
                StatusChip(
                    label = "Kết tủa ${testTubeState.precipitateFormula ?: ""}",
                    color = Color(0xFFFFFFFF)
                )
            }
            if (testTubeState.liquidLevel > 0f) {
                StatusChip(
                    label = "Dung dịch",
                    color = animatedLiquidColor
                )
            }
        }
    }
}

@Composable
private fun StatusChip(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.6f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                color = Color.White
            )
        }
    }
}

private fun DrawScope.drawLabGrid(canvasW: Float, canvasH: Float) {
    val gridColor = Color(0xFF13283E)
    for (x in 0..canvasW.toInt() step 36) {
        drawLine(gridColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), canvasH), strokeWidth = 1f)
    }
    for (y in 0..canvasH.toInt() step 36) {
        drawLine(gridColor, Offset(0f, y.toFloat()), Offset(canvasW, y.toFloat()), strokeWidth = 1f)
    }
}

private fun DrawScope.drawTestTubeStand(canvasW: Float, canvasH: Float) {
    val standX = canvasW * 0.28f
    // Thanh đứng
    drawLine(
        color = Color(0xFF455A64),
        start = Offset(standX, 8f),
        end = Offset(standX, canvasH - 18f),
        strokeWidth = 5f
    )
    // Đế
    drawLine(
        color = Color(0xFF37474F),
        start = Offset(standX - 36f, canvasH - 18f),
        end = Offset(standX + 54f, canvasH - 18f),
        strokeWidth = 7f,
        cap = StrokeCap.Round
    )
    // Càng kẹp
    drawLine(
        color = Color(0xFF607D8B),
        start = Offset(standX, canvasH * 0.32f),
        end = Offset(canvasW * 0.5f - 32f, canvasH * 0.32f),
        strokeWidth = 4f
    )
}

private fun DrawScope.drawGasCloud(tubeLeft: Float, tubeTop: Float, tubeW: Float, waveOffset: Float) {
    val gasColor = Color(0x66FFFFFF)
    for (i in 0..5) {
        val offset = sin(waveOffset + i) * 14f
        drawCircle(
            color = gasColor.copy(alpha = 0.42f - i * 0.06f),
            radius = 14f + i * 3.5f,
            center = Offset(tubeLeft + tubeW / 2f + offset, tubeTop - 14f - (i * 11f))
        )
    }
}

private fun DrawScope.drawLiquid(
    tubeLeft: Float, tubeTop: Float, tubeW: Float, tubeH: Float,
    tubeRadius: Float, tubeBottom: Float, liquidColor: Color,
    liquidLevel: Float, waveOffset: Float, bubbles: List<Bubble>, showBubbles: Boolean
) {
    val liquidTop = tubeBottom - (tubeH * liquidLevel)

    val liquidPath = Path().apply {
        moveTo(tubeLeft + 4f, liquidTop)
        // Sóng bề mặt
        for (w in 0..8) {
            val x = tubeLeft + 4f + (tubeW - 8f) * (w / 8f)
            val y = liquidTop + sin(waveOffset + (w * 0.8f)) * 3f
            lineTo(x, y)
        }
        lineTo(tubeLeft + tubeW - 4f, tubeBottom - tubeRadius)
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(
                left = tubeLeft + 4f,
                top = tubeBottom - tubeW + 4f,
                right = tubeLeft + tubeW - 4f,
                bottom = tubeBottom - 4f
            ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 180f,
            forceMoveTo = false
        )
        lineTo(tubeLeft + 4f, liquidTop)
        close()
    }

    drawPath(
        path = liquidPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                liquidColor.copy(alpha = 0.7f),
                liquidColor.copy(alpha = 0.95f)
            ),
            startY = liquidTop,
            endY = tubeBottom
        )
    )

    // Bọt khí
    if (showBubbles) {
        bubbles.forEach { b ->
            val bx = tubeLeft + (tubeW * b.xRatio)
            val by = tubeTop + (tubeH * b.yRatio)
            if (by in liquidTop..tubeBottom) {
                drawCircle(Color.White.copy(alpha = b.alpha * 0.8f), b.radius, Offset(bx, by))
                drawCircle(Color(0xFF00E5FF).copy(alpha = b.alpha * 0.7f), b.radius * 0.4f, Offset(bx - 1f, by - 1f))
            }
        }
    }
}

private fun DrawScope.drawPrecipitate(
    tubeLeft: Float, tubeBottom: Float, tubeW: Float,
    pptColor: Color, precipitateFormula: String?
) {
    drawRoundRect(
        color = pptColor.copy(alpha = 0.95f),
        topLeft = Offset(tubeLeft + 10f, tubeBottom - 22f),
        size = Size(tubeW - 20f, 18f),
        cornerRadius = CornerRadius(9f, 9f)
    )
}

private fun DrawScope.drawSolidPellets(
    tubeLeft: Float, tubeBottom: Float, tubeW: Float,
    chemicals: List<com.example.model.Chemical>, hasPrecipitate: Boolean
) {
    if (hasPrecipitate) return
    val solid = chemicals.find { it.state == com.example.model.ChemicalState.SOLID } ?: return
    val solidColor = Color(solid.color)
    drawOval(solidColor, Offset(tubeLeft + 13f, tubeBottom - 22f), Size(16f, 11f))
    drawOval(solidColor.copy(alpha = 0.85f), Offset(tubeLeft + 27f, tubeBottom - 26f), Size(20f, 13f))
    drawOval(solidColor, Offset(tubeLeft + 39f, tubeBottom - 20f), Size(14f, 9f))
}

private fun DrawScope.drawGlassTube(
    tubeLeft: Float, tubeTop: Float, tubeW: Float,
    tubeH: Float, tubeRadius: Float, tubeBottom: Float
) {
    val glassPath = Path().apply {
        moveTo(tubeLeft, tubeTop)
        lineTo(tubeLeft, tubeBottom - tubeRadius)
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(tubeLeft, tubeBottom - tubeW, tubeLeft + tubeW, tubeBottom),
            startAngleDegrees = 180f,
            sweepAngleDegrees = -180f,
            forceMoveTo = false
        )
        lineTo(tubeLeft + tubeW, tubeTop)
    }
    drawPath(path = glassPath, color = Color(0xAA80DEEA), style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round))

    // Mép ống
    drawRoundRect(color = Color(0xCC80DEEA), topLeft = Offset(tubeLeft - 4f, tubeTop - 4f), size = Size(tubeW + 8f, 6f), cornerRadius = CornerRadius(3f, 3f))

    // Phản chiếu thủy tinh
    drawLine(color = Color.White.copy(alpha = 0.45f), start = Offset(tubeLeft + 6f, tubeTop + 12f), end = Offset(tubeLeft + 6f, tubeBottom - tubeRadius - 8f), strokeWidth = 2f, cap = StrokeCap.Round)

    // Vạch đo thể tích
    for (mark in 1..3) {
        val markY = tubeBottom - (tubeH * (0.2f + mark * 0.18f))
        drawLine(color = Color(0x88E0F7FA), start = Offset(tubeLeft + tubeW - 13f, markY), end = Offset(tubeLeft + tubeW - 4f, markY), strokeWidth = 1.5f)
    }
}

private fun DrawScope.drawThermometer(tubeLeft: Float, tubeTop: Float, tubeBottom: Float, tubeW: Float, temp: Float) {
    val thermoX = tubeLeft + tubeW * 0.28f
    val thermoTop = tubeTop - 22f
    val thermoBottom = tubeBottom - 32f
    drawRoundRect(color = Color(0xDDFFFFFF), topLeft = Offset(thermoX - 3f, thermoTop), size = Size(6f, thermoBottom - thermoTop), cornerRadius = CornerRadius(3f, 3f))
    val tempRatio = ((temp - 20f) / 60f).coerceIn(0.1f, 0.95f)
    val mercuryTop = thermoBottom - (thermoBottom - thermoTop) * tempRatio
    drawRoundRect(color = Color(0xFFFF1744), topLeft = Offset(thermoX - 1.5f, mercuryTop), size = Size(3f, thermoBottom - mercuryTop), cornerRadius = CornerRadius(1.5f, 1.5f))
    drawCircle(Color(0xFFFF1744), 4.5f, Offset(thermoX, thermoBottom))
}

private fun DrawScope.drawAlcoholBurner(canvasW: Float, tubeBottom: Float, flameScale: Float) {
    val burnerTop = tubeBottom + 10f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF00E5FF), Color(0xFFFF9100), Color(0xFFFF3D00), Color.Transparent),
            center = Offset(canvasW / 2f, burnerTop),
            radius = 22f * flameScale
        ),
        radius = 22f * flameScale,
        center = Offset(canvasW / 2f, burnerTop)
    )
    drawRect(Color(0xFFCFD8DC), Offset(canvasW / 2f - 3.5f, burnerTop + 7f), Size(7f, 9f))
    drawRoundRect(Color(0xFF546E7A), Offset(canvasW / 2f - 22f, burnerTop + 16f), Size(44f, 20f), CornerRadius(4f, 4f))
}

private data class Bubble(
    var xRatio: Float,
    var yRatio: Float,
    val radius: Float,
    val speed: Float,
    val alpha: Float
)
