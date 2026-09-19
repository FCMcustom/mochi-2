package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Science
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
import com.example.model.ReactionOutcome
import com.example.model.Substance
import kotlin.math.sin
import kotlin.random.Random

data class BubbleParticle(
    var xRatio: Float,
    var yRatio: Float,
    var radius: Float,
    var speed: Float,
    var alpha: Float
)

@Composable
fun MacroLabView(
    reactantA: Substance?,
    reactantB: Substance?,
    outcome: ReactionOutcome?,
    isReacting: Boolean,
    isHeating: Boolean,
    temperatureCelsius: Float,
    onToggleHeating: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Infinite transition for continuous bubble and fluid ripple motion
    val infiniteTransition = rememberInfiniteTransition(label = "macro_anim")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    val flamePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame"
    )

    // Animated temperature display
    val animatedTemp by animateFloatAsState(
        targetValue = temperatureCelsius,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "temp"
    )

    // Bubble simulation state
    val bubbles = remember {
        List(25) {
            BubbleParticle(
                xRatio = 0.2f + Random.nextFloat() * 0.6f,
                yRatio = 0.4f + Random.nextFloat() * 0.5f,
                radius = 2.5f + Random.nextFloat() * 4.5f,
                speed = 0.003f + Random.nextFloat() * 0.008f,
                alpha = 0.6f + Random.nextFloat() * 0.4f
            )
        }
    }

    LaunchedEffect(isReacting, outcome?.gasRate) {
        val rate = outcome?.gasRate ?: 0f
        if (isReacting && rate > 0f) {
            while (true) {
                bubbles.forEach { b ->
                    b.yRatio -= b.speed * (1f + rate * 2.5f)
                    if (b.yRatio < 0.28f) { // reached fluid surface
                        b.yRatio = 0.85f + Random.nextFloat() * 0.08f
                        b.xRatio = 0.25f + Random.nextFloat() * 0.5f
                    }
                }
                kotlinx.coroutines.delay(32)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0D1B2A))
            .border(1.dp, Color(0xFF1E3A5F), RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        // Header info bar with Real-time Lab Telemetry
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF00E5FF).copy(alpha = 0.2f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = "Macro apparatus",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.padding(5.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "VĨ MÔ (MACRO APPARATUS)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF80D8FF),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (isReacting) "Đang diễn ra phản ứng" else "Trạng thái sẵn sàng",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isReacting) Color(0xFF00E676) else Color.LightGray
                    )
                }
            }

            // Real-time Thermometer readout badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1B2A47),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E4C7E))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Thermostat,
                        contentDescription = "Nhiệt độ",
                        tint = if (animatedTemp > 30f) Color(0xFFFF5252) else Color(0xFF00E5FF),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "%.1f°C".format(animatedTemp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (animatedTemp > 30f) Color(0xFFFF8A80) else Color(0xFFE0F7FA)
                    )
                    if (outcome != null && outcome.deltaH != 0f) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (outcome.deltaH < 0) "(Tỏa nhiệt)" else "(Thu nhiệt)",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = Color(0xFFFFD54F)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Central Lab Stage: Test Tube & Thermometer Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasW = size.width
                val canvasH = size.height

                // Draw background grid lines (lab bench tile aesthetic)
                val gridColor = Color(0xFF13283E)
                for (x in 0..canvasW.toInt() step 40) {
                    drawLine(gridColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), canvasH), strokeWidth = 1f)
                }
                for (y in 0..canvasH.toInt() step 40) {
                    drawLine(gridColor, Offset(0f, y.toFloat()), Offset(canvasW, y.toFloat()), strokeWidth = 1f)
                }

                // Lab stand / Clamp holder
                val standX = canvasW * 0.28f
                drawLine(
                    color = Color(0xFF455A64),
                    start = Offset(standX, 10f),
                    end = Offset(standX, canvasH - 20f),
                    strokeWidth = 6f
                )
                // Stand base
                drawLine(
                    color = Color(0xFF37474F),
                    start = Offset(standX - 40f, canvasH - 20f),
                    end = Offset(standX + 60f, canvasH - 20f),
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
                // Stand clamp arm holding test tube
                drawLine(
                    color = Color(0xFF607D8B),
                    start = Offset(standX, canvasH * 0.35f),
                    end = Offset(canvasW * 0.5f - 36f, canvasH * 0.35f),
                    strokeWidth = 5f
                )

                // Test Tube Geometry
                val tubeW = 72.dp.toPx()
                val tubeH = 175.dp.toPx()
                val tubeLeft = (canvasW - tubeW) / 2f
                val tubeTop = 20.dp.toPx()
                val tubeBottom = tubeTop + tubeH
                val tubeRadius = tubeW / 2f

                // Reaction Fumes / Gas cloud coming out of tube neck
                if (isReacting && outcome?.gasFormula != null) {
                    val fumeColor = if (outcome.gasColor != 0x00000000L) Color(outcome.gasColor) else Color(0x66FFFFFF)
                    for (i in 0..6) {
                        val fumeOffset = sin(waveOffset + i) * 16f
                        drawCircle(
                            color = fumeColor.copy(alpha = 0.45f - i * 0.05f),
                            radius = 16f + i * 4f,
                            center = Offset(canvasW / 2f + fumeOffset, tubeTop - 15f - (i * 12f))
                        )
                    }
                }

                // Liquid fill inside test tube
                val liquidHeightRatio = if (reactantA != null && reactantB != null) 0.65f
                else if (reactantA != null || reactantB != null) 0.45f
                else 0.0f

                if (liquidHeightRatio > 0f) {
                    val liquidTop = tubeBottom - (tubeH * liquidHeightRatio)

                    val fluidColor = if (isReacting && outcome != null) {
                        Color(outcome.solutionFinalColor)
                    } else if (reactantA?.state == com.example.model.PhysicalState.LIQUID) {
                        Color(reactantA.colorArgb)
                    } else if (reactantB?.state == com.example.model.PhysicalState.LIQUID) {
                        Color(reactantB.colorArgb)
                    } else {
                        Color(0x334DD0E1)
                    }

                    val liquidPath = Path().apply {
                        moveTo(tubeLeft + 4f, liquidTop)
                        // Surface wave
                        val waveSteps = 8
                        for (w in 0..waveSteps) {
                            val x = tubeLeft + 4f + (tubeW - 8f) * (w / waveSteps.toFloat())
                            val y = liquidTop + sin(waveOffset + (w * 0.8f)) * 3.5f
                            lineTo(x, y)
                        }
                        // Right wall down to rounded bottom
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

                    // Fluid gradient
                    drawPath(
                        path = liquidPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(fluidColor.copy(alpha = 0.75f), fluidColor.copy(alpha = 0.95f)),
                            startY = liquidTop,
                            endY = tubeBottom
                        )
                    )

                    // Precipitate layer at the bottom
                    if (isReacting && outcome?.precipitateColor != null) {
                        val pptColor = Color(outcome.precipitateColor)
                        drawRoundRect(
                            color = pptColor.copy(alpha = 0.95f),
                            topLeft = Offset(tubeLeft + 10f, tubeBottom - 26f),
                            size = Size(tubeW - 20f, 20f),
                            cornerRadius = CornerRadius(10f, 10f)
                        )
                    }

                    // Solid metal granules at bottom (Zn, Fe, Cu)
                    val solidReactant = if (reactantA?.state == com.example.model.PhysicalState.SOLID) reactantA
                                        else if (reactantB?.state == com.example.model.PhysicalState.SOLID) reactantB
                                        else null

                    if (solidReactant != null && outcome?.precipitateColor == null) {
                        val solidColor = Color(solidReactant.colorArgb)
                        // Draw metal pellets / chips
                        drawOval(solidColor, Offset(tubeLeft + 14f, tubeBottom - 24f), Size(18f, 12f))
                        drawOval(solidColor.copy(alpha = 0.85f), Offset(tubeLeft + 30f, tubeBottom - 28f), Size(22f, 14f))
                        drawOval(solidColor, Offset(tubeLeft + 44f, tubeBottom - 22f), Size(16f, 10f))
                    }

                    // Animated reaction bubbles
                    if (isReacting && (outcome?.gasRate ?: 0f) > 0f) {
                        bubbles.forEach { b ->
                            val bx = tubeLeft + (tubeW * b.xRatio)
                            val by = tubeTop + (tubeH * b.yRatio)
                            if (by in liquidTop..tubeBottom) {
                                drawCircle(
                                    color = Color.White.copy(alpha = b.alpha),
                                    radius = b.radius,
                                    center = Offset(bx, by)
                                )
                                // Bubble highlight
                                drawCircle(
                                    color = Color(0xFF00E5FF).copy(alpha = b.alpha * 0.8f),
                                    radius = b.radius * 0.4f,
                                    center = Offset(bx - 1f, by - 1f)
                                )
                            }
                        }
                    }

                    // Violent sodium reaction flame & sparks (Na + H2O)
                    if (isReacting && outcome?.hasFlame == true) {
                        val flameCenterX = tubeLeft + tubeW * 0.5f + sin(waveOffset * 3f) * 12f
                        val flameCenterY = liquidTop - 8f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFFFEA00), Color(0xFFFF6D00), Color.Transparent),
                                center = Offset(flameCenterX, flameCenterY),
                                radius = 28f * flamePulse
                            ),
                            radius = 28f * flamePulse,
                            center = Offset(flameCenterX, flameCenterY)
                        )
                        // Sodium molten silver ball
                        drawCircle(Color(0xFFE0E0E0), 7f, Offset(flameCenterX, flameCenterY + 4f))
                    }
                }

                // Test Tube Glass Body (High realism rounded container)
                val glassPath = Path().apply {
                    moveTo(tubeLeft, tubeTop)
                    lineTo(tubeLeft, tubeBottom - tubeRadius)
                    arcTo(
                        rect = androidx.compose.ui.geometry.Rect(
                            left = tubeLeft,
                            top = tubeBottom - tubeW,
                            right = tubeLeft + tubeW,
                            bottom = tubeBottom
                        ),
                        startAngleDegrees = 180f,
                        sweepAngleDegrees = -180f,
                        forceMoveTo = false
                    )
                    lineTo(tubeLeft + tubeW, tubeTop)
                }

                // Glass outline stroke
                drawPath(
                    path = glassPath,
                    color = Color(0xAA80DEEA),
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Test Tube Lip / Rim collar
                drawRoundRect(
                    color = Color(0xCC80DEEA),
                    topLeft = Offset(tubeLeft - 5f, tubeTop - 4f),
                    size = Size(tubeW + 10f, 7f),
                    cornerRadius = CornerRadius(3f, 3f)
                )

                // Glass highlights / reflections
                drawLine(
                    color = Color.White.copy(alpha = 0.5f),
                    start = Offset(tubeLeft + 7f, tubeTop + 15f),
                    end = Offset(tubeLeft + 7f, tubeBottom - tubeRadius - 10f),
                    strokeWidth = 2.5f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.25f),
                    start = Offset(tubeLeft + tubeW - 7f, tubeTop + 15f),
                    end = Offset(tubeLeft + tubeW - 7f, tubeBottom - tubeRadius - 10f),
                    strokeWidth = 1.5f,
                    cap = StrokeCap.Round
                )

                // Volume markings (5ml, 10ml, 15ml)
                for (mark in 1..3) {
                    val markY = tubeBottom - (tubeH * (0.2f + mark * 0.18f))
                    drawLine(
                        color = Color(0x88E0F7FA),
                        start = Offset(tubeLeft + tubeW - 14f, markY),
                        end = Offset(tubeLeft + tubeW - 4f, markY),
                        strokeWidth = 1.5f
                    )
                }

                // Digital Lab Thermometer immersed in liquid
                val thermoX = tubeLeft + tubeW * 0.3f
                val thermoTop = tubeTop - 25f
                val thermoBottom = tubeBottom - 35f

                // Thermometer glass stem
                drawRoundRect(
                    color = Color(0xDDFFFFFF),
                    topLeft = Offset(thermoX - 3.5f, thermoTop),
                    size = Size(7f, thermoBottom - thermoTop),
                    cornerRadius = CornerRadius(3.5f, 3.5f)
                )
                // Red indicator line
                val tempRatio = ((animatedTemp - 20f) / 60f).coerceIn(0.1f, 0.95f)
                val mercuryTop = thermoBottom - (thermoBottom - thermoTop) * tempRatio
                drawRoundRect(
                    color = Color(0xFFFF1744),
                    topLeft = Offset(thermoX - 2f, mercuryTop),
                    size = Size(4f, thermoBottom - mercuryTop),
                    cornerRadius = CornerRadius(2f, 2f)
                )
                // Thermometer bulb
                drawCircle(Color(0xFFFF1744), 5f, Offset(thermoX, thermoBottom))

                // Alcohol Burner / Flame beneath tube if heating is toggled
                if (isHeating) {
                    val burnerTop = tubeBottom + 12f
                    // Burner flame
                    val flameBrush = Brush.radialGradient(
                        colors = listOf(Color(0xFF00E5FF), Color(0xFFFF9100), Color(0xFFFF3D00), Color.Transparent),
                        center = Offset(canvasW / 2f, burnerTop),
                        radius = 24f * flamePulse
                    )
                    drawCircle(flameBrush, 24f * flamePulse, Offset(canvasW / 2f, burnerTop))

                    // Burner wick and glass cap
                    drawRect(Color(0xFFCFD8DC), Offset(canvasW / 2f - 4f, burnerTop + 8f), Size(8f, 10f))
                    drawRoundRect(
                        Color(0xFF546E7A),
                        Offset(canvasW / 2f - 24f, burnerTop + 18f),
                        Size(48f, 22f),
                        CornerRadius(4f, 4f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lab Bench Controls & Safety Tools
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Heating Button (Đèn cồn)
            OutlinedButton(
                onClick = onToggleHeating,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isHeating) Color(0xFFD84315) else Color(0xFF1B2A47),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "Đèn cồn",
                    tint = if (isHeating) Color(0xFFFFD54F) else Color.LightGray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isHeating) "Đang đun nóng (Tắt đèn cồn)" else "Đun đèn cồn",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            // Chemical hazard tag if any
            if (outcome?.isDangerous == true) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFB71C1C).copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252))
                ) {
                    Text(
                        text = "⚠️ NGUY HIỂM / KHÍ ĐỘC",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
