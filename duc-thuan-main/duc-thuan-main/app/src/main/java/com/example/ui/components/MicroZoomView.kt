package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ReactionOutcome
import com.example.model.Substance
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class MicroParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val type: String, // "H+", "Cl-", "Zn2+", "H2", "H2O", "Cu2+", "NO3-"
    val color: Color,
    val radius: Float
)

@Composable
fun MicroZoomView(
    reactantA: Substance?,
    reactantB: Substance?,
    outcome: ReactionOutcome?,
    isReacting: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "micro_anim")

    // Time ticker for smooth Brownian particle dynamics
    val ticker by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ticker"
    )

    // Electron transfer leap animation
    val electronProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "electron"
    )

    // Particle pool
    val particles = remember {
        val list = mutableListOf<MicroParticle>()
        val rnd = Random(123)
        // Protons H+
        repeat(8) {
            list.add(MicroParticle(50f + rnd.nextFloat() * 250f, 40f + rnd.nextFloat() * 100f, rnd.nextFloat() * 1.5f - 0.75f, rnd.nextFloat() * 1.5f - 0.75f, "H+", Color(0xFFFF5252), 9f))
        }
        // Chloride Cl-
        repeat(8) {
            list.add(MicroParticle(50f + rnd.nextFloat() * 250f, 40f + rnd.nextFloat() * 100f, rnd.nextFloat() * 1.2f - 0.6f, rnd.nextFloat() * 1.2f - 0.6f, "Cl-", Color(0xFF69F0AE), 14f))
        }
        // Water molecules H2O background
        repeat(12) {
            list.add(MicroParticle(40f + rnd.nextFloat() * 260f, 30f + rnd.nextFloat() * 120f, rnd.nextFloat() * 0.8f - 0.4f, rnd.nextFloat() * 0.8f - 0.4f, "H2O", Color(0xFF40C4FF).copy(alpha = 0.4f), 7f))
        }
        list
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF071426))
            .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        // Micro View Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF7C4DFF).copy(alpha = 0.25f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = "Micro zoom",
                        tint = Color(0xFFB388FF),
                        modifier = Modifier.padding(5.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "VI MÔ (MICRO PARTICLE ZOOM)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB388FF),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Độ phóng đại: 10,000,000x • Cấp độ Nguyên tử & Ion",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF1F2833)
            ) {
                Text(
                    text = "GDPT 2018",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64FFDA),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Particle Canvas Stage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF040B14))
                .border(1.dp, Color(0xFF1B3B6F), RoundedCornerShape(14.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Radial microscope viewport vignette
                drawCircle(
                    color = Color(0xFF0B2545).copy(alpha = 0.35f),
                    radius = w * 0.6f,
                    center = Offset(w / 2f, h / 2f)
                )

                // Render Crystal Metal Lattice (Zn / Fe) at the bottom 30% of canvas
                val latticeTop = h * 0.70f
                val atomRadius = 15f
                val rows = 3
                val cols = (w / (atomRadius * 2.2f)).toInt() + 1

                for (row in 0 until rows) {
                    val rowY = latticeTop + row * (atomRadius * 1.8f)
                    val xOffset = if (row % 2 == 1) atomRadius else 0f
                    for (col in 0..cols) {
                        val atomX = col * (atomRadius * 2.2f) + xOffset
                        // Thermal vibration
                        val jitterX = sin(ticker * 3f + col + row) * 0.8f
                        val jitterY = cos(ticker * 3f + col + row) * 0.8f

                        // Metallic lattice atom (Zn atom in silver-grey)
                        drawCircle(
                            color = Color(0xFF90A4AE),
                            radius = atomRadius,
                            center = Offset(atomX + jitterX, rowY + jitterY)
                        )
                        drawCircle(
                            color = Color(0xFFB0BEC5),
                            radius = atomRadius * 0.5f,
                            center = Offset(atomX + jitterX - 3f, rowY + jitterY - 3f)
                        )
                        // Metallic bond lines between lattice atoms
                        if (col < cols) {
                            drawLine(
                                color = Color(0xFF546E7A).copy(alpha = 0.5f),
                                start = Offset(atomX, rowY),
                                end = Offset(atomX + atomRadius * 2.2f, rowY),
                                strokeWidth = 1.5f
                            )
                        }
                    }
                }

                // Subtitle label for crystal lattice
                val latticeLabel = if (reactantA?.id == "fe" || reactantB?.id == "fe") "Mạng tinh thể Sắt (Fe)" else "Mạng tinh thể Kẽm (Zn)"

                // Floating aqueous ions (H+, Cl-, H2O)
                particles.forEachIndexed { i, p ->
                    // Brownian motion update with ticker
                    val ox = sin(ticker + i) * 12f
                    val oy = cos(ticker + i * 1.3f) * 10f
                    val curX = (p.x + ox).coerceIn(20f, w - 20f)
                    val curY = (p.y + oy).coerceIn(20f, latticeTop - 15f)

                    // Draw ion sphere
                    drawCircle(p.color, p.radius, Offset(curX, curY))
                    // Ion charge glow
                    drawCircle(p.color.copy(alpha = 0.35f), p.radius + 4f, Offset(curX, curY), style = Stroke(1.5f))

                    // If H2 molecule is formed (diatomic dumbbell)
                    if (isReacting && i % 4 == 0 && (outcome?.gasFormula == "H2" || outcome?.gasFormula == "CO2")) {
                        // Partner atom
                        val partnerX = curX + 13f
                        val partnerY = curY
                        drawCircle(Color(0xFFE0E0E0), 8f, Offset(partnerX, partnerY))
                        // Covalent bond cylinder
                        drawLine(Color(0xFFBDBDBD), Offset(curX, curY), Offset(partnerX, partnerY), strokeWidth = 3f)
                    }
                }

                // Electron Leap Animation (Zn atom donating 2e- to 2H+ ions)
                if (isReacting) {
                    val donorX = w * 0.45f
                    val donorY = latticeTop
                    val targetX = w * 0.55f + sin(ticker) * 15f
                    val targetY = h * 0.40f

                    val eX = donorX + (targetX - donorX) * electronProgress
                    val eY = donorY + (targetY - donorY) * electronProgress

                    // Golden glowing electron energy packets (2e-)
                    drawCircle(Color(0xFFFFEA00), 5.5f, Offset(eX - 4f, eY))
                    drawCircle(Color(0xFFFFEA00), 5.5f, Offset(eX + 4f, eY))
                    drawCircle(Color(0xFFFFD54F).copy(alpha = 0.5f), 12f, Offset(eX, eY))

                    // Electron trajectory curved dash line
                    drawLine(
                        color = Color(0xFFFFD54F).copy(alpha = 0.4f),
                        start = Offset(donorX, donorY),
                        end = Offset(targetX, targetY),
                        strokeWidth = 1.5f
                    )
                }
            }

            // Legend Overlay (Top Left)
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color(0xCC08121E), RoundedCornerShape(8.dp))
                    .border(0.5.dp, Color(0xFF224466), RoundedCornerShape(8.dp))
                    .padding(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Color(0xFFFF5252), CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cation H⁺", color = Color.White, fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF69F0AE), CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Anion Cl⁻", color = Color.White, fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF90A4AE), CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nguyên tử Zn", color = Color.White, fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(8.dp).background(Color(0xFFFFEA00), CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Electron (e⁻)", color = Color.White, fontSize = 10.sp)
                }
            }

            // Electron Half-reaction equation badge (Bottom Right)
            if (isReacting && outcome != null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xEE122338),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(
                            text = "Bản chất chuyển dịch electron:",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = Color(0xFF80D8FF)
                        )
                        Text(
                            text = if (outcome.netIonicEquation.isNotBlank()) outcome.netIonicEquation else "Zn → Zn²⁺ + 2e⁻ ; 2H⁺ + 2e⁻ → H₂↑",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Microscopic Explanation Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF102238),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A385C))
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Giải thích vi mô",
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(18.dp).padding(top = 1.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = outcome?.microExplanationVi
                        ?: "Góc nhìn vi mô thể hiện sự va chạm hiệu quả giữa các phân tử/ion và quá trình truyền electron theo thuyết va chạm hoạt động của SGK 2018.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCFD8DC),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
