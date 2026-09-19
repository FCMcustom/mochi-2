package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppNavigationTab
import com.example.ui.MainViewModel
import com.example.ui.components.SocraticChatDialog
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.KhktResearchScreen
import com.example.ui.screens.LabScreen
import com.example.ui.screens.PersonalizedDashboardScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme(darkTheme = true) {
                val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
                val reactantA by viewModel.reactantA.collectAsStateWithLifecycle()
                val reactantB by viewModel.reactantB.collectAsStateWithLifecycle()
                val isIupacMode by viewModel.isIupacMode.collectAsStateWithLifecycle()
                val showSocraticChat by viewModel.showSocraticChat.collectAsStateWithLifecycle()
                val masteries by viewModel.masteries.collectAsStateWithLifecycle()
                val experimentHistory by viewModel.experimentHistory.collectAsStateWithLifecycle()
                val researchStudents by viewModel.researchStudents.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFF070F1E),
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(0xFF0A182D),
                                titleContentColor = Color.White
                            ),
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF00E5FF).copy(alpha = 0.2f),
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Science,
                                            contentDescription = "App Icon",
                                            tint = Color(0xFF00E5FF),
                                            modifier = Modifier.padding(6.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Smart ChemLab",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "Phòng Lab Hóa học Thông minh GDPT 2018",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF80D8FF),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            },
                            actions = {
                                // IUPAC nomenclature switch chip
                                FilterChip(
                                    selected = isIupacMode,
                                    onClick = { viewModel.toggleIupacMode() },
                                    label = {
                                        Text(
                                            text = if (isIupacMode) "IUPAC" else "Tên SGK 2018",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF00E5FF),
                                        selectedLabelColor = Color(0xFF001F3F),
                                        containerColor = Color(0xFF142740),
                                        labelColor = Color.LightGray
                                    ),
                                    modifier = Modifier.padding(end = 6.dp)
                                )

                                // Socratic Assistant Quick Trigger
                                IconButton(onClick = { viewModel.openSocraticChat() }) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "Trợ lý Socratic",
                                        tint = Color(0xFF00E5FF)
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color(0xFF0A182D),
                            contentColor = Color.White,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = currentTab == AppNavigationTab.LAB,
                                onClick = { viewModel.selectTab(AppNavigationTab.LAB) },
                                icon = { Icon(Icons.Default.Science, contentDescription = "Phòng Lab") },
                                label = { Text("Phòng Lab", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF001F3F),
                                    selectedTextColor = Color(0xFF00E5FF),
                                    indicatorColor = Color(0xFF00E5FF),
                                    unselectedIconColor = Color.LightGray,
                                    unselectedTextColor = Color.LightGray
                                )
                            )

                            NavigationBarItem(
                                selected = currentTab == AppNavigationTab.CATALOG,
                                onClick = { viewModel.selectTab(AppNavigationTab.CATALOG) },
                                icon = { Icon(Icons.Default.ListAlt, contentDescription = "Ngân hàng TN") },
                                label = { Text("Ngân hàng TN", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF001F3F),
                                    selectedTextColor = Color(0xFF00E5FF),
                                    indicatorColor = Color(0xFF00E5FF),
                                    unselectedIconColor = Color.LightGray,
                                    unselectedTextColor = Color.LightGray
                                )
                            )

                            NavigationBarItem(
                                selected = currentTab == AppNavigationTab.BKT,
                                onClick = { viewModel.selectTab(AppNavigationTab.BKT) },
                                icon = { Icon(Icons.Default.Insights, contentDescription = "Cá nhân hóa") },
                                label = { Text("Cá nhân hóa", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF001F3F),
                                    selectedTextColor = Color(0xFF00E5FF),
                                    indicatorColor = Color(0xFF00E5FF),
                                    unselectedIconColor = Color.LightGray,
                                    unselectedTextColor = Color.LightGray
                                )
                            )

                            NavigationBarItem(
                                selected = currentTab == AppNavigationTab.KHKT,
                                onClick = { viewModel.selectTab(AppNavigationTab.KHKT) },
                                icon = { Icon(Icons.Default.Analytics, contentDescription = "Nghiên cứu KHKT") },
                                label = { Text("KHKT (A/B)", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF001F3F),
                                    selectedTextColor = Color(0xFF00E5FF),
                                    indicatorColor = Color(0xFF00E5FF),
                                    unselectedIconColor = Color.LightGray,
                                    unselectedTextColor = Color.LightGray
                                )
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentTab) {
                            AppNavigationTab.LAB -> {
                                LabScreen(
                                    currentSubstanceA = reactantA,
                                    currentSubstanceB = reactantB,
                                    onSelectSubstanceA = { viewModel.setReactantA(it) },
                                    onSelectSubstanceB = { viewModel.setReactantB(it) },
                                    onRecordExperiment = { title, ra, rb, pred, phen, conf, expl, comp ->
                                        viewModel.recordExperiment(title, ra, rb, pred, phen, conf, expl, comp)
                                    },
                                    onOpenSocraticChat = { viewModel.openSocraticChat() },
                                    isIupacMode = isIupacMode
                                )
                            }
                            AppNavigationTab.CATALOG -> {
                                CatalogScreen(
                                    onSelectExperiment = { template ->
                                        viewModel.loadExperimentTemplate(template)
                                    },
                                    isIupacMode = isIupacMode
                                )
                            }
                            AppNavigationTab.BKT -> {
                                PersonalizedDashboardScreen(
                                    masteries = masteries,
                                    experimentHistory = experimentHistory,
                                    onAnswerQuiz = { compId, isCorrect, currentProb, att, corr ->
                                        viewModel.updateSkillEvaluation(compId, isCorrect, currentProb, att, corr)
                                    }
                                )
                            }
                            AppNavigationTab.KHKT -> {
                                KhktResearchScreen(students = researchStudents)
                            }
                        }
                    }
                }

                // Socratic Assistant Modal Dialog
                if (showSocraticChat) {
                    val aName = reactantA?.formula ?: ""
                    val bName = reactantB?.formula ?: ""
                    val contextStr = "Thí nghiệm: $aName + $bName"
                    SocraticChatDialog(
                        experimentContext = contextStr,
                        studentHypothesis = "",
                        onDismiss = { viewModel.closeSocraticChat() },
                        onSendMessage = { query -> viewModel.querySocraticAssistant(query) }
                    )
                }
            }
        }
    }
}
