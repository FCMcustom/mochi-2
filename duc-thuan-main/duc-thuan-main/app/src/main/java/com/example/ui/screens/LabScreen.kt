package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ai.SocraticAssistant
import com.example.engine.ChemicalEngine
import com.example.model.*
import com.example.ui.components.*
import kotlinx.coroutines.launch

@Composable
fun LabScreen(
    currentSubstanceA: Substance?,
    currentSubstanceB: Substance?,
    onSelectSubstanceA: (Substance?) -> Unit,
    onSelectSubstanceB: (Substance?) -> Unit,
    onRecordExperiment: (String, String, String, String, String, Boolean, String, String) -> Unit,
    onOpenSocraticChat: () -> Unit,
    isIupacMode: Boolean,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var currentStep by remember { mutableStateOf(LabStep.STEP_1_PREDICT) }
    var viewMode by remember { mutableStateOf("BOTH") } // "MACRO", "MICRO", "BOTH"
    var isReacting by remember { mutableStateOf(false) }
    var isHeating by remember { mutableStateOf(false) }
    var temperatureCelsius by remember { mutableStateOf(25.0f) }

    // Prediction state
    var selectedPrediction by remember { mutableStateOf("") }
    var selectedThermoPrediction by remember { mutableStateOf("") }

    // Explanation state
    var studentExplanationText by remember { mutableStateOf("") }
    var evaluationResult by remember { mutableStateOf("") }
    var isEvaluating by remember { mutableStateOf(false) }

    // Computed reaction outcome
    val outcome = remember(currentSubstanceA, currentSubstanceB) {
        if (currentSubstanceA != null && currentSubstanceB != null) {
            ChemicalEngine.evaluateReaction(currentSubstanceA, currentSubstanceB)
        } else null
    }

    // Effect for temperature change during reaction
    LaunchedEffect(isReacting, isHeating, outcome) {
        if (isReacting && outcome != null) {
            val targetTemp = 25.0f + outcome.tempDelta + (if (isHeating) 35.0f else 0f)
            temperatureCelsius = targetTemp
        } else if (isHeating) {
            temperatureCelsius = 65.0f
        } else {
            temperatureCelsius = 25.0f
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070F1E))
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Step Progress Bar (GDPT 2018 4-step Pedagogical process)
        PedagogicalStepper(
            currentStep = currentStep,
            onSelectStep = { currentStep = it }
        )

        // View Mode Switcher (Macro vs Micro vs Dual View)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF102138))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                listOf(
                    Triple("BOTH", "Song song", Icons.Default.ViewAgenda),
                    Triple("MACRO", "Vĩ mô", Icons.Default.Science),
                    Triple("MICRO", "Vi mô", Icons.Default.ZoomIn)
                ).forEach { (id, label, icon) ->
                    val isSelected = viewMode == id
                    Surface(
                        shape = RoundedCornerShape(9.dp),
                        color = if (isSelected) Color(0xFF00E5FF) else Color.Transparent,
                        modifier = Modifier.clickable { viewMode = id }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) Color(0xFF001F3F) else Color.LightGray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF001F3F) else Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Socratic Assistant button
            FilledTonalButton(
                onClick = onOpenSocraticChat,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFF1E3A5F),
                    contentColor = Color(0xFF80D8FF)
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "Socratic AI", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Trợ lý Socratic", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Active Simulation Stage: Macro View and/or Micro Zoom View
        if (viewMode == "BOTH" || viewMode == "MACRO") {
            MacroLabView(
                reactantA = currentSubstanceA,
                reactantB = currentSubstanceB,
                outcome = outcome,
                isReacting = isReacting,
                isHeating = isHeating,
                temperatureCelsius = temperatureCelsius,
                onToggleHeating = { isHeating = !isHeating }
            )
        }

        if (viewMode == "BOTH" || viewMode == "MICRO") {
            MicroZoomView(
                reactantA = currentSubstanceA,
                reactantB = currentSubstanceB,
                outcome = outcome,
                isReacting = isReacting
            )
        }

        // Interactive Reagent Shelf (Select Chemical Reagents)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F1E33))
                .border(1.dp, Color(0xFF1B3B60), RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "KHO HÓA CHẤT PHÒNG THÍ NGHIỆM",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 0.5.sp
                )

                if (currentSubstanceA != null || currentSubstanceB != null) {
                    TextButton(
                        onClick = {
                            onSelectSubstanceA(null)
                            onSelectSubstanceB(null)
                            isReacting = false
                            isHeating = false
                            selectedPrediction = ""
                            selectedThermoPrediction = ""
                            evaluationResult = ""
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Rửa sạch ống nghiệm", color = Color(0xFFFF8A80), fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Current selected reactants indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF142740),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (currentSubstanceA != null) Color(0xFF00E5FF) else Color(0xFF2E4C70)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    if (currentSubstanceA != null) Color(currentSubstanceA.colorArgb) else Color.Gray,
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentSubstanceA != null) "${currentSubstanceA.formula} (${if (isIupacMode) currentSubstanceA.nameIupac else currentSubstanceA.nameVi})" else "Chất thứ nhất...",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (currentSubstanceA != null) Color.White else Color.Gray,
                            fontWeight = if (currentSubstanceA != null) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF142740),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (currentSubstanceB != null) Color(0xFF00E5FF) else Color(0xFF2E4C70)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    if (currentSubstanceB != null) Color(currentSubstanceB.colorArgb) else Color.Gray,
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentSubstanceB != null) "${currentSubstanceB.formula} (${if (isIupacMode) currentSubstanceB.nameIupac else currentSubstanceB.nameVi})" else "Chất thứ hai...",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (currentSubstanceB != null) Color.White else Color.Gray,
                            fontWeight = if (currentSubstanceB != null) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Horizontal scrolling reagent pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SubstanceCatalog.ALL_SUBSTANCES.forEach { sub ->
                    val isSelectedA = currentSubstanceA?.id == sub.id
                    val isSelectedB = currentSubstanceB?.id == sub.id
                    val isSelected = isSelectedA || isSelectedB

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF00B0FF).copy(alpha = 0.25f) else Color(0xFF14263D),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF00E5FF) else Color(0xFF203B58)
                        ),
                        modifier = Modifier.clickable {
                            if (isSelectedA) {
                                onSelectSubstanceA(null)
                            } else if (isSelectedB) {
                                onSelectSubstanceB(null)
                            } else if (currentSubstanceA == null) {
                                onSelectSubstanceA(sub)
                            } else if (currentSubstanceB == null) {
                                onSelectSubstanceB(sub)
                            } else {
                                // Replace second
                                onSelectSubstanceB(sub)
                            }
                            isReacting = false
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(sub.colorArgb), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = sub.formula,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color(0xFF80D8FF) else Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isIupacMode) sub.nameIupac else sub.nameVi.substringBefore(" ("),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Contextual Content per Step
        when (currentStep) {
            LabStep.STEP_1_PREDICT -> {
                // Step 1: Hypothesis & Phenomenon Prediction
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F1F35),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = "Dự đoán", tint = Color(0xFFFFD54F))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BƯỚC 1: DỰ ĐOÁN HIỆN TƯỢNG (GIẢ THUYẾT)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Theo nguyên tắc sư phạm GDPT 2018, học sinh cần đưa ra phán đoán khoa học trước khi làm thí nghiệm:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("1. Dự đoán hiện tượng biến đổi chất:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF80D8FF))
                        Spacer(modifier = Modifier.height(4.dp))

                        val predictionOptions = listOf(
                            "Sủi bọt khí không màu (H2 hoặc CO2)",
                            "Xuất hiện kết tủa lắng đọng (BaSO4 / AgCl / Cu)",
                            "Khí màu nâu đỏ độc hại bốc lên (NO2)",
                            "Mẩu kim loại cháy sáng trên mặt nước",
                            "Không xảy ra hiện tượng phản ứng"
                        )

                        predictionOptions.forEach { opt ->
                            val isChosen = selectedPrediction == opt
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isChosen) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color(0xFF152840),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isChosen) Color(0xFF00E5FF) else Color(0xFF223E5F)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { selectedPrediction = opt }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isChosen,
                                        onClick = { selectedPrediction = opt },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00E5FF))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = opt, style = MaterialTheme.typography.bodySmall, color = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("2. Dự đoán hiệu ứng năng lượng (ΔrH°):", style = MaterialTheme.typography.labelMedium, color = Color(0xFF80D8FF))
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Phản ứng tỏa nhiệt (ΔrH° < 0)", "Phản ứng thu nhiệt (ΔrH° > 0)", "Không biến đổi nhiệt").forEach { thermoOpt ->
                                val isChosen = selectedThermoPrediction == thermoOpt
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isChosen) Color(0xFFFFB300).copy(alpha = 0.2f) else Color(0xFF152840),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isChosen) Color(0xFFFFB300) else Color(0xFF223E5F)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedThermoPrediction = thermoOpt }
                                ) {
                                    Text(
                                        text = thermoOpt,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = if (isChosen) Color(0xFFFFD54F) else Color.LightGray,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { currentStep = LabStep.STEP_2_OPERATE },
                            enabled = currentSubstanceA != null && currentSubstanceB != null,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF002244)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Xác nhận giả thuyết & Sang Bước 2: Thao tác", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            LabStep.STEP_2_OPERATE -> {
                // Step 2: Safe Lab Operation & React Trigger
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F1F35),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Thao tác", tint = Color(0xFF00E676))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BƯỚC 2: TIẾN HÀNH THÍ NGHIỆM ẢO AN TOÀN",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E676)
                            )
                        }

                        if (outcome?.isDangerous == true) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF3E1218),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252))
                            ) {
                                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = "Nguy hiểm", tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = outcome.dangerWarningVi,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFFF8A80),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    isReacting = true
                                    currentStep = LabStep.STEP_3_OBSERVE
                                },
                                enabled = currentSubstanceA != null && currentSubstanceB != null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isReacting) Color(0xFF2E7D32) else Color(0xFF00E5FF),
                                    contentColor = Color(0xFF002244)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isReacting) "Đang phản ứng..." else "Kích hoạt phản ứng", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { isReacting = false },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray)
                            ) {
                                Text("Dừng")
                            }
                        }
                    }
                }
            }

            LabStep.STEP_3_OBSERVE -> {
                // Step 3: Dual Macro & Micro Observations
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F1F35),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Visibility, contentDescription = "Quan sát", tint = Color(0xFF00E5FF))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BƯỚC 3: QUAN SÁT VÀ GHI NHẬN THỰC THẾ",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E5FF)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (outcome != null) {
                            Text("1. Hiện tượng vĩ mô:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF80D8FF))
                            Text(
                                text = outcome.phenomenaVi,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("2. Cấp độ vi mô (Trao đổi electron & hạt ion):", style = MaterialTheme.typography.labelSmall, color = Color(0xFFB388FF))
                            Text(
                                text = outcome.microExplanationVi,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCFD8DC),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("3. Phương trình hóa học chuẩn:", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFFD54F))
                            Text(
                                text = outcome.balancedEquation,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFE082)
                            )
                            Text(
                                text = "Phương trình ion rút gọn: ${outcome.netIonicEquation}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { currentStep = LabStep.STEP_4_EXPLAIN },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF002244)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Sang Bước 4: Giải thích & Đánh giá năng lực BKT", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            LabStep.STEP_4_EXPLAIN -> {
                // Step 4: Explanation & Knowledge Tracing Evaluation
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F1F35),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, contentDescription = "Giải thích", tint = Color(0xFFB388FF))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BƯỚC 4: GIẢI THÍCH BẢN CHẤT & CHẤM ĐIỂM BKT",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB388FF)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Hãy viết lời giải thích của em về quá trình chuyển dịch electron và năng lượng phản ứng vừa quan sát:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = studentExplanationText,
                            onValueChange = { studentExplanationText = it },
                            placeholder = { Text("Ví dụ: Kẽm nhường 2e cho ion H+ tạo khí H2, phản ứng tỏa nhiệt làm tăng nhiệt kế...", color = Color.Gray, fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF0A1524),
                                unfocusedContainerColor = Color(0xFF0A1524),
                                focusedBorderColor = Color(0xFF00E5FF),
                                unfocusedBorderColor = Color(0xFF1E3A5F),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (outcome != null) {
                                    isEvaluating = true
                                    coroutineScope.launch {
                                        val eval = SocraticAssistant.evaluateStudentExplanation(studentExplanationText, outcome)
                                        evaluationResult = eval
                                        isEvaluating = false

                                        // Check hypothesis confirmation
                                        val isConfirmed = selectedPrediction.isNotBlank() &&
                                                (outcome.gasFormula != null && selectedPrediction.contains("khí")) ||
                                                (outcome.precipitateFormula != null && selectedPrediction.contains("kết tủa")) ||
                                                (outcome.reactionType == ReactionType.NO_REACTION && selectedPrediction.contains("Không"))

                                        onRecordExperiment(
                                            "${currentSubstanceA?.formula ?: ""} + ${currentSubstanceB?.formula ?: ""}",
                                            currentSubstanceA?.id ?: "",
                                            currentSubstanceB?.id ?: "",
                                            selectedPrediction,
                                            outcome.phenomenaVi,
                                            isConfirmed,
                                            studentExplanationText,
                                            outcome.competencyId
                                        )
                                    }
                                }
                            },
                            enabled = studentExplanationText.isNotBlank() && !isEvaluating,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF002244)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isEvaluating) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF002244), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Đang chấm điểm bằng NLP GDPT 2018...")
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Nộp bài & Cập nhật vết kiến thức BKT", fontWeight = FontWeight.Bold)
                            }
                        }

                        // NLP evaluation result card
                        if (evaluationResult.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF0B243B),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = evaluationResult,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(12.dp),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
