package com.example.engine

import com.example.model.ChemicalCompetency
import com.example.model.CompetencyMastery
import com.example.model.CompetencyRegistry

object BktEngine {

    /**
     * Standard Bayesian Knowledge Tracing Bayesian Update:
     * P(L_{t-1} | Obs) followed by P(L_t) transition update.
     */
    fun updateMastery(
        competency: ChemicalCompetency,
        currentP: Float,
        isCorrect: Boolean
    ): Float {
        val pL = currentP.coerceIn(0.01f, 0.99f)
        val pG = competency.pGuess
        val pS = competency.pSlip
        val pT = competency.pLearn

        // Posterior probability given observation
        val pLGivenObs = if (isCorrect) {
            val numerator = pL * (1f - pS)
            val denominator = (pL * (1f - pS)) + ((1f - pL) * pG)
            (numerator / denominator.coerceAtLeast(0.001f)).coerceIn(0.01f, 0.99f)
        } else {
            val numerator = pL * pS
            val denominator = (pL * pS) + ((1f - pL) * (1f - pG))
            (numerator / denominator.coerceAtLeast(0.001f)).coerceIn(0.01f, 0.99f)
        }

        // Transition to next state (learning effect)
        val nextP = pLGivenObs + (1f - pLGivenObs) * pT
        return nextP.coerceIn(0.05f, 0.99f)
    }

    /**
     * Determine adaptive recommendation based on weakest competency
     */
    fun getAdaptiveRecommendation(masteries: List<CompetencyMastery>): String {
        if (masteries.isEmpty()) return "Hãy thử làm thí nghiệm Zn + HCl để kiểm tra năng lực ban đầu."
        val lowest = masteries.minByOrNull { it.currentProbability } ?: masteries.first()

        return when (lowest.competency.id) {
            "metal_series" -> {
                if (lowest.currentProbability < 0.5f)
                    "Đề xuất thích ứng: Độ thành thạo Dãy điện hóa kim loại còn thấp (${(lowest.currentProbability * 100).toInt()}%). Hãy làm thí nghiệm đối chứng: Fe + CuSO4 so với Cu + HCl."
                else
                    "Độ thành thạo Dãy hoạt động kim loại đạt ${(lowest.currentProbability * 100).toInt()}%. Bạn đã sẵn sàng cho bài tập nâng cao về ăn mòn điện hóa!"
            }
            "ion_exchange" -> {
                if (lowest.currentProbability < 0.5f)
                    "Đề xuất thích ứng: Hãy rèn luyện thêm về Điều kiện phản ứng trao đổi ion với thí nghiệm BaCl2 + H2SO4 và AgNO3 + NaCl."
                else
                    "Độ thành thạo Trao đổi ion & Kết tủa đạt ${(lowest.currentProbability * 100).toInt()}% vững vàng."
            }
            "redox_thermo" -> {
                if (lowest.currentProbability < 0.5f)
                    "Đề xuất thích ứng: Khái niệm Oxi hóa - khử và Enthalpy (ΔrH°) cần củng cố. Hãy quan sát hoạt ảnh Vi mô trao đổi electron trong phản ứng Cu + HNO3 đặc!"
                else
                    "Độ hiểu bản chất Enthalpy & Electron đạt ${(lowest.currentProbability * 100).toInt()}%. Rất xuất sắc!"
            }
            else -> {
                "Hãy tiếp tục khám phá các thí nghiệm nguy hiểm và ghi nhận hiện tượng vào nhật ký nghiên cứu."
            }
        }
    }
}
