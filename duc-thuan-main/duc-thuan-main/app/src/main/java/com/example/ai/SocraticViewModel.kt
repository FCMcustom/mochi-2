package com.example.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel quản lý trạng thái và logic của Trợ lý Hóa học Socratic.
 * Sử dụng StateFlow để expose state ra UI layer.
 */
class SocraticViewModel(
    private val assistantService: SocraticAssistantService = SocraticAssistantService
) : ViewModel() {

    // Trạng thái UI của màn hình chat
    private val _uiState = MutableStateFlow<SocraticUiState>(SocraticUiState.Idle)
    val uiState: StateFlow<SocraticUiState> = _uiState.asStateFlow()

    // Danh sách tin nhắn hội thoại
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // Lịch sử hội thoại dạng list (dùng cho prompt)
    val conversationHistory: List<ChatMessage>
        get() = _messages.value

    // Trạng thái lab hiện tại (thí nghiệm đang chạy, chất đã cho vào...)
    private val _labState = MutableStateFlow("")
    val labState: StateFlow<String> = _labState.asStateFlow()

    // Job đang chạy để cancel nếu cần
    private var currentStreamingJob: Job? = null

    /**
     * Cập nhật trạng thái phòng thí nghiệm hiện tại.
     * Gọi khi người dùng thêm/bỏ chất, thay đổi thí nghiệm.
     */
    fun updateLabState(state: String) {
        _labState.value = state
    }

    /**
     * Gửi câu hỏi từ học sinh đến Assistant.
     * Sử dụng streaming để nhận phản hồi từng phần.
     *
     * @param userQuestion Câu hỏi/nội dung học sinh nhập
     */
    fun sendQuestion(userQuestion: String) {
        val trimmed = userQuestion.trim()
        if (trimmed.isBlank()) return

        // Cancel job cũ nếu đang chạy
        currentStreamingJob?.cancel()

        // Thêm tin nhắn user vào danh sách
        val userMessage = ChatMessage(
            sender = "user",
            message = trimmed,
            timestamp = System.currentTimeMillis()
        )
        _messages.update { it + userMessage }

        // Bắt đầu streaming
        _uiState.value = SocraticUiState.Loading

        currentStreamingJob = viewModelScope.launch {
            val assistantText = StringBuilder()
            val currentLabState = _labState.value
            val history = conversationHistory.toList()

            assistantService.askAssistant(
                labState = currentLabState,
                userQuestion = trimmed,
                history = history
            )
                .catch { e ->
                    _uiState.value = SocraticUiState.Error(
                        e.message ?: "Đã xảy ra lỗi không xác định"
                    )
                }
                .collect { result ->
                    when (result) {
                        is AssistantResult.StreamingStarted -> {
                            _uiState.value = SocraticUiState.Streaming("")
                        }

                        is AssistantResult.Chunk -> {
                            assistantText.append(result.text)
                            _uiState.value = SocraticUiState.Streaming(assistantText.toString())
                        }

                        is AssistantResult.Done -> {
                            val assistantMessage = ChatMessage(
                                sender = "assistant",
                                message = result.fullText,
                                timestamp = System.currentTimeMillis()
                            )
                            _messages.update { it + assistantMessage }
                            _uiState.value = SocraticUiState.Idle
                        }

                        is AssistantResult.Error -> {
                            // Nếu Gemini fail, thử local fallback
                            handleApiError(result.message)
                        }
                    }
                }
        }
    }

    /**
     * Xử lý khi API gặp lỗi - fallback sang local engine.
     */
    private suspend fun handleApiError(errorMessage: String) {
        // Fallback sang local Socratic response
        val fallbackResponse = SocraticLocalEngine.generateResponse(
            userQuestion = _messages.value.lastOrNull()?.message ?: "",
            labState = _labState.value
        )

        val assistantMessage = ChatMessage(
            sender = "assistant",
            message = fallbackResponse,
            timestamp = System.currentTimeMillis()
        )
        _messages.update { it + assistantMessage }
        _uiState.value = SocraticUiState.Idle
    }

    /**
     * Gửi câu hỏi dạng đồng bộ (non-streaming).
     * Dùng khi cần phản hồi ngay lập tức cho các thao tác nhỏ.
     */
    fun sendQuestionSimple(userQuestion: String) {
        val trimmed = userQuestion.trim()
        if (trimmed.isBlank()) return

        currentStreamingJob?.cancel()

        val userMessage = ChatMessage(
            sender = "user",
            message = trimmed,
            timestamp = System.currentTimeMillis()
        )
        _messages.update { it + userMessage }
        _uiState.value = SocraticUiState.Loading

        viewModelScope.launch {
            val result = assistantService.askAssistantSimple(
                labState = _labState.value,
                userQuestion = trimmed,
                history = conversationHistory.toList()
            )

            result.fold(
                onSuccess = { response ->
                    val assistantMessage = ChatMessage(
                        sender = "assistant",
                        message = response,
                        timestamp = System.currentTimeMillis()
                    )
                    _messages.update { it + assistantMessage }
                    _uiState.value = SocraticUiState.Idle
                },
                onFailure = { e ->
                    handleApiError(e.message ?: "Lỗi không xác định")
                }
            )
        }
    }

    /**
     * Xóa toàn bộ lịch sử hội thoại.
     */
    fun clearHistory() {
        _messages.value = emptyList()
        _uiState.value = SocraticUiState.Idle
    }

    /**
     * Xóa tin nhắn cuối cùng (undo).
     */
    fun undoLastMessage() {
        if (_messages.value.isNotEmpty()) {
            _messages.update { it.dropLast(1) }
        }
    }

    /**
     * Hủy streaming hiện tại.
     */
    fun cancelStreaming() {
        currentStreamingJob?.cancel()
        currentStreamingJob = null
        _uiState.value = SocraticUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        currentStreamingJob?.cancel()
    }
}

/**
 * Trạng thái UI của màn hình Socratic Assistant.
 */
sealed class SocraticUiState {
    /** Chưa có hoạt động gì */
    data object Idle : SocraticUiState()

    /** Đang tải phản hồi */
    data object Loading : SocraticUiState()

    /** Đang nhận streaming chunks */
    data class Streaming(val partialText: String) : SocraticUiState()

    /** Có lỗi xảy ra */
    data class Error(val message: String) : SocraticUiState()
}

/**
 * Factory để tạo SocraticViewModel với dependency injection đơn giản.
 */
class SocraticViewModelFactory(
    private val assistantService: SocraticAssistantService = SocraticAssistantService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SocraticViewModel::class.java)) {
            return SocraticViewModel(assistantService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

/**
 * Local Socratic response engine - fallback khi không có API key.
 * Sử dụng pattern matching để sinh phản hồi gợi mở.
 */
object SocraticLocalEngine {

    /**
     * Sinh phản hồi Socratic cục bộ dựa trên keyword.
     */
    fun generateResponse(userQuestion: String, labState: String): String {
        val questionLower = userQuestion.lowercase()
        val labLower = labState.lowercase()

        return when {
            // Kim loại và axit
            questionLower.contains("đồng") || questionLower.contains("cu") ||
            questionLower.contains("không phản ứng") -> {
                "💡 **Gợi ý Socratic:** Em hãy nhớ lại vị trí của Đồng (Cu) và Hydro (H) trong dãy hoạt động hóa học xem nào! Cặp oxi hóa - khử Cu²⁺/Cu có thế chuẩn E° = +0.34V, cao hơn hay thấp hơn H⁺/H₂ (0.00V)?"
            }

            questionLower.contains("kẽm") || questionLower.contains("zn") ||
            (questionLower.contains("hcl") && questionLower.contains("bọt")) -> {
                "💡 **Gợi ý Socratic:** Khi mẩu Zn tiếp xúc với HCl, bọt khí nổi lên là khí gì? Em hãy quan sát ở góc màn hình 'Vi mô': các electron đang di chuyển từ nguyên tử nào sang ion nào?"
            }

            questionLower.contains("sắt") || questionLower.contains("fe") ||
            questionLower.contains("đinh") -> {
                "💡 **Gợi ý Socratic:** Sắt đứng trước hay sau Hydro trong dãy điện hóa? Em hãy kiểm tra xem sắt có nhường electron cho H⁺ được không, và sản phẩm tạo thành là gì?"
            }

            // Kết tủa
            questionLower.contains("kết tủa") || questionLower.contains("baso4") ||
            questionLower.contains("bari") -> {
                "💡 **Gợi ý Socratic:** Để phản ứng trao đổi ion trong dung dịch chất điện li xảy ra, cần có ít nhất một trong ba điều kiện nào? Hãy kiểm tra tính tan của muối Bari sunfat (BaSO₄) trong nước!"
            }

            questionLower.contains("agcl") || questionLower.contains("bạc") ||
            questionLower.contains("agno3") -> {
                "💡 **Gợi ý Socratic:** Thuốc thử AgNO₃ dùng để nhận biết ion nào? Muối AgCl có tan trong axit nitric không, và có đặc điểm gì thú vị khi để ngoài ánh sáng?"
            }

            // Khí
            questionLower.contains("nâu đỏ") || questionLower.contains("no2") ||
            questionLower.contains("nitơ") || questionLower.contains("nitric") -> {
                "⚠️ **Gợi ý Socratic:** Khí màu nâu đỏ bốc lên chính là Nitrogen dioxide (NO₂). Số oxi hóa của Nitơ đã thay đổi từ bao nhiêu trong HNO₃ về bao nhiêu trong NO₂? Vì sao thí nghiệm này bắt buộc phải làm trong tủ hút?"
            }

            questionLower.contains("co2") || questionLower.contains("cacbonat") ||
            questionLower.contains("đá vôi") || questionLower.contains("caco3") -> {
                "💡 **Gợi ý Socratic:** Khi axit mạnh tác dụng với muối cacbonat, axit cacbonic (H₂CO₃) có bền không? Nó phân hủy thành những chất gì? Khí CO₂ có thể nhận biết bằng thuốc thử nào?"
            }

            // Bazơ & Chỉ thị
            questionLower.contains("phenol") || questionLower.contains("chỉ thị") ||
            questionLower.contains("bazơ") || questionLower.contains("kiềm") -> {
                "💡 **Gợi ý Socratic:** Chỉ thị phenolphthalein chuyển màu ở pH bao nhiêu? Em hãy nhớ lại thang pH và màu của phenolphthalein trong môi trường axit (pH < 7) và bazơ (pH > 8.2)!"
            }

            questionLower.contains("naoh") || questionLower.contains("natri hiđroxit") -> {
                "💡 **Gợi ý Socratic:** NaOH là bazơ mạnh hay yếu? Trong nước, NaOH phân li hoàn toàn thành những ion nào? Khi cho phenolphthalein vào, màu sắc thay đổi như thế nào?"
            }

            // Kim loại kiềm
            questionLower.contains("natri") || questionLower.contains("na") ||
            questionLower.contains("nổ") || questionLower.contains("cháy") ||
            questionLower.contains("kiềm") -> {
                "⚠️ **Gợi ý Socratic:** Natri là kim loại kiềm nhóm IA có năng lượng ion hóa rất thấp. Tại sao khi thả vào nước mẩu Na lại nóng chảy thành viên tròn và bốc cháy? Phản ứng này tỏa hay thu nhiệt?"
            }

            // pH
            questionLower.contains("ph") || questionLower.contains("axit") -> {
                "💡 **Gợi ý Socratic:** pH = 1 nghĩa là nồng độ ion H⁺ là bao nhiêu mol/L? Hãy nhớ công thức: pH = -log[H⁺]. Axit mạnh có pH như thế nào so với axit yếu cùng nồng độ?"
            }

            // Nhiệt
            questionLower.contains("nhiệt") || questionLower.contains("tỏa") ||
            questionLower.contains("thu") || questionLower.contains("enthalpy") -> {
                "💡 **Gợi ý Socratic:** Phản ứng tỏa nhiệt có ΔH mang dấu gì? Em hãy phân biệt giữa năng lượng phân ly liên kết và năng lượng tạo liên kết mới. Phản ứng nào cần cung cấp nhiệt để xảy ra?"
            }

            // Màu sắc
            questionLower.contains("màu") || questionLower.contains("xanh") ||
            questionLower.contains("đỏ") || questionLower.contains("trắng") -> {
                "💡 **Gợi ý Socratic:** Màu sắc dung dịch liên quan đến loại cation nào? Ion Cu²⁺ tạo màu gì trong nước? Còn Fe²⁺ và Fe³⁺ thì sao?"
            }

            // Thí nghiệm cụ thể
            questionLower.contains("cuso4") && questionLower.contains("naoh") ||
            questionLower.contains("đồng") && questionLower.contains("kiềm") -> {
                "💡 **Gợi ý Socratic:** Khi cho dung dịch kiềm (NaOH) vào dung dịch muối đồng (CuSO₄), em quan sát thấy hiện tượng gì? Cation Cu²⁺ kết hợp với anion OH⁻ tạo thành hợp chất nào?"
            }

            labLower.contains("trung hòa") || labLower.contains("naoh") && labLower.contains("hcl") -> {
                "💡 **Gợi ý Socratic:** Phản ứng trung hòa axit - bazơ có bản chất là gì ở cấp độ ion? Ion H⁺ và OH⁻ kết hợp tạo thành phân tử nào? Nhiệt trung hòa là bao nhiêu kJ/mol?"
            }

            // Câu hỏi chung
            questionLower.contains("phương trình") || questionLower.contains("pt") ||
            questionLower.contains("cân bằng") -> {
                "💡 **Gợi ý Socratic:** Trước khi cân bằng, em hãy xác định số oxi hóa của mỗi nguyên tố. Sau đó kiểm tra tổng nguyên tử mỗi nguyên tố ở hai vế đã bằng nhau chưa nhé!"
            }

            questionLower.contains("dãy") || questionLower.contains("thế điện cực") ||
            questionLower.contains("hoạt động") -> {
                "💡 **Gợi ý Socratic:** Em hãy nhớ thứ tự dãy hoạt động: K Na Ca Mg Al Zn Fe Ni Sn Pb H Cu Ag Au. Kim loại đứng trước có thể đẩy kim loại đứng sau ra khỏi dung dịch muối của chúng. Đúng không nào?"
            }

            // An toàn
            questionLower.contains("nguy hiểm") || questionLower.contains("an toàn") ||
            questionLower.contains("cảnh báo") || questionLower.contains("khí độc") -> {
                "⚠️ **Lưu ý an toàn:** Trong phòng thí nghiệm hóa học THPT, luôn tuân thủ các quy tắc: (1) Đeo kính bảo hộ và găng tay khi làm việc với axit/kiềm đặc. (2) Không ngửi trực tiếp khí thoát ra. (3) Làm thí nghiệm với khí độc trong tủ hút. (4) Báo thầy/cô ngay nếu có sự cố."
            }

            // Mặc định
            else -> {
                "💡 **Trợ lý Socratic:** Câu hỏi rất hay! Trước khi thầy đưa ra kết luận, em hãy quan sát kỹ hai khía cạnh: (1) **Vĩ mô**: màu sắc, bọt khí, kết tủa, nhiệt độ; (2) **Vi mô**: sự trao đổi electron, ion hóa, liên kết. Em dự đoán liên kết nào vừa bị bẻ gãy và liên kết nào vừa được hình thành?"
            }
        }
    }
}
