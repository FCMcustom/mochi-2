package com.example.ai

import android.util.Log
import com.example.BuildConfig
import com.example.model.ReactionOutcome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object SocraticAssistant {
    private const val TAG = "SocraticAssistant"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Ask the Socratic Lab Assistant for guided pedagogical inquiry
     */
    suspend fun consultAssistant(
        userMessage: String,
        currentExperimentContext: String,
        studentHypothesis: String = ""
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = buildSocraticPrompt(userMessage, currentExperimentContext, studentHypothesis)
                val response = callGeminiRest(apiKey, "gemini-3.5-flash", prompt)
                if (response.isNotBlank()) {
                    return@withContext response
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini API call failed, falling back to local Socratic tutor engine: ${e.message}")
            }
        }

        // Intelligent local Socratic pedagogical tutor
        generateLocalSocraticResponse(userMessage, currentExperimentContext, studentHypothesis)
    }

    /**
     * Evaluate student free-text explanation against pedagogical rubric
     */
    suspend fun evaluateStudentExplanation(
        studentText: String,
        outcome: ReactionOutcome
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    Bạn là Trợ lý giáo dục Hóa học THPT theo chương trình GDPT 2018.
                    Hãy chấm điểm và nhận xét câu giải thích của học sinh về thí nghiệm sau:
                    - Phản ứng: ${outcome.balancedEquation}
                    - Phương trình ion rút gọn: ${outcome.netIonicEquation}
                    - Bản chất vi mô: ${outcome.microExplanationVi}
                    - Năng lượng phản ứng: ΔrH° = ${outcome.deltaH} kJ/mol (${if (outcome.deltaH < 0) "Tỏa nhiệt" else "Thu nhiệt"})
                    
                    Câu trả lời của học sinh: "$studentText"
                    
                    Yêu cầu:
                    1. Đánh giá tính chính xác về mặt hóa học (đúng/sai/thiếu sót).
                    2. Khen ngợi điểm học sinh hiểu đúng (sự chuyển dịch electron, cation/anion, hiện tượng vĩ mô).
                    3. Đặt 1 câu hỏi Socratic gợi mở để học sinh đào sâu thêm bản chất.
                    4. Giữ giọng điệu sư phạm, khích lệ và ngắn gọn (dưới 120 từ).
                """.trimIndent()

                val result = callGeminiRest(apiKey, "gemini-3.5-flash", prompt)
                if (result.isNotBlank()) return@withContext result
            } catch (e: Exception) {
                Log.w(TAG, "Gemini evaluation fallback: ${e.message}")
            }
        }

        // Local algorithmic NLP rubric evaluation
        evaluateLocalRubric(studentText, outcome)
    }

    private fun callGeminiRest(apiKey: String, model: String, prompt: String): String {
        val url = "$BASE_URL/$model:generateContent?key=$apiKey"

        val requestJson = JSONObject().apply {
            val contentsArr = JSONArray().apply {
                val item = JSONObject().apply {
                    val partsArr = JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    }
                    put("parts", partsArr)
                }
                put(item)
            }
            put("contents", contentsArr)

            val genConfig = JSONObject().apply {
                put("temperature", 0.6)
                put("maxOutputTokens", 600)
            }
            put("generationConfig", genConfig)
        }

        val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("HTTP ${response.code}: ${response.message}")
            }
            val bodyString = response.body?.string() ?: return ""
            val json = JSONObject(bodyString)
            val candidates = json.optJSONArray("candidates") ?: return ""
            if (candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    return parts.getJSONObject(0).optString("text", "")
                }
            }
        }
        return ""
    }

    private fun buildSocraticPrompt(userMsg: String, context: String, hypothesis: String): String {
        return """
            Bạn là 'Trợ lý phòng Lab Hóa học Socratic' theo chuẩn chương trình GDPT 2018 của Bộ Giáo dục và Đào tạo Việt Nam.
            Nguyên tắc phản hồi Socratic:
            - TUYỆT ĐỐI KHÔNG đưa ra ngay câu trả lời trực tiếp nếu học sinh hỏi 'chất nào phản ứng' hay 'kết quả ra sao'.
            - Hãy đặt câu hỏi gợi mở định hướng vào: Dãy hoạt động hóa học/thế điện cực chuẩn, điều kiện trao đổi ion (kết tủa, bay hơi), hoặc quá trình chuyển giao electron (vi mô).
            - Bám sát danh pháp IUPAC kết hợp tên tiếng Việt SGK 2018 (Ví dụ: Zinc/Kẽm, Hydrochloric acid/Axit clohidric).
            - Ngắn gọn, thân thiện, mang tính kích thích tư duy khoa học.
            
            Ngữ cảnh thí nghiệm hiện tại: $context
            Giả thuyết học sinh đã chọn: $hypothesis
            Học sinh hỏi/nói: "$userMsg"
        """.trimIndent()
    }

    private fun generateLocalSocraticResponse(userMsg: String, context: String, hypothesis: String): String {
        val lower = userMsg.lowercase()
        return when {
            lower.contains("cu") || lower.contains("đồng") || lower.contains("không phản ứng") -> {
                "💡 **Gợi ý Socratic:** Em hãy nhớ lại vị trí của Đồng (Cu) và Hydro (H) trong dãy hoạt động hóa học hoặc dãy thế điện cực chuẩn xem nào! Cặp oxi hóa - khử Cu²⁺/Cu có thế chuẩn E° = +0.34V, liệu ion H⁺ (0.00V) có đủ mạnh để oxi hóa được Cu không?"
            }
            lower.contains("kẽm") || lower.contains("zn") || lower.contains("hcl") -> {
                "💡 **Gợi ý Socratic:** Khi mẩu Zn tiếp xúc với HCl, bọt khí nổi lên là khí gì? Em hãy quan sát ở góc màn hình 'Vi mô': các electron đang di chuyển từ nguyên tử nào sang ion nào?"
            }
            lower.contains("natri") || lower.contains("na") || lower.contains("nổ") || lower.contains("cháy") -> {
                "⚠️ **Gợi ý Socratic:** Natri là kim loại kiềm nhóm IA có năng lượng ion hóa rất thấp. Tại sao khi thả vào nước mẩu Na lại nóng chảy thành viên tròn và bốc cháy? Phản ứng này tỏa ra bao nhiêu nhiệt lượng (ΔrH°)?"
            }
            lower.contains("kết tủa") || lower.contains("baso4") || lower.contains("bari") -> {
                "💡 **Gợi ý Socratic:** Để phản ứng trao đổi ion trong dung dịch chất điện li xảy ra, cần có ít nhất một trong ba điều kiện nào? Hãy kiểm tra tính tan của muối Bari sunfat (BaSO4) trong nước và trong axit xem nhé!"
            }
            lower.contains("nâu đỏ") || lower.contains("no2") || lower.contains("độc") -> {
                "⚠️ **Gợi ý Socratic:** Khí màu nâu đỏ bốc lên chính là Nitrogen dioxide (NO2). Số oxi hóa của Nitơ đã thay đổi từ bao nhiêu trong HNO3 về bao nhiêu trong NO2? Vì sao thí nghiệm này bắt buộc phải làm trong tủ hút?"
            }
            else -> {
                "💡 **Trợ lý Socratic:** Câu hỏi rất hay! Trước khi thầy đưa ra kết luận, em hãy quan sát kỹ hai hiện tượng: (1) Màu sắc dung dịch và bọt khí (vĩ mô), (2) Sự trao đổi electron giữa các hạt phân tử (vi mô). Em dự đoán liên kết nào vừa bị bẻ gãy?"
            }
        }
    }

    private fun evaluateLocalRubric(studentText: String, outcome: ReactionOutcome): String {
        val text = studentText.lowercase()
        var score = 0
        val feedbackItems = mutableListOf<String>()

        if (text.contains("electron") || text.contains("nhường") || text.contains("nhận") || text.contains("oxi hóa") || text.contains("khử")) {
            score += 35
            feedbackItems.add("✓ Đã nhận diện đúng bản chất chuyển dịch electron / quá trình oxi hóa khử.")
        }
        if (text.contains("ion") || text.contains("h+") || text.contains("zn2+") || text.contains("kết tủa") || text.contains("liên kết")) {
            score += 35
            feedbackItems.add("✓ Nêu được tương tác giữa các ion/hạt ở cấp độ vi mô.")
        }
        if (text.contains("tỏa nhiệt") || text.contains("thu nhiệt") || text.contains("nhiệt độ") || text.contains("enthalpy") || text.contains("năng lượng")) {
            score += 30
            feedbackItems.add("✓ Có phân tích yếu tố nhiệt phản ứng và năng lượng liên kết theo GDPT 2018.")
        }

        val totalScore = score.coerceAtLeast(30)
        val rankStr = if (totalScore >= 80) "Rất xuất sắc (Mức 3 - Vận dụng cao)" else if (totalScore >= 50) "Khá tốt (Mức 2 - Thông hiểu)" else "Cần bổ sung (Mức 1 - Nhận biết)"

        val sb = StringBuilder()
        sb.append("📊 **Đánh giá năng lực hóa học GDPT 2018:** $rankStr ($totalScore/100 điểm)\n\n")
        if (feedbackItems.isNotEmpty()) {
            feedbackItems.forEach { sb.append(it).append("\n") }
        } else {
            sb.append("• Em đã ghi nhận được hiện tượng cơ bản, nhưng cần bổ sung thêm giải thích về mặt hạt ion và số electron trao đổi.\n")
        }
        sb.append("\n🎯 **Bản chất chuẩn:** ").append(outcome.microExplanationVi)
        return sb.toString()
    }
}
