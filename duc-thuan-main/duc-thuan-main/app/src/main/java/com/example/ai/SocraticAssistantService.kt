package com.example.ai

import android.util.Log
import com.example.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.java.GenerativeModelFutures
import com.google.ai.client.generativeai.model.Content
import com.google.ai.client.generativeai.model.GenerationMetrics
import com.google.ai.client.generativeai.model.GenerationResponse
import com.google.ai.client.generativeai.model.SafetySetting
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.uncheckedCast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Lớp service giao tiếp với Gemini API cho Trợ lý Hóa học Socratic.
 * Sử dụng Gemini SDK chính thức với model gemini-1.5-flash.
 */
object SocraticAssistantService {

    private const val TAG = "SocraticAssistant"
    private const val MODEL_NAME = "gemini-1.5-flash"
    private const val MODEL_NAME_VISION = "gemini-1.5-flash"

    // System Instruction chuẩn Socratic theo chương trình GDPT 2018
    private const val SYSTEM_INSTRUCTION = """
Bạn là Trợ lý Phòng thí nghiệm Hóa học ảo THPT theo chuẩn chương trình GDPT 2018.
Nguyên tắc sư phạm: Tuyệt đối không đưa ngay đáp án hoặc kết quả phản ứng hoàn chỉnh.
Khi học sinh hỏi hoặc thực hiện một thao tác, hãy:
- Đặt câu hỏi gợi mở về hiện tượng thực tế (màu sắc, trạng thái, bọt khí, kết tủa).
- Gợi ý học sinh nhớ lại bản chất liên kết, phản ứng trao đổi ion hoặc quy tắc oxy hóa - khử.
- Nhắc nhở quy tắc an toàn phòng thí nghiệm nếu học sinh thử các thao tác nguy hiểm.
- Luôn dùng xưng hô thân thiện, khuyến khích tư duy khoa học.
- Trả lời bằng tiếng Việt, ngắn gọn (dưới 150 từ), có emoji minh họa.
"""

    private var generativeModel: GenerativeModel? = null
    private var generativeModelFutures: GenerativeModelFutures? = null
    private var isInitialized = false

    /**
     * Cấu hình safety settings để cho phép nội dung hóa học phổ thông.
     */
    private val safetySettings: List<SafetySetting> = listOf(
        SafetySetting(HarmCategory.HARM_CATEGORY_HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.HARM_CATEGORY_HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT, BlockThreshold.BLOCK_MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT, BlockThreshold.BLOCK_ONLY_HIGH)
    )

    /**
     * Cấu hình generation config cho phản hồi ngắn gọn, sư phạm.
     */
    private val generationConfig = GenerationConfig.Builder()
        .setTemperature(0.7)
        .setTopK(40)
        .setTopP(0.95)
        .setMaxOutputTokens(512)
        .build()

    /**
     * Khởi tạo GenerativeModel với Gemini SDK.
     * Đọc API Key an toàn từ BuildConfig.GEMINI_API_KEY.
     *
     * @return true nếu khởi tạo thành công, false nếu API Key không hợp lệ
     */
    @Synchronized
    fun initialize(): Result<Unit> {
        if (isInitialized && generativeModel != null) {
            return Result.success(Unit)
        }

        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            Log.w(TAG, "GEMINI_API_KEY not found in BuildConfig: ${e.message}")
            ""
        }

        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "CHANGEME") {
            Log.w(TAG, "Gemini API Key is not configured. Using fallback local engine.")
            return Result.failure(IllegalStateException("GEMINI_API_KEY chưa được cấu hình trong BuildConfig"))
        }

        return try {
            generativeModel = GenerativeModel(
                modelName = MODEL_NAME,
                apiKey = apiKey,
                generationConfig = generationConfig,
                safetySettings = safetySettings,
                systemInstruction = Content.Builder()
                    .addText(SYSTEM_INSTRUCTION)
                    .build()
            )

            // Khởi tạo Java Futures wrapper để hỗ trợ streaming
            generativeModelFutures = GenerativeModelFutures.from(generativeModel!!)
            isInitialized = true
            Log.i(TAG, "Gemini GenerativeModel initialized successfully with model: $MODEL_NAME")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize GenerativeModel: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Kiểm tra xem service đã được khởi tạo chưa.
     */
    fun isInitialized(): Boolean = isInitialized && generativeModel != null

    /**
     * Tạo luồng phản hồi streaming từ Gemini API.
     * Trả về Flow<String> để UI có thể nhận từng chunk phản hồi.
     *
     * @param labState Trạng thái phòng thí nghiệm hiện tại (thí nghiệm đang thực hiện, chất đã cho vào)
     * @param userQuestion Câu hỏi của học sinh
     * @param history Lịch sử hội thoại [List of ChatMessage]
     * @return Flow<String> phát từng đoạn phản hồi
     */
    fun askAssistant(
        labState: String,
        userQuestion: String,
        history: List<ChatMessage>
    ): Flow<AssistantResult> = flow {
        // Bước 1: Kiểm tra khởi tạo
        if (!isInitialized()) {
            val initResult = initialize()
            if (initResult.isFailure) {
                emit(AssistantResult.Error(initResult.exceptionOrNull()?.message ?: "Không thể khởi tạo Gemini API"))
                return@flow
            }
        }

        // Bước 2: Build prompt từ history + context
        val fullPrompt = buildPrompt(labState, userQuestion, history)

        try {
            val model = generativeModel ?: run {
                emit(AssistantResult.Error("GenerativeModel chưa được khởi tạo"))
                return@flow
            }

            // Bước 3: Gọi API với streaming
            emit(AssistantResult.StreamingStarted)

            val response = model.generateContentStream(fullPrompt)

            val fullText = StringBuilder()
            response.collect { part ->
                if (part.text != null) {
                    fullText.append(part.text)
                    emit(AssistantResult.Chunk(part.text!!))
                }
            }

            emit(AssistantResult.Done(fullText.toString()))

        } catch (e: Exception) {
            Log.e(TAG, "Gemini API streaming error: ${e.message}", e)
            emit(AssistantResult.Error("Lỗi kết nối Gemini API: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Gửi câu hỏi và nhận phản hồi hoàn chỉnh (non-streaming).
     *
     * @param labState Trạng thái phòng thí nghiệm hiện tại
     * @param userQuestion Câu hỏi của học sinh
     * @param history Lịch sử hội thoại
     * @return Phản hồi hoàn chỉnh hoặc lỗi
     */
    suspend fun askAssistantSimple(
        labState: String,
        userQuestion: String,
        history: List<ChatMessage> = emptyList()
    ): Result<String> = withContext(Dispatchers.IO) {
        if (!isInitialized()) {
            val initResult = initialize()
            if (initResult.isFailure) {
                return@withContext Result.failure(initResult.exceptionOrNull()!!)
            }
        }

        val fullPrompt = buildPrompt(labState, userQuestion, history)

        try {
            val model = generativeModel ?: return@withContext Result.failure(
                IllegalStateException("GenerativeModel chưa được khởi tạo")
            )

            val response = model.generateContent(fullPrompt)
            val text = response.text ?: ""

            // Log metrics nếu có
            try {
                @Suppress("UNCHECKED_CAST")
                val metrics = response.uncheckedCast<GenerationResponse>()?.let {
                    (it as? GenerationMetrics)
                }
                if (metrics != null) {
                    Log.d(TAG, "Gemini response - promptChars: ${metrics.promptTokenCount}, completionTokens: ${metrics.candidatesTokenCount}")
                }
            } catch (_: Exception) {
                // Metrics logging is best-effort
            }

            Result.success(text)
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Xây dựng prompt hoàn chỉnh từ lịch sử hội thoại và ngữ cảnh.
     */
    private fun buildPrompt(labState: String, userQuestion: String, history: List<ChatMessage>): Content {
        val roleParts = mutableListOf<Content.Role>()

        // Định dạng lịch sử hội thoại
        val historyText = if (history.isNotEmpty()) {
            buildString {
                history.forEach { msg ->
                    when (msg.sender) {
                        "user" -> append("Học sinh: ${msg.message}\n")
                        "assistant" -> append("Trợ lý: ${msg.message}\n")
                        else -> append("${msg.sender}: ${msg.message}\n")
                    }
                }
            }
        } else {
            ""
        }

        val promptText = buildString {
            if (historyText.isNotBlank()) {
                append("=== Lịch sử hội thoại ===\n$historyText\n=== Kết thúc lịch sử ===\n\n")
            }
            append("=== Ngữ cảnh phòng thí nghiệm ===\n$labState\n=== Hết ngữ cảnh ===\n\n")
            append("=== Câu hỏi của học sinh ===\n$userQuestion\n=== Hết câu hỏi ===\n\n")
            append("Hãy trả lời theo phong cách Socratic: gợi mở, đặt câu hỏi, KHÔNG đưa đáp án trực tiếp.")
        }

        return Content.Builder()
            .addText(promptText)
            .setRole("user")
            .build()
    }

    /**
     * Lấy system instruction hiện tại (để test).
     */
    fun getSystemInstruction(): String = SYSTEM_INSTRUCTION

    /**
     * Lấy tên model đang dùng.
     */
    fun getModelName(): String = MODEL_NAME

    /**
     * Reset trạng thái service (dùng khi cần thay API key mới).
     */
    @Synchronized
    fun reset() {
        generativeModel = null
        generativeModelFutures = null
        isInitialized = false
        Log.i(TAG, "SocraticAssistantService has been reset")
    }
}

/**
 * Lớp đại diện cho một tin nhắn trong cuộc hội thoại.
 *
 * @property sender Người gửi: "user" hoặc "assistant"
 * @property message Nội dung tin nhắn
 * @property timestamp Thời điểm gửi (epoch millis)
 */
data class ChatMessage(
    val sender: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Sealed class đại diện cho các trạng thái phản hồi từ Assistant.
 */
sealed class AssistantResult {
    /** Bắt đầu streaming */
    data object StreamingStarted : AssistantResult()

    /** Một chunk phản hồi được nhận */
    data class Chunk(val text: String) : AssistantResult()

    /** Phản hồi hoàn chỉnh */
    data class Done(val fullText: String) : AssistantResult()

    /** Lỗi xảy ra */
    data class Error(val message: String) : AssistantResult()
}
