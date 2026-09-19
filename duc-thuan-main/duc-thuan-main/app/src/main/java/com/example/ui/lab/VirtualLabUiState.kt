package com.example.ui.lab

import com.example.ai.ChatMessage
import com.example.ai.SocraticUiState
import com.example.engine.ReactionTemplate
import com.example.model.Chemical

/**
 * Trạng thái UI của màn hình VirtualLabScreen.
 */
data class VirtualLabUiState(
    val labStep: LabStep = LabStep.SELECT_CHEMICALS,
    val selectedChemicals: List<Chemical> = emptyList(),
    val currentReaction: ReactionTemplate? = null,
    val reactionOccurred: Boolean = false,
    val showEquation: Boolean = false,
    val temperatureCelsius: Float = 25f,
    val isHeating: Boolean = false,
    val socraticUiState: SocraticUiState = SocraticUiState.Idle,
    val labErrorMessage: String? = null
)

/**
 * Các bước trong quy trình sư phạm của Virtual Lab.
 */
enum class LabStep(val label: String, val labelVi: String) {
    /** Học sinh chọn 2 chất từ kệ hóa chất */
    SELECT_CHEMICALS("Select Chemicals", "CHỌN HÓA CHẤT"),

    /** Học sinh dự đoán hiện tượng (giả thuyết) */
    PREDICT("Predict", "DỰ ĐOÁN"),

    /** Học sinh thao tác đổ chất và quan sát */
    OBSERVE("Observe", "QUAN SÁT"),

    /** Học sinh giải thích và xem kết quả */
    EXPLAIN("Explain", "GIẢI THÍCH")
}

/**
 * Trạng thái hiệu ứng ống nghiệm.
 */
data class TestTubeState(
    val liquidColor: Long = 0xFFE0F7FA,
    val hasPrecipitate: Boolean = false,
    val precipitateColor: Long = 0xFFFFFFFF,
    val precipitateFormula: String? = null,
    val hasGas: Boolean = false,
    val gasFormula: String? = null,
    val gasRate: Float = 0f,
    val gasColor: Long = 0xAAFFFFFF,
    val hasFlame: Boolean = false,
    val isDangerous: Boolean = false,
    val dangerWarning: String? = null,
    val liquidLevel: Float = 0f // 0.0..0.7 (tỷ lệ mực chất lỏng trong ống nghiệm)
)

/**
 * Sự kiện để ViewModel xử lý.
 */
sealed class VirtualLabEvent {
    data class SelectChemical(val chemical: Chemical) : VirtualLabEvent()
    data object RemoveLastChemical : VirtualLabEvent()
    data object ResetLab : VirtualLabEvent()
    data object ToggleHeating : VirtualLabEvent()
    data object TriggerReaction : VirtualLabEvent()
    data object RevealEquation : VirtualLabEvent()
    data class SendSocraticQuestion(val question: String) : VirtualLabEvent()
    data object AdvanceStep : VirtualLabEvent()
    data object GoBackStep : VirtualLabEvent()
}

/**
 * Danh sách các chất có sẵn trong Virtual Lab.
 */
val VIRTUAL_LAB_CHEMICALS = listOf(
    com.example.model.ChemicalCatalog.Fe,
    com.example.model.ChemicalCatalog.HCl,
    com.example.model.ChemicalCatalog.BaCl2,
    com.example.model.ChemicalCatalog.H2SO4,
    com.example.model.ChemicalCatalog.NaOH,
    com.example.model.ChemicalCatalog.CuSO4,
    com.example.model.ChemicalCatalog.Phenolphthalein,
    com.example.model.ChemicalCatalog.AgNO3,
    com.example.model.ChemicalCatalog.NaCl,
    com.example.model.ChemicalCatalog.CaCO3,
    com.example.model.ChemicalCatalog.Zn,
    com.example.model.ChemicalCatalog.Cu
)
