package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.engine.ChemicalEngine
import com.example.model.ExperimentTemplate
import com.example.model.ReactionType
import com.example.model.SubstanceCatalog

@Composable
fun CatalogScreen(
    onSelectExperiment: (ExperimentTemplate) -> Unit,
    isIupacMode: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "DANGEROUS", "METAL", "ION", "REDOX"

    val allExperiments = ChemicalEngine.CURRICULUM_EXPERIMENTS

    val filteredExperiments = remember(selectedFilter) {
        when (selectedFilter) {
            "DANGEROUS" -> allExperiments.filter { it.isDangerousOrExpensive }
            "METAL" -> allExperiments.filter { it.category == ReactionType.METAL_DISPLACEMENT }
            "ION" -> allExperiments.filter { it.category == ReactionType.ION_EXCHANGE }
            "REDOX" -> allExperiments.filter { it.category == ReactionType.REDOX }
            else -> allExperiments
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070F1E))
            .padding(14.dp)
    ) {
        // Title & Description
        Text(
            text = "NGÂN HÀNG THÍ NGHIỆM GDPT 2018",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00E5FF),
            letterSpacing = 0.5.sp
        )
        Text(
            text = "Bao gồm cả các thí nghiệm nguy hiểm/độc hại học sinh không được làm ở trường và thí nghiệm đối chứng",
            style = MaterialTheme.typography.bodySmall,
            color = Color.LightGray,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
        )

        // Filter chips row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Pair("ALL", "Tất cả (${allExperiments.size})"),
                Pair("DANGEROUS", "⚠️ Nguy hiểm / Độc hại"),
                Pair("METAL", "Dãy điện hóa"),
                Pair("ION", "Trao đổi ion"),
                Pair("REDOX", "Oxi hóa - Khử")
            ).forEach { (key, label) ->
                val isSelected = selectedFilter == key
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = key },
                    label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF00E5FF),
                        selectedLabelColor = Color(0xFF001F3F),
                        containerColor = Color(0xFF102138),
                        labelColor = Color.LightGray
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Experiment List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredExperiments) { exp ->
                val outcome = exp.expectedOutcome
                val reactantA = SubstanceCatalog.ALL_SUBSTANCES.find { it.id == exp.reactantAId }
                val reactantB = SubstanceCatalog.ALL_SUBSTANCES.find { it.id == exp.reactantBId }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0E1E34),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (exp.isDangerousOrExpensive) Color(0xFFE53935).copy(alpha = 0.6f) else Color(0xFF1A395E)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (exp.isDangerousOrExpensive) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFD32F2F)
                                    ) {
                                        Text(
                                            text = "NGUY HIỂM",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                }

                                Text(
                                    text = if (isIupacMode) exp.titleEn else exp.titleVi,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF193252)
                            ) {
                                Text(
                                    text = if (isIupacMode) exp.category.labelEn else exp.category.labelVi,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF80D8FF),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Chemical equation box
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF091424),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = outcome.balancedEquation,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFFFE082),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isIupacMode) exp.summaryEn else exp.summaryVi,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCFD8DC),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Telemetry attributes row (Delta H, Temperature)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (outcome.deltaH != 0f) {
                                    Text(
                                        text = "ΔrH°: ${outcome.deltaH} kJ/mol",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (outcome.deltaH < 0) Color(0xFFFF8A80) else Color(0xFF80D8FF),
                                        fontSize = 10.sp
                                    )
                                }
                                if (outcome.tempDelta != 0f) {
                                    Text(
                                        text = "ΔT: +${outcome.tempDelta}°C",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFFFD54F),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Button(
                                onClick = { onSelectExperiment(exp) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00E5FF),
                                    contentColor = Color(0xFF001F3F)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Vào Phòng Lab", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
