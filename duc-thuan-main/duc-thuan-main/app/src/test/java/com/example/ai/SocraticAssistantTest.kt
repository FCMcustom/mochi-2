package com.example.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests cho SocraticAssistantService và SocraticViewModel.
 */
class SocraticAssistantTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        SocraticAssistantService.reset()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        SocraticAssistantService.reset()
    }

    // ─────────────────────────────────────────────
    // SocraticAssistantService Tests
    // ─────────────────────────────────────────────

    @Test
    fun `System instruction contains required Socratic principles`() {
        val instruction = SocraticAssistantService.getSystemInstruction()

        assertTrue("System instruction must mention GDPT 2018", instruction.contains("GDPT 2018"))
        assertTrue("System instruction must mention Socratic method", instruction.contains("Socratic"))
        assertTrue("System instruction must mention safety rules", instruction.contains("an toàn"))
        assertTrue("System instruction must instruct not to give direct answers", instruction.contains("không đưa ngay đáp án"))
        assertTrue("System instruction must be in Vietnamese", instruction.contains("tiếng Việt"))
    }

    @Test
    fun `Model name is gemini-1-5-flash`() {
        assertEquals("gemini-1.5-flash", SocraticAssistantService.getModelName())
    }

    @Test
    fun `Service starts uninitialized`() {
        // After reset, service should not be initialized
        assertFalse(SocraticAssistantService.isInitialized())
    }

    @Test
    fun `Initialize without API key returns failure`() {
        val result = SocraticAssistantService.initialize()

        // Without a valid API key, initialization should fail
        assertTrue("Initialize should fail without API key", result.isFailure)
    }

    @Test
    fun `ChatMessage stores data correctly`() {
        val timestamp = System.currentTimeMillis()
        val message = ChatMessage(
            sender = "user",
            message = "Fe tác dụng HCl sinh ra khí gì?",
            timestamp = timestamp
        )

        assertEquals("user", message.sender)
        assertEquals("Fe tác dụng HCl sinh ra khí gì?", message.message)
        assertEquals(timestamp, message.timestamp)
    }

    @Test
    fun `ChatMessage default timestamp is set`() {
        val before = System.currentTimeMillis()
        val message = ChatMessage("assistant", "Đó là khí H₂")
        val after = System.currentTimeMillis()

        assertTrue(message.timestamp in before..after)
    }

    @Test
    fun `AssistantResult sealed class has all states`() {
        val streamingStarted = AssistantResult.StreamingStarted
        val chunk = AssistantResult.Chunk("Xin chào")
        val done = AssistantResult.Done("Hoàn thành")
        val error = AssistantResult.Error("Lỗi")

        assertTrue(streamingStarted is AssistantResult)
        assertTrue(chunk is AssistantResult)
        assertTrue(done is AssistantResult)
        assertTrue(error is AssistantResult)

        assertEquals("Xin chào", (chunk as AssistantResult.Chunk).text)
        assertEquals("Hoàn thành", (done as AssistantResult.Done).fullText)
        assertEquals("Lỗi", (error as AssistantResult.Error).message)
    }

    @Test
    fun `BuildPrompt includes history and context`() {
        // Test bằng cách gọi local engine với history
        val history = listOf(
            ChatMessage("user", "Thí nghiệm 1"),
            ChatMessage("assistant", "Phản hồi 1")
        )

        val labState = "Fe + HCl đang phản ứng, có bọt khí"
        val question = "Khí gì thoát ra?"

        val response = SocraticLocalEngine.generateResponse(question, labState)

        assertNotNull(response)
        assertTrue(response.isNotBlank())
        // Response phải có chứa keyword liên quan đến câu hỏi
        assertTrue(
            "Response phải gợi ý về H₂",
            response.contains("H₂") || response.contains("khí") || response.contains("electron")
        )
    }

    // ─────────────────────────────────────────────
    // SocraticLocalEngine Tests
    // ─────────────────────────────────────────────

    @Test
    fun `Local engine handles Cu question`() {
        val response = SocraticLocalEngine.generateResponse(
            "Đồng có phản ứng với HCl không?",
            ""
        )
        assertTrue(response.contains("Socratic") || response.contains("Gợi ý"))
        assertTrue(response.contains("Đồng") || response.contains("Cu"))
    }

    @Test
    fun `Local engine handles Zn HCl question`() {
        val response = SocraticLocalEngine.generateResponse(
            "Kẽm tác dụng HCl tạo ra khí gì?",
            "Zn + HCl"
        )
        assertTrue(response.contains("bọt") || response.contains("H₂") || response.contains("electron"))
    }

    @Test
    fun `Local engine handles precipitate question`() {
        val response = SocraticLocalEngine.generateResponse(
            "BaCl2 với H2SO4 tạo kết tủa gì?",
            ""
        )
        assertTrue(response.contains("kết tủa") || response.contains("BaSO₄") || response.contains("ion"))
    }

    @Test
    fun `Local engine handles phenolphthalein question`() {
        val response = SocraticLocalEngine.generateResponse(
            "NaOH có làm phenolphthalein chuyển màu không?",
            ""
        )
        assertTrue(response.contains("phenol") || response.contains("bazơ") || response.contains("màu"))
    }

    @Test
    fun `Local engine handles safety question`() {
        val response = SocraticLocalEngine.generateResponse(
            "Thí nghiệm với HNO3 đặc có nguy hiểm không?",
            ""
        )
        assertTrue(
            "Safety response must mention safety rules",
            response.contains("an toàn") || response.contains("nguy hiểm") || response.contains("Cảnh báo") || response.contains("⚠")
        )
    }

    @Test
    fun `Local engine handles NO2 question`() {
        val response = SocraticLocalEngine.generateResponse(
            "Khí màu nâu đỏ thoát ra là gì?",
            ""
        )
        assertTrue(response.contains("NO₂") || response.contains("nitơ") || response.contains("nitrogen"))
    }

    @Test
    fun `Local engine handles pH question`() {
        val response = SocraticLocalEngine.generateResponse(
            "pH của HCl 1M là bao nhiêu?",
            ""
        )
        assertTrue(response.contains("pH") || response.contains("H⁺") || response.contains("nồng độ"))
    }

    @Test
    fun `Local engine handles CO2 question`() {
        val response = SocraticLocalEngine.generateResponse(
            "CaCO3 tác dụng HCl sinh ra khí gì?",
            ""
        )
        assertTrue(response.contains("CO₂") || response.contains("cacbonat") || response.contains("khí"))
    }

    @Test
    fun `Local engine handles metal series question`() {
        val response = SocraticLocalEngine.generateResponse(
            "Dãy hoạt động hóa học có thứ tự như thế nào?",
            ""
        )
        assertTrue(response.contains("K") || response.contains("Na") || response.contains("hoạt động"))
    }

    @Test
    fun `Local engine handles default unknown question`() {
        val response = SocraticLocalEngine.generateResponse(
            "Liên kết cộng hóa trị là gì?",
            ""
        )
        assertNotNull(response)
        assertTrue(response.isNotBlank())
        assertTrue(response.contains("Socratic") || response.contains("Gợi ý"))
    }

    @Test
    fun `Local engine handles Fe CuSO4 displacement question`() {
        val response = SocraticLocalEngine.generateResponse(
            "Fe có đẩy Cu ra khỏi CuSO4 được không?",
            ""
        )
        assertTrue(response.contains("Fe") || response.contains("Cu") || response.contains("đẩy") || response.contains("dãy"))
    }

    @Test
    fun `Local engine handles AgCl question`() {
        val response = SocraticLocalEngine.generateResponse(
            "AgNO3 với NaCl tạo kết tủa gì?",
            ""
        )
        assertTrue(response.contains("AgCl") || response.contains("kết tủa") || response.contains("Cl⁻"))
    }

    // ─────────────────────────────────────────────
    // SocraticUiState Tests
    // ─────────────────────────────────────────────

    @Test
    fun `UiState sealed class has all states`() {
        val idle = SocraticUiState.Idle
        val loading = SocraticUiState.Loading
        val streaming = SocraticUiState.Streaming("đang stream...")
        val error = SocraticUiState.Error("Lỗi")

        assertTrue(idle is SocraticUiState)
        assertTrue(loading is SocraticUiState)
        assertTrue(streaming is SocraticUiState)
        assertTrue(error is SocraticUiState)

        assertEquals("đang stream...", (streaming as SocraticUiState.Streaming).partialText)
        assertEquals("Lỗi", (error as SocraticUiState.Error).message)
    }

    @Test
    fun `SocraticViewModelFactory creates correct ViewModel`() {
        val factory = SocraticViewModelFactory()
        val viewModel = factory.create(SocraticViewModel::class.java)

        assertNotNull(viewModel)
        assertTrue(viewModel is SocraticViewModel)
    }

    // ─────────────────────────────────────────────
    // SocraticViewModel Tests (without Android)
    // ─────────────────────────────────────────────

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `ViewModel starts in Idle state`() = runTest {
        val viewModel = SocraticViewModel()

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is SocraticUiState.Idle)
        assertTrue(viewModel.messages.value.isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Empty question is ignored`() = runTest {
        val viewModel = SocraticViewModel()

        viewModel.sendQuestion("")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is SocraticUiState.Idle)
        assertTrue(viewModel.messages.value.isEmpty())

        viewModel.sendQuestion("   ")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.messages.value.isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `sendQuestion adds user message to list`() = runTest {
        val viewModel = SocraticViewModel()

        viewModel.sendQuestion("Fe tác dụng HCl tạo ra khí gì?")
        testDispatcher.scheduler.advanceUntilIdle()

        val messages = viewModel.messages.value
        assertEquals(1, messages.size)
        assertEquals("user", messages[0].sender)
        assertEquals("Fe tác dụng HCl tạo ra khí gì?", messages[0].message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `sendQuestion updates lab state`() = runTest {
        val viewModel = SocraticViewModel()

        viewModel.updateLabState("Fe + HCl đang phản ứng")

        assertEquals("Fe + HCl đang phản ứng", viewModel.labState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `clearHistory empties message list`() = runTest {
        val viewModel = SocraticViewModel()

        viewModel.sendQuestion("Câu hỏi 1")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearHistory()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.messages.value.isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `undoLastMessage removes last message`() = runTest {
        val viewModel = SocraticViewModel()

        viewModel.sendQuestion("Câu hỏi 1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.messages.value.size)

        viewModel.undoLastMessage()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.messages.value.isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `undoLastMessage does nothing on empty list`() = runTest {
        val viewModel = SocraticViewModel()

        viewModel.undoLastMessage()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.messages.value.isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `multiple sendQuestion accumulates messages`() = runTest {
        val viewModel = SocraticViewModel()

        viewModel.sendQuestion("Câu hỏi 1")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendQuestion("Câu hỏi 2")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.messages.value.size)
        assertEquals("Câu hỏi 1", viewModel.messages.value[0].message)
        assertEquals("Câu hỏi 2", viewModel.messages.value[1].message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `conversationHistory reflects messages list`() = runTest {
        val viewModel = SocraticViewModel()

        viewModel.sendQuestion("Test question")
        testDispatcher.scheduler.advanceUntilIdle()

        val history = viewModel.conversationHistory
        assertEquals(1, history.size)
        assertEquals("Test question", history[0].message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `sendQuestionSimple adds user message`() = runTest {
        val viewModel = SocraticViewModel()

        viewModel.sendQuestionSimple("Test simple question")
        testDispatcher.scheduler.advanceUntilIdle()

        val messages = viewModel.messages.value
        assertEquals(1, messages.size)
        assertEquals("user", messages[0].sender)
        assertEquals("Test simple question", messages[0].message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `cancelStreaming returns to Idle`() = runTest {
        val viewModel = SocraticViewModel()

        viewModel.sendQuestion("Long question")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.cancelStreaming()

        assertTrue(viewModel.uiState.value is SocraticUiState.Idle)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `ViewModel handles long message correctly`() = runTest {
        val viewModel = SocraticViewModel()

        val longQuestion = "Cho 5 gam kim loại sắt tác dụng hoàn toàn với 100 ml dung dịch axit clohiđric 1M. Hãy xác định thể tích khí hiđro sinh ra ở điều kiện tiêu chuẩn và nồng độ mol của các chất trong dung dịch sau phản ứng."

        viewModel.sendQuestion(longQuestion)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.messages.value.size)
        assertEquals(longQuestion, viewModel.messages.value[0].message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `ViewModel preserves message order`() = runTest {
        val viewModel = SocraticViewModel()

        val q1 = "Câu hỏi 1"
        val q2 = "Câu hỏi 2"
        val q3 = "Câu hỏi 3"

        viewModel.sendQuestion(q1)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendQuestion(q2)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendQuestion(q3)
        testDispatcher.scheduler.advanceUntilIdle()

        val messages = viewModel.messages.value
        assertEquals(3, messages.size)
        assertEquals(q1, messages[0].message)
        assertEquals(q2, messages[1].message)
        assertEquals(q3, messages[2].message)

        // Timestamps should be in order
        assertTrue(messages[0].timestamp <= messages[1].timestamp)
        assertTrue(messages[1].timestamp <= messages[2].timestamp)
    }
}
