package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.SocraticAssistant
import com.example.data.ExperimentRecord
import com.example.data.LabDatabase
import com.example.data.LabRepository
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavigationTab(val labelVi: String, val labelEn: String) {
    LAB("Phòng Lab", "Lab"),
    CATALOG("Ngân hàng TN", "Catalog"),
    BKT("Cá nhân hóa", "Adaptive BKT"),
    KHKT("Nghiên cứu KHKT", "Research")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LabRepository

    val masteries: StateFlow<List<CompetencyMastery>>
    val experimentHistory: StateFlow<List<ExperimentRecord>>
    val researchStudents: StateFlow<List<StudentSample>>

    private val _currentTab = MutableStateFlow(AppNavigationTab.LAB)
    val currentTab: StateFlow<AppNavigationTab> = _currentTab.asStateFlow()

    private val _reactantA = MutableStateFlow<Substance?>(SubstanceCatalog.ZINC)
    val reactantA: StateFlow<Substance?> = _reactantA.asStateFlow()

    private val _reactantB = MutableStateFlow<Substance?>(SubstanceCatalog.HYDROCHLORIC_ACID)
    val reactantB: StateFlow<Substance?> = _reactantB.asStateFlow()

    private val _isIupacMode = MutableStateFlow(true)
    val isIupacMode: StateFlow<Boolean> = _isIupacMode.asStateFlow()

    private val _showSocraticChat = MutableStateFlow(false)
    val showSocraticChat: StateFlow<Boolean> = _showSocraticChat.asStateFlow()

    init {
        val database = LabDatabase.getInstance(application)
        repository = LabRepository(database.labDao())

        masteries = repository.bktMasteries.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        experimentHistory = repository.experimentRecords.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        researchStudents = repository.researchStudents.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    fun selectTab(tab: AppNavigationTab) {
        _currentTab.value = tab
    }

    fun setReactantA(substance: Substance?) {
        _reactantA.value = substance
    }

    fun setReactantB(substance: Substance?) {
        _reactantB.value = substance
    }

    fun toggleIupacMode() {
        _isIupacMode.value = !_isIupacMode.value
    }

    fun openSocraticChat() {
        _showSocraticChat.value = true
    }

    fun closeSocraticChat() {
        _showSocraticChat.value = false
    }

    fun loadExperimentTemplate(template: ExperimentTemplate) {
        val a = SubstanceCatalog.ALL_SUBSTANCES.find { it.id == template.reactantAId }
        val b = SubstanceCatalog.ALL_SUBSTANCES.find { it.id == template.reactantBId }
        _reactantA.value = a
        _reactantB.value = b
        _currentTab.value = AppNavigationTab.LAB
    }

    fun recordExperiment(
        title: String,
        reactantA: String,
        reactantB: String,
        prediction: String,
        phenomena: String,
        isConfirmed: Boolean,
        explanation: String,
        competencyId: String
    ) {
        viewModelScope.launch {
            repository.recordExperiment(
                title = title,
                reactantA = reactantA,
                reactantB = reactantB,
                prediction = prediction,
                phenomena = phenomena,
                isConfirmed = isConfirmed,
                explanation = explanation,
                competencyId = competencyId
            )
        }
    }

    fun updateSkillEvaluation(
        competencyId: String,
        isCorrect: Boolean,
        currentProb: Float,
        attempts: Int,
        correct: Int
    ) {
        viewModelScope.launch {
            repository.updateSkillEvaluation(
                competencyId = competencyId,
                isCorrect = isCorrect,
                currentProb = currentProb,
                attempts = attempts,
                correct = correct
            )
        }
    }

    suspend fun querySocraticAssistant(userMsg: String): String {
        val a = _reactantA.value?.formula ?: "chất A"
        val b = _reactantB.value?.formula ?: "chất B"
        val context = "Phản ứng giữa $a và $b trong phòng lab ảo Smart ChemLab GDPT 2018."
        return SocraticAssistant.consultAssistant(userMsg, context)
    }
}
