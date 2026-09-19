package com.example.ui.lab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ai.ChatMessage
import com.example.ai.SocraticUiState
import com.example.ai.SocraticViewModel
import com.example.data.ExperimentSession
import com.example.data.MistakeType
import com.example.data.ResearchRepository
import com.example.data.StudentMistakeLog
import com.example.model.Chemical
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel quản lý trạng thái của VirtualLabScreen.
 * Tích hợp ChemicalEngine (SimulationEngine), SocraticViewModel, và ResearchRepository (Room).
 *
 * Luồng ghi nhận phiên thí nghiệm:
 * 1. Khi trigger phản ứng → tạo session mới với startNewSession()
 * 2. Khi học sinh gửi câu hỏi → tăng socraticPromptCount
 * 3. Khi xem phương trình → đánh dấu viewedExplanationDirectly = true
 * 4. Khi reset/chuyển bài → tự động gọi finishSession() ghi xuống Room
 */
class VirtualLabViewModel(
    private val socraticViewModel: SocraticViewModel = SocraticViewModel(),
    private val researchRepository: ResearchRepository? = null
) : ViewModel() {

    // ─── Trạng thái UI chính ──────────────────────────────
    private val _uiState = MutableStateFlow(VirtualLabUiState())
    val uiState: StateFlow<VirtualLabUiState> = _uiState.asStateFlow()

    // ─── Trạng thái hiệu ứng ống nghiệm ───────────────────
    private val _testTubeState = MutableStateFlow(TestTubeState())
    val testTubeState: StateFlow<TestTubeState> = _testTubeState.asStateFlow()

    // ─── Proxy từ SocraticViewModel ─────────────────────────
    val socraticUiState: StateFlow<SocraticUiState> = socraticViewModel.uiState
    val socraticMessages: StateFlow<List<ChatMessage>> = socraticViewModel.messages

    // ─── Nhiệt độ mục tiêu ────────────────────────────────
    private val _targetTemperature = MutableStateFlow(25f)
    val targetTemperature: StateFlow<Float> = _targetTemperature.asStateFlow()

    // ─── Theo dõi phiên thí nghiệm hiện tại ──────────────
    private var currentSessionId: Long? = null
    private var sessionStartTime: Long = 0L
    private var socraticPromptCountInSession: Int = 0
    private var viewedEquationDirectly: Boolean = false
    private var currentReactionEquation: String? = null

    /**
     * Xử lý sự kiện từ UI.
     */
    fun onEvent(event: VirtualLabEvent) {
        when (event) {
            is VirtualLabEvent.SelectChemical -> selectChemical(event.chemical)
            is VirtualLabEvent.RemoveLastChemical -> removeLastChemical()
            is VirtualLabEvent.ResetLab -> resetLab()
            is VirtualLabEvent.ToggleHeating -> toggleHeating()
            is VirtualLabEvent.TriggerReaction -> triggerReaction()
            is VirtualLabEvent.RevealEquation -> revealEquation()
            is VirtualLabEvent.SendSocraticQuestion -> sendSocraticQuestion(event.question)
            is VirtualLabEvent.AdvanceStep -> advanceStep()
            is VirtualLabEvent.GoBackStep -> goBackStep()
        }
    }

    /**
     * Chọn một chất hóa học và thêm vào ống nghiệm.
     */
    private fun selectChemical(chemical: Chemical) {
        val current = _uiState.value.selectedChemicals

        if (current.any { it.id == chemical.id }) return

        if (current.size >= 3) {
            _uiState.update { it.copy(labErrorMessage = "Tối đa 3 chất trong ống nghiệm") }
            return
        }

        val newList = current + chemical
        _uiState.update {
            it.copy(selectedChemicals = newList, labErrorMessage = null)
        }

        updateLiquidLevel(newList.size)

        if (newList.size == 2) {
            _uiState.update { it.copy(labStep = LabStep.PREDICT) }
        }
    }

    /**
     * Bỏ chất cuối cùng khỏi ống nghiệm.
     */
    private fun removeLastChemical() {
        val current = _uiState.value.selectedChemicals
        if (current.isEmpty()) return

        // Nếu đang có phản ứng → ghi session trước khi bỏ
        if (_uiState.value.reactionOccurred) {
            finishCurrentSession(isReactionOccurred = true)
        }

        val newList = current.dropLast(1)
        _uiState.update {
            it.copy(selectedChemicals = newList, reactionOccurred = false, currentReaction = null, showEquation = false)
        }

        updateLiquidLevel(newList.size)
        _testTubeState.update { TestTubeState(liquidLevel = calculateLiquidLevel(newList.size)) }

        if (newList.size < 2) {
            _uiState.update { it.copy(labStep = LabStep.SELECT_CHEMICALS) }
        }
    }

    /**
     * Kích hoạt phản ứng hóa học.
     */
    private fun triggerReaction() {
        val chemicals = _uiState.value.selectedChemicals
        if (chemicals.size < 2) return

        val result = com.example.engine.ChemicalEngine.mix(chemicals[0], chemicals[1])

        // Bắt đầu phiên thí nghiệm mới
        startNewSession(chemicals[0], chemicals[1])

        _uiState.update {
            it.copy(reactionOccurred = true, labStep = LabStep.OBSERVE)
        }

        _testTubeState.update {
            TestTubeState(
                liquidColor = result.colorChange,
                hasPrecipitate = result.precipitate != null,
                precipitateColor = result.precipitateColor ?: 0xFFFFFFFF,
                precipitateFormula = result.precipitate,
                hasGas = result.gasReleased != null,
                gasFormula = result.gasReleased,
                gasRate = if (result.gasReleased != null) 0.75f else 0f,
                gasColor = when (result.gasReleased) {
                    "H₂", "CO₂" -> 0xAAFFFFFF
                    "Cl₂" -> 0xAAFFEB3B
                    "NO₂" -> 0xAA8D6E63
                    else -> 0xAAFFFFFF
                },
                liquidLevel = calculateLiquidLevel(chemicals.size)
            )
        }

        _targetTemperature.value = 25f + result.temperatureChange
        _uiState.update { it.copy(temperatureCelsius = _targetTemperature.value) }

        // Tự động gửi sự kiện Socratic gợi mở
        autoSendSocraticPrompt(chemicals[0], chemicals[1], result)
    }

    /**
     * Bắt đầu một phiên thí nghiệm mới và ghi vào Room.
     */
    private fun startNewSession(chemA: Chemical, chemB: Chemical) {
        sessionStartTime = System.currentTimeMillis()
        socraticPromptCountInSession = 0
        viewedEquationDirectly = false
        currentReactionEquation = null

        if (researchRepository != null) {
            val chemicalsMixed = "${chemA.formula}+${chemB.formula}"
            val reactionType = classifyReaction(chemA, chemB)
            val competencyId = guessCompetency(chemA, chemB)

            viewModelScope.launch {
                currentSessionId = researchRepository.startNewSession(
                    chemicalsMixed = chemicalsMixed,
                    reactionType = reactionType,
                    competencyId = competencyId
                )
            }
        }
    }

    /**
     * Kết thúc phiên thí nghiệm hiện tại và ghi vào Room.
     */
    private fun finishCurrentSession(isReactionOccurred: Boolean) {
        val repo = researchRepository ?: return
        val sessionId = currentSessionId ?: return

        val endTime = System.currentTimeMillis()
        val duration = ((endTime - sessionStartTime) / 1000).toInt()

        val chemicals = _uiState.value.selectedChemicals
        val chemicalsMixed = chemicals.joinToString("+") { it.formula }
        val reactionType = classifyReaction(chemicals.getOrNull(0), chemicals.getOrNull(1))
        val competencyId = guessCompetency(chemicals.getOrNull(0), chemicals.getOrNull(1))
        val phenomenon = buildString {
            val result = if (isReactionOccurred) {
                com.example.engine.ChemicalEngine.mix(chemicals[0], chemicals[1])
            } else null
            if (result?.gasReleased != null) append("${result.gasReleased} ")
            if (result?.precipitate != null) append("kết tủa ${result.precipitate} ")
            if (result?.temperatureChange ?: 0f > 2f) append("tỏa nhiệt")
        }.ifEmpty { "không có hiện tượng" }

        viewModelScope.launch {
            repo.saveSession(
                ExperimentSession(
                    id = sessionId,
                    startTime = sessionStartTime,
                    endTime = endTime,
                    durationSeconds = duration.coerceAtLeast(1),
                    chemicalsMixed = chemicalsMixed,
                    reactionEquation = currentReactionEquation,
                    isReactionOccurred = isReactionOccurred,
                    socraticPromptCount = socraticPromptCountInSession,
                    viewedExplanationDirectly = viewedEquationDirectly,
                    reactionType = reactionType,
                    competencyId = competencyId,
                    observedPhenomena = phenomenon
                )
            )
        }

        // Reset tracking
        currentSessionId = null
        socraticPromptCountInSession = 0
        viewedEquationDirectly = false
        currentReactionEquation = null
    }

    /**
     * Phân loại phản ứng theo loại.
     */
    private fun classifyReaction(chemA: Chemical?, chemB: Chemical?): String {
        if (chemA == null || chemB == null) return "UNKNOWN"

        val hasAcid = listOf(chemA, chemB).any {
            it.pH < 4f && it.state == com.example.model.ChemicalState.AQUEOUS
        }
        val hasBase = listOf(chemA, chemB).any {
            it.pH > 10f && it.state == com.example.model.ChemicalState.AQUEOUS
        }
        val hasSolidMetal = listOf(chemA, chemB).any {
            it.state == com.example.model.ChemicalState.SOLID && it.concentration == null
        }

        // Axit + Bazơ
        if (hasAcid && hasBase) return "ACID_BASE"

        // Kim loại + Axit
        if (hasSolidMetal && hasAcid) return "METAL_DISPLACEMENT"

        // Trao đổi ion (muối)
        if (!hasSolidMetal && !hasBase && !hasAcid) return "ION_EXCHANGE"

        return "UNKNOWN"
    }

    /**
     * Đoán năng lực GDPT liên quan.
     */
    private fun guessCompetency(chemA: Chemical?, chemB: Chemical?): String {
        if (chemA == null || chemB == null) return "general"

        val hasAcid = listOf(chemA, chemB).any { it.pH < 4f }
        val hasBase = listOf(chemA, chemB).any { it.pH > 10f }

        return when {
            hasAcid && hasBase -> "acid_base"
            !hasAcid && !hasBase -> "ion_exchange"
            else -> "metal_series"
        }
    }

    private fun updateLiquidLevel(count: Int) {
        val level = calculateLiquidLevel(count)
        _testTubeState.update { it.copy(liquidLevel = level) }
    }

    private fun calculateLiquidLevel(count: Int): Float {
        return when (count) {
            0 -> 0f
            1 -> 0.35f
            2 -> 0.60f
            else -> 0.70f
        }
    }

    private fun toggleHeating() {
        val current = _uiState.value.isHeating
        _uiState.update {
            it.copy(
                isHeating = !current,
                temperatureCelsius = if (!current) it.temperatureCelsius + 40f else it.temperatureCelsius - 40f
            )
        }
        _targetTemperature.update { if (!current) it + 40f else it - 40f }
    }

    private fun revealEquation() {
        viewedEquationDirectly = true

        // Ghi lại kết quả phản ứng vào session
        val chemicals = _uiState.value.selectedChemicals
        if (chemicals.size >= 2) {
            val result = com.example.engine.ChemicalEngine.mix(chemicals[0], chemicals[1])
            currentReactionEquation = result.equation
        }

        _uiState.update { it.copy(showEquation = true, labStep = LabStep.EXPLAIN) }
    }

    private fun autoSendSocraticPrompt(chemA: Chemical, chemB: Chemical, result: com.example.model.ReactionResult) {
        val phenomenon = buildString {
            if (result.gasReleased != null) append("có bọt khí ($result.gasReleased) thoát ra. ")
            if (result.precipitate != null) append("có kết tủa ($result.precipitate) lắng xuống. ")
            if (result.temperatureChange > 2f) append("ống nghiệm nóng lên rõ rệt. ")
        }.ifEmpty { "không có thay đổi rõ rệt." }

        val socraticPrompt = buildString {
            append("Học sinh vừa trộn ${chemA.name} (${chemA.formula}) với ${chemB.name} (${chemB.formula}). ")
            append("Hiện tượng quan sát được: $phenomenon ")
            append("Hãy đặt một câu hỏi gợi mở ngắn gọn (dưới 50 từ) hướng dẫn học sinh giải thích bản chất. ")
            append("KHÔNG tiết lộ kết quả phản ứng hoàn chỉnh!")
        }

        socraticViewModel.sendQuestionSimple(socraticPrompt)
    }

    private fun sendSocraticQuestion(question: String) {
        // Tăng số câu hỏi trong phiên
        socraticPromptCountInSession++

        val chemicals = _uiState.value.selectedChemicals
        val labState = if (chemicals.size >= 2) {
            "${chemicals[0].name} (${chemicals[0].formula}) + ${chemicals[1].name} (${chemicals[1].formula})"
        } else {
            "Chưa chọn hóa chất"
        }
        socraticViewModel.updateLabState(labState)
        socraticViewModel.sendQuestion(question)
    }

    private fun advanceStep() {
        val current = _uiState.value.labStep
        val next = when (current) {
            LabStep.SELECT_CHEMICALS -> LabStep.PREDICT
            LabStep.PREDICT -> LabStep.OBSERVE
            LabStep.OBSERVE -> LabStep.EXPLAIN
            LabStep.EXPLAIN -> LabStep.EXPLAIN
        }
        _uiState.update { it.copy(labStep = next) }
    }

    private fun goBackStep() {
        val current = _uiState.value.labStep
        val prev = when (current) {
            LabStep.SELECT_CHEMICALS -> LabStep.SELECT_CHEMICALS
            LabStep.PREDICT -> LabStep.SELECT_CHEMICALS
            LabStep.OBSERVE -> LabStep.PREDICT
            LabStep.EXPLAIN -> LabStep.OBSERVE
        }
        _uiState.update { it.copy(labStep = prev) }
    }

    /**
     * Reset toàn bộ phòng thí nghiệm ảo và ghi phiên vào Room.
     */
    private fun resetLab() {
        // Ghi phiên hiện tại (nếu có phản ứng)
        if (_uiState.value.reactionOccurred) {
            finishCurrentSession(isReactionOccurred = true)
        } else if (currentSessionId != null) {
            // Phiên không có phản ứng
            finishCurrentSession(isReactionOccurred = false)
        }

        _uiState.update { VirtualLabUiState() }
        _testTubeState.update { TestTubeState() }
        _targetTemperature.value = 25f
        socraticViewModel.clearHistory()
    }

    override fun onCleared() {
        super.onCleared()
        socraticViewModel.cancelStreaming()
        // Ghi phiên cuối cùng nếu chưa kết thúc
        if (currentSessionId != null) {
            finishCurrentSession(isReactionOccurred = _uiState.value.reactionOccurred)
        }
    }
}

/**
 * Factory để tạo VirtualLabViewModel với dependency injection đơn giản.
 */
class VirtualLabViewModelFactory(
    private val researchRepository: ResearchRepository? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VirtualLabViewModel::class.java)) {
            return VirtualLabViewModel(
                socraticViewModel = SocraticViewModel(),
                researchRepository = researchRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
