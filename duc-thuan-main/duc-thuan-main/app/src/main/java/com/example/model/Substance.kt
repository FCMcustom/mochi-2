package com.example.model

enum class PhysicalState(val labelVi: String, val labelEn: String) {
    SOLID("Chất rắn", "Solid"),
    LIQUID("Dung dịch/Lỏng", "Liquid/Aqueous"),
    GAS("Chất khí", "Gas")
}

enum class HazardLevel(val labelVi: String, val colorHex: Long) {
    SAFE("An toàn", 0xFF4CAF50),
    CAUTION("Cẩn trọng - Kích ứng", 0xFFFF9800),
    DANGEROUS("Nguy hiểm - Ăn mòn/Tỏa nhiệt", 0xFFFF5722),
    EXTREME_HAZARD("Cực kỳ nguy hiểm - Cháy nổ/Khí độc", 0xFFD32F2F)
}

data class Substance(
    val id: String,
    val formula: String,
    val nameVi: String,
    val nameIupac: String,
    val state: PhysicalState,
    val colorArgb: Long,
    val ph: Float,
    val reactivityRank: Int = 99, // Lower rank = higher reactivity in electrochemical series: K=1, Na=2, Ca=3, Mg=4, Al=5, Zn=6, Fe=7, H=11, Cu=12, Ag=15
    val hazardLevel: HazardLevel = HazardLevel.SAFE,
    val descriptionVi: String = "",
    val descriptionEn: String = "",
    val safetyWarningVi: String = "",
    val safetyWarningEn: String = ""
)

object SubstanceCatalog {
    val ZINC = Substance(
        id = "zn",
        formula = "Zn",
        nameVi = "Kẽm (Viên/Lá)",
        nameIupac = "Zinc",
        state = PhysicalState.SOLID,
        colorArgb = 0xFF9E9E9E,
        ph = 7.0f,
        reactivityRank = 6,
        hazardLevel = HazardLevel.SAFE,
        descriptionVi = "Kim loại màu xám ánh kim, đứng trước H trong dãy hoạt động.",
        descriptionEn = "Silvery-grey metal, positioned before H in reactivity series."
    )

    val HYDROCHLORIC_ACID = Substance(
        id = "hcl",
        formula = "HCl",
        nameVi = "Axit clohiđric (1M)",
        nameIupac = "Hydrochloric acid",
        state = PhysicalState.LIQUID,
        colorArgb = 0x15E0F7FA, // Clear transparent
        ph = 1.0f,
        reactivityRank = 11,
        hazardLevel = HazardLevel.DANGEROUS,
        descriptionVi = "Dung dịch axit mạnh, không màu, có tính ăn mòn.",
        descriptionEn = "Strong acid solution, clear and corrosive."
    )

    val COPPER = Substance(
        id = "cu",
        formula = "Cu",
        nameVi = "Đồng (Lá/Phoi)",
        nameIupac = "Copper",
        state = PhysicalState.SOLID,
        colorArgb = 0xFFB87333, // Copper red-orange
        ph = 7.0f,
        reactivityRank = 12,
        hazardLevel = HazardLevel.SAFE,
        descriptionVi = "Kim loại màu đỏ cam, đứng sau H trong dãy điện hóa.",
        descriptionEn = "Red-orange transition metal, less active than Hydrogen."
    )

    val NITRIC_ACID_CONC = Substance(
        id = "hno3_conc",
        formula = "HNO3 (đặc)",
        nameIupac = "Concentrated Nitric acid",
        nameVi = "Axit nitric đặc (68%)",
        state = PhysicalState.LIQUID,
        colorArgb = 0x22FFF59D,
        ph = 0.5f,
        hazardLevel = HazardLevel.EXTREME_HAZARD,
        descriptionVi = "Axit oxi hóa rất mạnh, phản ứng sinh khí NO2 nâu đỏ độc hại.",
        descriptionEn = "Powerful oxidizing acid, produces toxic brown NO2 gas.",
        safetyWarningVi = "THÍ NGHIỆM ĐỘC HẠI: Bắt buộc làm trong tủ hút hoặc mô phỏng ảo!",
        safetyWarningEn = "TOXIC HAZARD: Produce in fume hood or virtual lab simulation!"
    )

    val SODIUM = Substance(
        id = "na",
        formula = "Na",
        nameVi = "Natri (Kim loại kiềm)",
        nameIupac = "Sodium",
        state = PhysicalState.SOLID,
        colorArgb = 0xFFCFD8DC,
        ph = 7.0f,
        reactivityRank = 2,
        hazardLevel = HazardLevel.EXTREME_HAZARD,
        descriptionVi = "Kim loại kiềm rất mềm, phản ứng mãnh liệt với nước tỏa nhiệt bốc cháy.",
        descriptionEn = "Soft alkali metal, reacts vigorously with water with flame.",
        safetyWarningVi = "NGUY HIỂM NỔ/CHÁY: Chỉ được lấy một mẩu nhỏ bằng hạt đậu xanh!",
        safetyWarningEn = "EXPLOSION/FIRE RISK: In real life only tiny speck used!"
    )

    val WATER = Substance(
        id = "h2o",
        formula = "H2O",
        nameVi = "Nước cất",
        nameIupac = "Distilled Water",
        state = PhysicalState.LIQUID,
        colorArgb = 0x22B3E5FC,
        ph = 7.0f,
        hazardLevel = HazardLevel.SAFE,
        descriptionVi = "Dung môi phổ biến trong phòng thí nghiệm.",
        descriptionEn = "Common universal solvent in chemistry lab."
    )

    val BARIUM_CHLORIDE = Substance(
        id = "bacl2",
        formula = "BaCl2",
        nameVi = "Bari clorua (Dung dịch 0.5M)",
        nameIupac = "Barium chloride",
        state = PhysicalState.LIQUID,
        colorArgb = 0x15E0F2F1,
        ph = 6.8f,
        hazardLevel = HazardLevel.CAUTION,
        descriptionVi = "Dung dịch muối bari dùng nhận biết ion sunfat (SO4 2-).",
        descriptionEn = "Soluble barium salt used to identify sulfate ions."
    )

    val SULFURIC_ACID = Substance(
        id = "h2so4",
        formula = "H2SO4",
        nameVi = "Axit sunfuric (0.5M)",
        nameIupac = "Sulfuric acid",
        state = PhysicalState.LIQUID,
        colorArgb = 0x15E0F7FA,
        ph = 1.0f,
        hazardLevel = HazardLevel.DANGEROUS,
        descriptionVi = "Axit vô cơ mạnh chứa nhóm sunfat.",
        descriptionEn = "Strong mineral diprotic acid."
    )

    val IRON_NAIL = Substance(
        id = "fe",
        formula = "Fe",
        nameVi = "Đinh sắt (Fe sạch)",
        nameIupac = "Iron nail",
        state = PhysicalState.SOLID,
        colorArgb = 0xFF78909C,
        ph = 7.0f,
        reactivityRank = 7,
        hazardLevel = HazardLevel.SAFE,
        descriptionVi = "Kim loại chuyển tiếp có tính khử trung bình.",
        descriptionEn = "Transition metal with moderate reducing strength."
    )

    val COPPER_SULFATE = Substance(
        id = "cuso4",
        formula = "CuSO4",
        nameVi = "Đồng(II) sunfat (Dung dịch)",
        nameIupac = "Copper(II) sulfate",
        state = PhysicalState.LIQUID,
        colorArgb = 0xAA0288D1, // Deep bright blue
        ph = 5.5f,
        hazardLevel = HazardLevel.CAUTION,
        descriptionVi = "Dung dịch có màu xanh lam đặc trưng của ion hydrated Cu2+.",
        descriptionEn = "Vibrant blue aqueous solution due to hydrated Cu2+ ions."
    )

    val SILVER_NITRATE = Substance(
        id = "agno3",
        formula = "AgNO3",
        nameVi = "Bạc nitrat (0.1M)",
        nameIupac = "Silver nitrate",
        state = PhysicalState.LIQUID,
        colorArgb = 0x15EDE7F6,
        ph = 6.5f,
        hazardLevel = HazardLevel.CAUTION,
        descriptionVi = "Dung dịch thuốc thử phát hiện ion halogenua (Cl-).",
        descriptionEn = "Precipitation reagent for halide detection."
    )

    val SODIUM_CHLORIDE = Substance(
        id = "nacl",
        formula = "NaCl",
        nameVi = "Natri clorua (Muối ăn)",
        nameIupac = "Sodium chloride",
        state = PhysicalState.LIQUID,
        colorArgb = 0x15E0F7FA,
        ph = 7.0f,
        hazardLevel = HazardLevel.SAFE,
        descriptionVi = "Muối trung tính phổ biến.",
        descriptionEn = "Common neutral aqueous salt solution."
    )

    val SODIUM_HYDROXIDE = Substance(
        id = "naoh",
        formula = "NaOH",
        nameVi = "Natri hiđroxit (Bazơ)",
        nameIupac = "Sodium hydroxide",
        state = PhysicalState.LIQUID,
        colorArgb = 0x15E0F7FA,
        ph = 13.0f,
        hazardLevel = HazardLevel.DANGEROUS,
        descriptionVi = "Dung dịch kiềm mạnh, làm xanh quỳ tím và hồng phenolphthalein.",
        descriptionEn = "Strong caustic alkaline base solution."
    )

    val POTASSIUM_PERMANGANATE = Substance(
        id = "kmno4",
        formula = "KMnO4",
        nameVi = "Kali pemanganat (Thuốc tím)",
        nameIupac = "Potassium permanganate",
        state = PhysicalState.SOLID,
        colorArgb = 0xFF4A148C, // Dark purple
        ph = 7.0f,
        hazardLevel = HazardLevel.CAUTION,
        descriptionVi = "Chất oxi hóa mạnh có màu tím đậm đặc trưng.",
        descriptionEn = "Strong oxidizing agent with deep purple color."
    )

    val CALCIUM_CARBONATE = Substance(
        id = "caco3",
        formula = "CaCO3",
        nameVi = "Đá vôi (Canxi cacbonat)",
        nameIupac = "Calcium carbonate",
        state = PhysicalState.SOLID,
        colorArgb = 0xFFECEFF1,
        ph = 8.5f,
        hazardLevel = HazardLevel.SAFE,
        descriptionVi = "Muối không tan trong nước nhưng tan trong axit giải phóng khí CO2.",
        descriptionEn = "Insoluble carbonate that reacts with acid to liberate CO2."
    )

    val ALL_SUBSTANCES = listOf(
        ZINC,
        HYDROCHLORIC_ACID,
        COPPER,
        NITRIC_ACID_CONC,
        SODIUM,
        WATER,
        BARIUM_CHLORIDE,
        SULFURIC_ACID,
        IRON_NAIL,
        COPPER_SULFATE,
        SILVER_NITRATE,
        SODIUM_CHLORIDE,
        SODIUM_HYDROXIDE,
        POTASSIUM_PERMANGANATE,
        CALCIUM_CARBONATE
    )
}
