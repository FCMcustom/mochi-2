package com.example.model

data class ChemicalCompetency(
    val id: String,
    val nameVi: String,
    val nameEn: String,
    val descriptionVi: String,
    val descriptionEn: String,
    val pInit: Float = 0.25f, // P(L0)
    val pLearn: Float = 0.18f, // P(T)
    val pGuess: Float = 0.20f, // P(G)
    val pSlip: Float = 0.10f   // P(S)
)

data class CompetencyMastery(
    val competency: ChemicalCompetency,
    val currentProbability: Float, // P(Lt) 0.0 to 1.0
    val totalAttempts: Int,
    val correctCount: Int
)

object CompetencyRegistry {
    val METAL_SERIES = ChemicalCompetency(
        id = "metal_series",
        nameVi = "Dãy hoạt động hóa học của kim loại",
        nameEn = "Electrochemical Metal Activity Series",
        descriptionVi = "Khả năng dự đoán kim loại nào phản ứng với axit hoặc đẩy kim loại khác ra khỏi dung dịch muối.",
        descriptionEn = "Predicting metal reactions with acids and salt solutions based on reactivity hierarchy."
    )

    val ION_EXCHANGE = ChemicalCompetency(
        id = "ion_exchange",
        nameVi = "Phản ứng trao đổi ion trong dung dịch",
        nameEn = "Ion Exchange & Precipitation Equilibrium",
        descriptionVi = "Điều kiện tạo thành chất kết tủa, chất khí hoặc chất điện li yếu theo SGK 2018.",
        descriptionEn = "Conditions for precipitation, gas release or weak electrolyte formation in solution."
    )

    val REDOX_THERMO = ChemicalCompetency(
        id = "redox_thermo",
        nameVi = "Oxi hóa - Khử & Biến thiên Enthalpy (ΔrH°)",
        nameEn = "Redox & Enthalpy of Reaction (ΔrH°)",
        descriptionVi = "Quá trình nhường - nhận electron và hiệu ứng nhiệt (tỏa nhiệt/thu nhiệt) trong phản ứng hóa học.",
        descriptionEn = "Electron transfer mechanism and reaction thermal energy effects according to GDPT 2018."
    )

    val LAB_SAFETY = ChemicalCompetency(
        id = "lab_safety",
        nameVi = "Quy tắc an toàn & Thao tác sư phạm",
        nameEn = "Lab Safety & Standard Protocol",
        descriptionVi = "Ý thức bảo hộ, xử lý hóa chất độc hại, phòng tránh cháy nổ khi thao tác thực nghiệm.",
        descriptionEn = "Safety awareness, hazardous reagent handling and explosion prevention."
    )

    val ALL_COMPETENCIES = listOf(METAL_SERIES, ION_EXCHANGE, REDOX_THERMO, LAB_SAFETY)
}
