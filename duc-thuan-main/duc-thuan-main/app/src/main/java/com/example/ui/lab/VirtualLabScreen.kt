package com.example.ui.lab

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ai.SocraticUiState
import com.example.engine.ChemicalEngine
import com.example.model.ReactionResult

/**
 * Màn hình phòng thí nghiệm ảo Virtual Lab.
 * Bố cục thích ứng: dọc cho điện thoại, ngang cho máy tính bảng.
 *
 * Khu vực 1: ChemicalShelf - Kệ hóa chất chọn chất phản ứng
 * Khu vực 2: TestTubeCanvas - Ống nghiệm với animation hiệu ứng phản ứng
 * Khu vực 3: SocraticPanel + Bảng hiện tượng vĩ mô
 */
@Composable
fun VirtualLabScreen(
    windowSizeClass: WindowWidthSizeClass? = null,
    viewModel: VirtualLabViewModel = viewModel(factory = VirtualLabViewModelFactory()),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val testTubeState by viewModel.testTubeState.collectAsState()
    val socraticUiState by viewModel.socraticUiState.collectAsState()
    val socraticMessages by viewModel.socraticMessages.collectAsState()

    // Xác định bố cục: Grid cho màn hình rộng, Column cho màn hình hẹp
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
    val isWideScreen = windowSizeClass == WindowWidthSizeClass.Expanded ||
            windowSizeClass == WindowWidthSizeClass.Medium ||
            configuration.screenWidthDp >= 600

    // Xử lý lỗi
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.labErrorMessage) {
        uiState.labErrorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF070F1E),
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isWideScreen || isLandscape) {
                // Bố cục ngang cho máy tính bảng
                WideLayout(
                    uiState = uiState,
                    testTubeState = testTubeState,
                    socraticUiState = socraticUiState,
                    socraticMessages = socraticMessages,
                    viewModel = viewModel
                )
            } else {
                // Bố cục dọc cho điện thoại
                CompactLayout(
                    uiState = uiState,
                    testTubeState = testTubeState,
                    socraticUiState = socraticUiState,
                    socraticMessages = socraticMessages,
                    viewModel = viewModel
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// BỐ CỤC DỌC (ĐIỆN THOẠI)
// ══════════════════════════════════════════════════════

@Composable
private fun CompactLayout(
    uiState: VirtualLabUiState,
    testTubeState: TestTubeState,
    socraticUiState: SocraticUiState,
    socraticMessages: List<com.example.ai.ChatMessage>,
    viewModel: VirtualLabViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header
        LabHeader(
            labStep = uiState.labStep,
            onBack = { viewModel.onEvent(VirtualLabEvent.GoBackStep) },
            onReset = { viewModel.onEvent(VirtualLabEvent.ResetLab) }
        )

        // Ống nghiệm
        TestTubeCanvas(
            testTubeState = testTubeState,
            temperature = uiState.temperatureCelsius,
            isHeating = uiState.isHeating,
            selectedChemicals = uiState.selectedChemicals
        )

        // Nút điều khiển thí nghiệm
        LabControlButtons(
            uiState = uiState,
            viewModel = viewModel
        )

        // Kệ hóa chất
        ChemicalShelf(
            chemicals = VIRTUAL_LAB_CHEMICALS,
            selectedChemicals = uiState.selectedChemicals,
            onChemicalSelected = { viewModel.onEvent(VirtualLabEvent.SelectChemical(it)) },
            onRemoveChemical = { viewModel.onEvent(VirtualLabEvent.RemoveLastChemical) },
            isGridLayout = false
        )

        // Hiện tượng vĩ mô (chưa hiện phương trình)
        MacroscopicPhenomenonCard(
            uiState = uiState,
            viewModel = viewModel
        )

        // Panel Socratic
        SocraticPanel(
            uiState = socraticUiState,
            messages = socraticMessages,
            onSendMessage = { viewModel.onEvent(VirtualLabEvent.SendSocraticQuestion(it)) },
            onClearHistory = { /* viewModel.clearHistory */ },
            modifier = Modifier.heightIn(min = 200.dp, max = 320.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ══════════════════════════════════════════════════════
// BỐ CỤC NGANG (MÁY TÍNH BẢNG / PC)
// ══════════════════════════════════════════════════════

@Composable
private fun WideLayout(
    uiState: VirtualLabUiState,
    testTubeState: TestTubeState,
    socraticUiState: SocraticUiState,
    socraticMessages: List<com.example.ai.ChatMessage>,
    viewModel: VirtualLabViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Cột trái: Ống nghiệm + Kệ hóa chất
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LabHeader(
                labStep = uiState.labStep,
                onBack = { viewModel.onEvent(VirtualLabEvent.GoBackStep) },
                onReset = { viewModel.onEvent(VirtualLabEvent.ResetLab) }
            )

            TestTubeCanvas(
                testTubeState = testTubeState,
                temperature = uiState.temperatureCelsius,
                isHeating = uiState.isHeating,
                selectedChemicals = uiState.selectedChemicals
            )

            LabControlButtons(
                uiState = uiState,
                viewModel = viewModel
            )

            ChemicalShelf(
                chemicals = VIRTUAL_LAB_CHEMICALS,
                selectedChemicals = uiState.selectedChemicals,
                onChemicalSelected = { viewModel.onEvent(VirtualLabEvent.SelectChemical(it)) },
                onRemoveChemical = { viewModel.onEvent(VirtualLabEvent.RemoveLastChemical) },
                isGridLayout = true
            )
        }

        // Cột phải: Hiện tượng + Socratic Panel
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MacroscopicPhenomenonCard(
                uiState = uiState,
                viewModel = viewModel
            )

            SocraticPanel(
                uiState = socraticUiState,
                messages = socraticMessages,
                onSendMessage = { viewModel.onEvent(VirtualLabEvent.SendSocraticQuestion(it)) },
                onClearHistory = { /* viewModel.clearHistory */ },
                isExpanded = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ══════════════════════════════════════════════════════
// HEADER VÀ ĐIỀU KHIỂN
// ══════════════════════════════════════════════════════

@Composable
private fun LabHeader(
    labStep: LabStep,
    onBack: () -> Unit,
    onReset: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F1E33),
        border = BorderStroke(1.dp, Color(0xFF1B3B60))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "PHÒNG THÍ NGHIỆM ẢO",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        letterSpacing = 0.5.sp
                    )
                    StepIndicator(currentStep = labStep)
                }
            }

            Row {
                if (labStep != LabStep.SELECT_CHEMICALS) {
                    TextButton(onClick = onBack, contentPadding = PaddingValues(4.dp)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Quay lại", color = Color.LightGray, fontSize = 12.sp)
                    }
                }
                TextButton(onClick = onReset, contentPadding = PaddingValues(4.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color(0xFFFF8A80), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Rửa ống nghiệm", color = Color(0xFFFF8A80), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStep: LabStep) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LabStep.entries.forEachIndexed { index, step ->
            val isActive = step == currentStep
            val isPast = step.ordinal < currentStep.ordinal
            val color = when {
                isActive -> Color(0xFF00E5FF)
                isPast -> Color(0xFF00E676)
                else -> Color(0xFF2E4C70)
            }
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, RoundedCornerShape(3.dp))
            )
            if (index < LabStep.entries.size - 1) {
                Box(
                    modifier = Modifier
                        .width(12.dp)
                        .height(1.dp)
                        .background(if (isPast) Color(0xFF00E676) else Color(0xFF2E4C70))
                )
            }
        }
    }
}

@Composable
private fun LabControlButtons(
    uiState: VirtualLabUiState,
    viewModel: VirtualLabViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Nút kích hoạt phản ứng
        Button(
            onClick = { viewModel.onEvent(VirtualLabEvent.TriggerReaction) },
            enabled = uiState.selectedChemicals.size >= 2 && !uiState.reactionOccurred,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (uiState.reactionOccurred) Color(0xFF2E7D32) else Color(0xFF00E5FF),
                contentColor = Color(0xFF002244)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = if (uiState.reactionOccurred) Icons.Default.Check else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (uiState.reactionOccurred) "Đã phản ứng" else "Kích hoạt phản ứng",
                fontWeight = FontWeight.Bold
            )
        }

        // Nút đun nóng
        OutlinedButton(
            onClick = { viewModel.onEvent(VirtualLabEvent.ToggleHeating) },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (uiState.isHeating) Color(0xFFD84315) else Color(0xFF1B2A47),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "Đun nóng",
                tint = if (uiState.isHeating) Color(0xFFFFD54F) else Color.LightGray,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (uiState.isHeating) "Tắt đun" else "Đun nóng", fontSize = 12.sp)
        }
    }
}

// ══════════════════════════════════════════════════════
// THẺ HIỆN TƯỢNG VĨ MÔ
// ══════════════════════════════════════════════════════

@Composable
private fun MacroscopicPhenomenonCard(
    uiState: VirtualLabUiState,
    viewModel: VirtualLabViewModel
) {
    val chemicals = uiState.selectedChemicals
    val reactionOccurred = uiState.reactionOccurred
    val showEquation = uiState.showEquation

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F1F35),
        border = BorderStroke(1.dp, Color(0xFF1E3A5F)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "HIỆN TƯỢNG QUAN SÁT (VĨ MÔ)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            when {
                chemicals.size < 2 -> {
                    Text(
                        text = "Hãy chọn ít nhất 2 chất từ kệ hóa chất để bắt đầu thí nghiệm.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                !reactionOccurred -> {
                    // Trước phản ứng: Hiển thị dự đoán
                    Column {
                        if (chemicals.size == 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                chemicals.forEach { chem ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(Color(chem.color), RoundedCornerShape(8.dp))
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = chem.formula,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = chem.name,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFF1E3A5F))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Bạn dự đoán hiện tượng gì sẽ xảy ra khi trộn hai chất này?",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFFD54F),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }

                else -> {
                    // Sau phản ứng: Hiển thị hiện tượng (CHƯA hiện phương trình)
                    val result = ChemicalEngine.mix(chemicals[0], chemicals[1])
                    PhenomenonDisplay(result = result, showEquation = showEquation, onRevealEquation = {
                        viewModel.onEvent(VirtualLabEvent.RevealEquation)
                    })
                }
            }
        }
    }
}

@Composable
private fun PhenomenonDisplay(
    result: ReactionResult,
    showEquation: Boolean,
    onRevealEquation: () -> Unit
) {
    Column {
        // Các icon hiện tượng
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (result.gasReleased != null) {
                PhenomenonChip(
                    icon = Icons.Default.BubbleChart,
                    label = "Bọt khí ${result.gasReleased}",
                    color = Color(0xFF00E5FF)
                )
            }
            if (result.precipitate != null) {
                PhenomenonChip(
                    icon = Icons.Default.Layers,
                    label = "Kết tủa ${result.precipitate}",
                    color = Color(0xFFFFFFFF)
                )
            }
            if (result.temperatureChange > 2f) {
                PhenomenonChip(
                    icon = Icons.Default.Thermostat,
                    label = if (result.temperatureChange > 0) "Tỏa nhiệt" else "Thu nhiệt",
                    color = Color(0xFFFFB300)
                )
            }
            if (result.gasReleased == null && result.precipitate == null) {
                PhenomenonChip(
                    icon = Icons.Default.CheckCircle,
                    label = "Dung dịch trong",
                    color = Color(0xFF00E676)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Lời giải thích hiện tượng (KHÔNG tiết lộ phương trình)
        Text(
            text = "Bạn hãy quan sát và tự giải thích hiện tượng dựa trên kiến thức đã học.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFFFD54F),
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Nút hiện phương trình (chỉ khi học sinh đã quan sát)
        AnimatedVisibility(
            visible = !showEquation,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            OutlinedButton(
                onClick = onRevealEquation,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF80D8FF)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Xem giải thích & phương trình", fontSize = 12.sp)
            }
        }

        // Hiện phương trình khi được bật
        AnimatedVisibility(
            visible = showEquation,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                HorizontalDivider(color = Color(0xFF1E3A5F), modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    text = "Phương trình phản ứng:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFFD54F)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0A1524),
                    border = BorderStroke(1.dp, Color(0xFFFFD54F).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = result.equation,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFE082),
                        modifier = Modifier.padding(10.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result.explanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Cyan.copy(alpha = 0.85f),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun PhenomenonChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = color
            )
        }
    }
}

// ══════════════════════════════════════════════════════
// COMPOSE PREVIEWS
// ══════════════════════════════════════════════════════

@Preview(name = "Empty Test Tube", showBackground = true, backgroundColor = 0xFF070F1E)
@Composable
private fun PreviewEmptyTestTube() {
    MaterialTheme {
        TestTubeCanvas(
            testTubeState = TestTubeState(),
            temperature = 25f,
            isHeating = false,
            selectedChemicals = emptyList()
        )
    }
}

@Preview(name = "Test Tube with Blue Solution", showBackground = true, backgroundColor = 0xFF070F1E)
@Composable
private fun PreviewBlueSolution() {
    MaterialTheme {
        TestTubeCanvas(
            testTubeState = TestTubeState(
                liquidColor = 0xFF0288D1,
                liquidLevel = 0.55f
            ),
            temperature = 28f,
            isHeating = false,
            selectedChemicals = listOf(com.example.model.ChemicalCatalog.CuSO4)
        )
    }
}

@Preview(name = "Test Tube with Precipitate", showBackground = true, backgroundColor = 0xFF070F1E)
@Composable
private fun PreviewPrecipitate() {
    MaterialTheme {
        TestTubeCanvas(
            testTubeState = TestTubeState(
                liquidColor = 0xFFE0F7FA,
                hasPrecipitate = true,
                precipitateColor = 0xFFFFFFFF,
                precipitateFormula = "BaSO₄",
                liquidLevel = 0.6f
            ),
            temperature = 27f,
            isHeating = false,
            selectedChemicals = listOf(com.example.model.ChemicalCatalog.BaCl2)
        )
    }
}

@Preview(name = "Test Tube with Gas Bubbles", showBackground = true, backgroundColor = 0xFF070F1E)
@Composable
private fun PreviewGasBubbles() {
    MaterialTheme {
        TestTubeCanvas(
            testTubeState = TestTubeState(
                liquidColor = 0xFF81C784,
                hasGas = true,
                gasFormula = "H₂",
                gasRate = 0.75f,
                liquidLevel = 0.6f
            ),
            temperature = 33f,
            isHeating = false,
            selectedChemicals = listOf(com.example.model.ChemicalCatalog.Zn)
        )
    }
}

@Preview(name = "Test Tube Heating", showBackground = true, backgroundColor = 0xFF070F1E)
@Composable
private fun PreviewHeating() {
    MaterialTheme {
        TestTubeCanvas(
            testTubeState = TestTubeState(
                liquidColor = 0xFFE0F7FA,
                hasGas = true,
                gasFormula = "CO₂",
                gasRate = 0.6f,
                liquidLevel = 0.6f
            ),
            temperature = 65f,
            isHeating = true,
            selectedChemicals = listOf(com.example.model.ChemicalCatalog.CaCO3)
        )
    }
}

@Preview(name = "Chemical Shelf", showBackground = true, backgroundColor = 0xFF070F1E)
@Composable
private fun PreviewChemicalShelf() {
    MaterialTheme {
        ChemicalShelf(
            chemicals = VIRTUAL_LAB_CHEMICALS,
            selectedChemicals = listOf(
                com.example.model.ChemicalCatalog.Fe,
                com.example.model.ChemicalCatalog.HCl
            ),
            onChemicalSelected = {},
            onRemoveChemical = {},
            isGridLayout = false
        )
    }
}

@Preview(name = "Chemical Shelf Grid", showBackground = true, backgroundColor = 0xFF070F1E)
@Composable
private fun PreviewChemicalShelfGrid() {
    MaterialTheme {
        ChemicalShelf(
            chemicals = VIRTUAL_LAB_CHEMICALS,
            selectedChemicals = emptyList(),
            onChemicalSelected = {},
            onRemoveChemical = {},
            isGridLayout = true
        )
    }
}

@Preview(name = "Socratic Panel", showBackground = true, backgroundColor = 0xFF070F1E)
@Composable
private fun PreviewSocraticPanel() {
    MaterialTheme {
        SocraticPanel(
            uiState = com.example.ai.SocraticUiState.Idle,
            messages = listOf(
                com.example.ai.ChatMessage("assistant", "Xin chào! Hãy chọn hóa chất và quan sát hiện tượng nhé."),
                com.example.ai.ChatMessage("user", "Tại sao có bọt khí?"),
                com.example.ai.ChatMessage("assistant", "Câu hỏi hay! Em hãy nhớ lại dãy hoạt động hóa học: Kim loại nào đứng trước H có thể đẩy H⁺ ra khỏi axit?")
            ),
            onSendMessage = {},
            onClearHistory = {}
        )
    }
}

@Preview(name = "Full Compact Layout", showBackground = true, backgroundColor = 0xFF070F1E)
@Composable
private fun PreviewFullCompactLayout() {
    MaterialTheme {
        VirtualLabScreen(
            windowSizeClass = null,
            viewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = VirtualLabViewModelFactory())
        )
    }
}
