package com.example.engine

import com.example.model.Chemical
import com.example.model.ChemicalCatalog
import com.example.model.ChemicalState
import com.example.model.ReactionResult

/**
 * Engine mô phỏng phản ứng hóa học cơ bản.
 * Phù hợp với chương trình Hóa học lớp 10/11 GDPT 2018.
 */
object ChemicalEngine {

    /**
     * Phản ứng 1: Fe + HCl → FeCl₂ + H₂
     * Sắt tác dụng với axit clohiđric giải phóng khí hiđro.
     */
    private val reactionFeHCl = ReactionTemplate(
        reactantA = ChemicalCatalog.Fe,
        reactantB = ChemicalCatalog.HCl,
        result = ReactionResult(
            equation = "Fe + 2HCl → FeCl₂ + H₂↑",
            colorChange = 0xFF81C784, // Xanh lục nhạt (dung dịch FeCl2)
            gasReleased = "H₂",
            temperatureChange = 8.5f,
            explanation = "Sắt đứng trước hiđro trong dãy hoạt động hóa học nên khử được ion H⁺ thành khí H₂. Mẩu sắt tan dần, sủi bọt khí không màu, dung dịch chuyển sang màu xanh lục nhạt."
        )
    )

    /**
     * Phản ứng 2: BaCl₂ + H₂SO₄ → BaSO₄↓ + 2HCl
     * Bari clorua tác dụng axit sunfuric tạo kết tủa trắng.
     */
    private val reactionBaCl2H2SO4 = ReactionTemplate(
        reactantA = ChemicalCatalog.BaCl2,
        reactantB = ChemicalCatalog.H2SO4,
        result = ReactionResult(
            equation = "BaCl₂ + H₂SO₄ → BaSO₄↓ + 2HCl",
            colorChange = 0xFFFFFFFF, // Trắng (kết tủa BaSO4)
            precipitate = "BaSO₄",
            temperatureChange = 2.0f,
            explanation = "Ion Ba²⁺ gặp ion SO₄²⁻ tạo kết tủa trắng BaSO₄ không tan trong axit. Đây là phản ứng nhận biết ion sunfat SO₄²⁻."
        )
    )

    /**
     * Phản ứng 3: NaOH + Phenolphthalein → Màu hồng
     * Natri hiđroxit làm chỉ thị phenolphthalein chuyển sang màu hồng.
     */
    private val reactionNaOHPhenolphthalein = ReactionTemplate(
        reactantA = ChemicalCatalog.NaOH,
        reactantB = ChemicalCatalog.Phenolphthalein,
        result = ReactionResult(
            equation = "NaOH + Phenolphthalein → Màu hồng (môi trường bazơ)",
            colorChange = 0xFFFF69B4, // Hồng
            temperatureChange = 0f,
            explanation = "Phenolphthalein là chỉ thị axit-bazơ. Trong môi trường bazơ (pH > 8.2), phenolphthalein chuyển sang màu hồng đặc trưng. Đây là thí nghiệm nhận biết dung dịch bazơ."
        )
    )

    /**
     * Phản ứng 4: AgNO₃ + NaCl → AgCl↓ + NaNO₃
     * Bạc nitrat tác dụng natri clorua tạo kết tủa trắng.
     */
    private val reactionAgNO3NaCl = ReactionTemplate(
        reactantA = ChemicalCatalog.AgNO3,
        reactantB = ChemicalCatalog.NaCl,
        result = ReactionResult(
            equation = "AgNO₃ + NaCl → AgCl↓ + NaNO₃",
            colorChange = 0xFFF5F5F5, // Trắng sữa
            precipitate = "AgCl",
            temperatureChange = 3.0f,
            explanation = "Ion Ag⁺ gặp ion Cl⁻ tạo kết tủa trắng vón của bạc clorua (AgCl). Kết tủa không tan trong axit nitric nhưng tan trong dung dịch amoniac."
        )
    )

    /**
     * Phản ứng 5: Zn + H₂SO₄ → ZnSO₄ + H₂↑
     * Kẽm tác dụng axit sunfuric loãng giải phóng khí hiđro.
     */
    private val reactionZnH2SO4 = ReactionTemplate(
        reactantA = ChemicalCatalog.Zn,
        reactantB = ChemicalCatalog.H2SO4,
        result = ReactionResult(
            equation = "Zn + H₂SO₄ → ZnSO₄ + H₂↑",
            colorChange = 0xFFE0F7FA,
            gasReleased = "H₂",
            temperatureChange = 10.0f,
            explanation = "Kẽm là kim loại hoạt động, đứng trước hiđro trong dãy điện hóa. Kẽm nhường electron cho ion H⁺, tạo khí H₂ thoát ra dưới dạng bọt khí."
        )
    )

    /**
     * Phản ứng 6: CuSO₄ + 2NaOH → Cu(OH)₂↓ + Na₂SO₄
     * Đồng(II) sunfat tác dụng natri hiđroxit tạo kết tủa xanh.
     */
    private val reactionCuSO4NaOH = ReactionTemplate(
        reactantA = ChemicalCatalog.CuSO4,
        reactantB = ChemicalCatalog.NaOH,
        result = ReactionResult(
            equation = "CuSO₄ + 2NaOH → Cu(OH)₂↓ + Na₂SO₄",
            colorChange = 0xFF00BCD4, // Xanh lam nhạt
            precipitate = "Cu(OH)₂",
            temperatureChange = 2.5f,
            explanation = "Ion Cu²⁺ gặp ion OH⁻ tạo kết tủa keo xanh lam Cu(OH)₂. Đây là phản ứng đặc trưng của muối đồng với kiềm."
        )
    )

    /**
     * Phản ứng 7: HCl + NaOH → NaCl + H₂O
     * Phản ứng trung hòa axit-bazơ.
     */
    private val reactionHClNaOH = ReactionTemplate(
        reactantA = ChemicalCatalog.HCl,
        reactantB = ChemicalCatalog.NaOH,
        result = ReactionResult(
            equation = "HCl + NaOH → NaCl + H₂O",
            colorChange = 0xFFE0F7FA,
            temperatureChange = 6.8f,
            explanation = "Phản ứng trung hòa giữa axit mạnh và bazơ mạnh. Ion H⁺ kết hợp với ion OH⁻ tạo phân tử nước, tỏa nhiệt khoảng 57.3 kJ/mol."
        )
    )

    /**
     * Phản ứng 8: CaCO₃ + 2HCl → CaCl₂ + CO₂↑ + H₂O
     * Đá vôi tác dụng axit clohiđric.
     */
    private val reactionCaCO3HCl = ReactionTemplate(
        reactantA = ChemicalCatalog.CaCO3,
        reactantB = ChemicalCatalog.HCl,
        result = ReactionResult(
            equation = "CaCO₃ + 2HCl → CaCl₂ + CO₂↑ + H₂O",
            colorChange = 0xFFE0F7FA,
            gasReleased = "CO₂",
            temperatureChange = 3.5f,
            explanation = "Axit mạnh HCl phản ứng với muối cacbonat, giải phóng khí CO₂ làm sủi bọt. Khí CO₂ làm vẩn đục nước vôi trong (Ca(OH)₂)."
        )
    )

    /**
     * Phản ứng 9: Fe + CuSO₄ → FeSO₄ + Cu
     * Sắt đẩy đồng ra khỏi dung dịch muối đồng.
     */
    private val reactionFeCuSO4 = ReactionTemplate(
        reactantA = ChemicalCatalog.Fe,
        reactantB = ChemicalCatalog.CuSO4,
        result = ReactionResult(
            equation = "Fe + CuSO₄ → FeSO₄ + Cu↓",
            colorChange = 0xFF81C784, // Xanh lục nhạt
            precipitate = "Cu",
            temperatureChange = 4.5f,
            explanation = "Sắt có tính khử mạnh hơn đồng (đứng trước Cu trong dãy điện hóa) nên đẩy ion Cu²⁺ ra khỏi dung dịch, tạo lớp đồng kim loại màu đỏ bám trên thanh sắt."
        )
    )

    /**
     * Phản ứng 10: BaCl₂ + Na₂CO₃ → BaCO₃↓ + 2NaCl
     * Bari clorua tác dụng natri cacbonat tạo kết tủa trắng.
     */
    private val reactionBaCl2Na2CO3 = ReactionTemplate(
        reactantA = ChemicalCatalog.BaCl2,
        reactantB = ChemicalCatalog.Na2CO3,
        result = ReactionResult(
            equation = "BaCl₂ + Na₂CO₃ → BaCO₃↓ + 2NaCl",
            colorChange = 0xFFFFFFFF,
            precipitate = "BaCO₃",
            temperatureChange = 1.5f,
            explanation = "Ion Ba²⁺ gặp ion CO₃²⁻ tạo kết tủa trắng BaCO₃. Kết tủa tan trong axit mạnh nhưng không tan trong nước."
        )
    )

    private val reactions: List<ReactionTemplate> = listOf(
        reactionFeHCl,
        reactionBaCl2H2SO4,
        reactionNaOHPhenolphthalein,
        reactionAgNO3NaCl,
        reactionZnH2SO4,
        reactionCuSO4NaOH,
        reactionHClNaOH,
        reactionCaCO3HCl,
        reactionFeCuSO4,
        reactionBaCl2Na2CO3
    )

    /**
     * Mô phỏng phản ứng giữa hai chất hóa học.
     *
     * @param substanceA Chất thứ nhất
     * @param substanceB Chất thứ hai
     * @return ReactionResult chứa thông tin về phản ứng
     */
    fun mix(substanceA: Chemical, substanceB: Chemical): ReactionResult {
        val idA = substanceA.id
        val idB = substanceB.id

        // Tìm phản ứng phù hợp (không phân biệt thứ tự)
        for (reaction in reactions) {
            if ((reaction.reactantA.id == idA && reaction.reactantB.id == idB) ||
                (reaction.reactantA.id == idB && reaction.reactantB.id == idA)) {
                return reaction.result
            }
        }

        // Phản ứng mặc định: không có phản ứng
        return ReactionResult(
            equation = "${substanceA.formula} + ${substanceB.formula} → Không xảy ra phản ứng",
            colorChange = mixColors(substanceA.color, substanceB.color),
            temperatureChange = 0f,
            explanation = "Hai chất này không phản ứng với nhau trong điều kiện thường hoặc không thuộc danh sách phản ứng được mô phỏng."
        )
    }

    /**
     * Trộn hai màu sắc theo tỷ lệ.
     */
    private fun mixColors(colorA: Long, colorB: Long, ratio: Float = 0.5f): Long {
        val aA = (colorA shr 24) and 0xFF
        val aB = (colorB shr 24) and 0xFF
        val rA = (colorA shr 16) and 0xFF
        val rB = (colorB shr 16) and 0xFF
        val gA = (colorA shr 8) and 0xFF
        val gB = (colorB shr 8) and 0xFF
        val bA = colorA and 0xFF
        val bB = colorB and 0xFF

        val a = ((aA * ratio + aB * (1 - ratio)).toInt() shl 24)
        val r = ((rA * ratio + rB * (1 - ratio)).toInt() shl 16)
        val g = ((gA * ratio + gB * (1 - ratio)).toInt() shl 8)
        val b = ((bA * ratio + bB * (1 - ratio)).toInt())

        return (a + r + g + b).toLong() and 0xFFFFFFFF
    }

    /**
     * Lấy danh sách các phản ứng có sẵn.
     */
    fun getAvailableReactions(): List<ReactionTemplate> = reactions
}

/**
 * Template lưu trữ thông tin phản ứng.
 */
data class ReactionTemplate(
    val reactantA: Chemical,
    val reactantB: Chemical,
    val result: ReactionResult
)
