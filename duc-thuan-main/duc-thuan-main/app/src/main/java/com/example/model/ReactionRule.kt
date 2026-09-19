package com.example.model

enum class ReactionType(val labelVi: String, val labelEn: String) {
    METAL_DISPLACEMENT("Thế kim loại", "Single Metal Displacement"),
    ION_EXCHANGE("Trao đổi ion & Kết tủa", "Ion Exchange & Precipitation"),
    ACID_BASE("Axit - Bazơ (Trung hòa)", "Acid-Base Neutralization"),
    REDOX("Oxi hóa - Khử & Khí độc", "Redox & Gas Generation"),
    NO_REACTION("Không xảy ra phản ứng", "No Reaction Occurred")
}

data class ReactionOutcome(
    val reactionType: ReactionType,
    val balancedEquation: String,
    val ionicEquation: String,
    val netIonicEquation: String,
    val deltaH: Float, // Standard enthalpy kJ/mol (negative = exothermic)
    val tempDelta: Float, // Temperature change for lab thermometer
    val solutionFinalColor: Long,
    val gasFormula: String? = null,
    val gasRate: Float = 0f, // 0..1 bubble intensity
    val gasColor: Long = 0x00000000,
    val precipitateFormula: String? = null,
    val precipitateColor: Long? = null,
    val hasFlame: Boolean = false,
    val isDangerous: Boolean = false,
    val dangerWarningVi: String = "",
    val phenomenaVi: String,
    val phenomenaEn: String,
    val microExplanationVi: String,
    val microExplanationEn: String,
    val competencyId: String // e.g. "metal_series", "ion_exchange", "redox_energy", "lab_safety"
)

data class ExperimentTemplate(
    val id: String,
    val titleVi: String,
    val titleEn: String,
    val reactantAId: String,
    val reactantBId: String,
    val category: ReactionType,
    val isCurriculum2018Highlight: Boolean = true,
    val isDangerousOrExpensive: Boolean = false,
    val summaryVi: String,
    val summaryEn: String,
    val expectedOutcome: ReactionOutcome
)
