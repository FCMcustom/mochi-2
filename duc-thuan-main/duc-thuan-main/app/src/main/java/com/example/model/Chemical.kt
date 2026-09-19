package com.example.model

/**
 * Trạng thái vật lý của chất hóa học.
 */
enum class ChemicalState(val label: String) {
    SOLID("Rắn"),
    LIQUID("Lỏng"),
    AQUEOUS("Dung dịch"),
    GAS("Khí")
}

/**
 * Data class mô tả một chất hóa học cơ bản.
 * Thuộc về chương trình Hóa học lớp 10/11 GDPT 2018.
 *
 * @property id Mã định danh duy nhất của chất
 * @property name Tên chất (Tiếng Việt)
 * @property formula Công thức hóa học
 * @property state Trạng thái vật lý của chất
 * @property color Màu sắc (ARGB hex, ví dụ: 0xFFFF0000 = đỏ)
 * @property pH Giá trị pH của dung dịch (1-14)
 * @property concentration Nồng độ (mol/L), null nếu là chất rắn nguyên chất
 */
data class Chemical(
    val id: String,
    val name: String,
    val formula: String,
    val state: ChemicalState,
    val color: Long,
    val pH: Float,
    val concentration: Float? = null
)

/**
 * Kết quả của một phản ứng hóa học.
 *
 * @property equation Phương trình phản ứng cân bằng
 * @property colorChange Màu sắc thay đổi sau phản ứng (ARGB hex)
 * @property precipitate Chất kết tủa tạo thành (null nếu không có)
 * @property gasReleased Khí thoát ra (null nếu không có)
 * @property temperatureChange Thay đổi nhiệt độ (°C, dương = tỏa nhiệt, âm = thu nhiệt)
 * @property explanation Giải thích hiện tượng bằng Tiếng Việt
 */
data class ReactionResult(
    val equation: String,
    val colorChange: Long,
    val precipitate: String? = null,
    val gasReleased: String? = null,
    val temperatureChange: Float,
    val explanation: String
)

/**
 * Danh mục các chất hóa học phổ biến trong chương trình GDPT 2018.
 */
object ChemicalCatalog {
    // Kim loại
    val Fe = Chemical("fe", "Sắt", "Fe", ChemicalState.SOLID, 0xFF78909C, 7.0f)
    val Zn = Chemical("zn", "Kẽm", "Zn", ChemicalState.SOLID, 0xFF9E9E9E, 7.0f)
    val Cu = Chemical("cu", "Đồng", "Cu", ChemicalState.SOLID, 0xFFB87333, 7.0f)
    val Mg = Chemical("mg", "Magie", "Mg", ChemicalState.SOLID, 0xFFE0E0E0, 7.0f)
    val Na = Chemical("na", "Natri", "Na", ChemicalState.SOLID, 0xFFCFD8DC, 7.0f)

    // Axit
    val HCl = Chemical("hcl", "Axit clohiđric", "HCl", ChemicalState.AQUEOUS, 0xFFE0F7FA, 1.0f, 1.0f)
    val H2SO4 = Chemical("h2so4", "Axit sunfuric", "H₂SO₄", ChemicalState.AQUEOUS, 0xFFE0F7FA, 1.0f, 0.5f)
    val HNO3 = Chemical("hno3", "Axit nitric", "HNO₃", ChemicalState.AQUEOUS, 0xFFFFF9C4, 0.5f, 1.0f)

    // Bazơ
    val NaOH = Chemical("naoh", "Natri hiđroxit", "NaOH", ChemicalState.AQUEOUS, 0xFFE0F7FA, 14.0f, 1.0f)
    val CaOH2 = Chemical("caoh2", "Canxi hiđroxit", "Ca(OH)₂", ChemicalState.AQUEOUS, 0xFFE0F7FA, 12.5f, 0.1f)

    // Muối
    val NaCl = Chemical("nacl", "Natri clorua", "NaCl", ChemicalState.AQUEOUS, 0xFFE0F7FA, 7.0f, 0.5f)
    val BaCl2 = Chemical("bacl2", "Bari clorua", "BaCl₂", ChemicalState.AQUEOUS, 0xFFE0F2F1, 6.8f, 0.5f)
    val AgNO3 = Chemical("agno3", "Bạc nitrat", "AgNO₃", ChemicalState.AQUEOUS, 0xFFEDE7F6, 6.5f, 0.1f)
    val CuSO4 = Chemical("cuso4", "Đồng(II) sunfat", "CuSO₄", ChemicalState.AQUEOUS, 0xFF0288D1, 5.5f, 0.5f)
    val FeSO4 = Chemical("feso4", "Sắt(II) sunfat", "FeSO₄", ChemicalState.AQUEOUS, 0xFF81C784, 5.0f, 0.5f)
    val Na2CO3 = Chemical("na2co3", "Natri cacbonat", "Na₂CO₃", ChemicalState.AQUEOUS, 0xFFE0F7FA, 11.5f, 0.5f)
    val K2CrO4 = Chemical("k2cro4", "Kali dicromat", "K₂CrO₄", ChemicalState.AQUEOUS, 0xFFFF9800, 8.5f, 0.1f)

    // Chất khác
    val H2O = Chemical("h2o", "Nước cất", "H₂O", ChemicalState.LIQUID, 0xFFB3E5FC, 7.0f)
    val Phenolphthalein = Chemical("pp", "Phenolphthalein", "C₂₀H₁₄O₄", ChemicalState.AQUEOUS, 0xFFFFFFFF, 7.0f, 0.001f)
    val CaCO3 = Chemical("caco3", "Canxi cacbonat", "CaCO₃", ChemicalState.SOLID, 0xFFECEFF1, 8.5f)
    val KMnO4 = Chemical("kmno4", "Kali pemanganat", "KMnO₄", ChemicalState.SOLID, 0xFF4A148C, 7.0f)

    val all: List<Chemical> = listOf(
        Fe, Zn, Cu, Mg, Na, HCl, H2SO4, HNO3, NaOH, CaOH2,
        NaCl, BaCl2, AgNO3, CuSO4, FeSO4, Na2CO3, K2CrO4,
        H2O, Phenolphthalein, CaCO3, KMnO4
    )

    fun findById(id: String): Chemical? = all.find { it.id == id }
}
