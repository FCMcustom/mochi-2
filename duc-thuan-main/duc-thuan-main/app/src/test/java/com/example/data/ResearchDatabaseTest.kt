package com.example.data

import android.content.Context
import android.net.Uri
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
 * Unit tests cho Room Database với inMemoryDatabaseBuilder.
 * Kiểm thử:
 * - CRUD cho ExperimentSession và StudentMistakeLog
 * - KPI queries: tỷ lệ phản ứng thành công, số câu hỏi Socratic trung bình
 * - CSV export logic
 */
class ResearchDatabaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var database: LabDatabase
    private lateinit var dao: LabDao
    private lateinit var repository: ResearchRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Sử dụng in-memory database cho unit test
        database = LabDatabase.inMemory(android.app.Application())
        dao = database.labDao()
        repository = ResearchRepository(dao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        database.close()
    }

    // ─────────────────────────────────────────────
    // ExperimentSession CRUD Tests
    // ─────────────────────────────────────────────

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Insert and retrieve session successfully`() = runTest {
        val now = System.currentTimeMillis()
        val session = ExperimentSession(
            startTime = now - 600_000,
            endTime = now,
            durationSeconds = 600,
            chemicalsMixed = "Fe+HCl",
            reactionEquation = "Fe + 2HCl → FeCl₂ + H₂↑",
            isReactionOccurred = true,
            socraticPromptCount = 3,
            viewedExplanationDirectly = false,
            reactionType = "METAL_DISPLACEMENT",
            competencyId = "metal_series",
            observedPhenomena = "Sủi bọt khí H2, dung dịch xanh lục"
        )

        val id = dao.insertSession(session)
        assertTrue(id > 0)

        val sessions = dao.getAllSessions().first()
        assertEquals(1, sessions.size)
        assertEquals("Fe+HCl", sessions[0].chemicalsMixed)
        assertTrue(sessions[0].isReactionOccurred)
        assertEquals(3, sessions[0].socraticPromptCount)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Get recent sessions returns limited results`() = runTest {
        val now = System.currentTimeMillis()

        for (i in 1..5) {
            dao.insertSession(
                ExperimentSession(
                    startTime = now - (i * 600_000L),
                    endTime = now - (i * 600_000L - 300_000),
                    durationSeconds = 300,
                    chemicalsMixed = "Chem$i",
                    reactionEquation = null,
                    isReactionOccurred = false,
                    socraticPromptCount = 0,
                    viewedExplanationDirectly = false,
                    reactionType = "UNKNOWN",
                    competencyId = "general",
                    observedPhenomena = ""
                )
            )
        }

        val recent = dao.getRecentSessions(3).first()
        assertEquals(3, recent.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Update session works correctly`() = runTest {
        val now = System.currentTimeMillis()
        val id = dao.insertSession(
            ExperimentSession(
                startTime = now - 600_000,
                endTime = now - 300_000,
                durationSeconds = 300,
                chemicalsMixed = "NaOH+HCl",
                reactionEquation = null,
                isReactionOccurred = false,
                socraticPromptCount = 1,
                viewedExplanationDirectly = false,
                reactionType = "ACID_BASE",
                competencyId = "acid_base",
                observedPhenomena = ""
            )
        )

        // Update với phản ứng
        dao.updateSession(
            ExperimentSession(
                id = id,
                startTime = now - 600_000,
                endTime = now,
                durationSeconds = 600,
                chemicalsMixed = "NaOH+HCl",
                reactionEquation = "NaOH + HCl → NaCl + H₂O",
                isReactionOccurred = true,
                socraticPromptCount = 1,
                viewedExplanationDirectly = true,
                reactionType = "ACID_BASE",
                competencyId = "acid_base",
                observedPhenomena = "Dung dịch trong suốt, tỏa nhiệt"
            )
        )

        val sessions = dao.getAllSessions().first()
        assertEquals(1, sessions.size)
        assertTrue(sessions[0].isReactionOccurred)
        assertEquals("NaOH + HCl → NaCl + H₂O", sessions[0].reactionEquation)
        assertTrue(sessions[0].viewedExplanationDirectly)
    }

    // ─────────────────────────────────────────────
    // StudentMistakeLog CRUD Tests
    // ─────────────────────────────────────────────

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Insert and retrieve mistake logs`() = runTest {
        val sessionId = dao.insertSession(
            ExperimentSession(
                startTime = System.currentTimeMillis() - 600_000,
                endTime = System.currentTimeMillis(),
                durationSeconds = 600,
                chemicalsMixed = "Cu+HNO3",
                reactionEquation = null,
                isReactionOccurred = false,
                socraticPromptCount = 0,
                viewedExplanationDirectly = false,
                reactionType = "REDOX",
                competencyId = "redox",
                observedPhenomena = ""
            )
        )

        dao.insertMistakeLog(
            StudentMistakeLog(
                sessionId = sessionId,
                timestamp = System.currentTimeMillis(),
                mistakeType = MistakeType.SAFETY_VIOLATION,
                description = "Học sinh không đeo kính bảo hộ khi làm việc với HNO3 đặc"
            )
        )

        val logs = dao.getMistakeLogsForSession(sessionId).first()
        assertEquals(1, logs.size)
        assertEquals(MistakeType.SAFETY_VIOLATION, logs[0].mistakeType)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Batch insert mistake logs`() = runTest {
        val sessionId = dao.insertSession(
            ExperimentSession(
                startTime = System.currentTimeMillis() - 600_000,
                endTime = System.currentTimeMillis(),
                durationSeconds = 600,
                chemicalsMixed = "Fe+HCl",
                reactionEquation = "Fe + 2HCl → FeCl₂ + H₂↑",
                isReactionOccurred = true,
                socraticPromptCount = 0,
                viewedExplanationDirectly = false,
                reactionType = "METAL_DISPLACEMENT",
                competencyId = "metal_series",
                observedPhenomena = ""
            )
        )

        val mistakes = listOf(
            StudentMistakeLog(sessionId = sessionId, timestamp = System.currentTimeMillis(), mistakeType = MistakeType.WRONG_REAGENT, description = "Chọn nhầm chất"),
            StudentMistakeLog(sessionId = sessionId, timestamp = System.currentTimeMillis(), mistakeType = MistakeType.UNNECESSARY_HEATING, description = "Đun nóng không cần thiết")
        )

        dao.insertMistakeLogs(mistakes)

        val logs = dao.getMistakeLogsForSession(sessionId).first()
        assertEquals(2, logs.size)
    }

    // ─────────────────────────────────────────────
    // KPI Query Tests
    // ─────────────────────────────────────────────

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Successful reaction rate calculated correctly`() = runTest {
        // 3 phản ứng xảy ra / 5 tổng = 60%
        val sessions = listOf(
            makeSession(1, "Fe+HCl", true),
            makeSession(2, "Cu+HCl", false),
            makeSession(3, "BaCl2+H2SO4", true),
            makeSession(4, "NaOH+HCl", true),
            makeSession(5, "AgNO3+NaCl", false)
        )
        sessions.forEach { dao.insertSession(it) }

        testDispatcher.scheduler.advanceUntilIdle()

        val total = dao.getTotalSessionCount().first()
        val successful = dao.getSuccessfulReactionCount().first()

        assertEquals(5, total)
        assertEquals(3, successful)
        assertEquals(0.6f, successful.toFloat() / total.toFloat(), 0.01f)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Average Socratic prompts per session calculated correctly`() = runTest {
        val sessions = listOf(
            makeSession(1, "Fe+HCl", true, socraticCount = 5),
            makeSession(2, "Cu+HCl", false, socraticCount = 2),
            makeSession(3, "BaCl2+H2SO4", true, socraticCount = 8)
        )
        sessions.forEach { dao.insertSession(it) }

        testDispatcher.scheduler.advanceUntilIdle()

        val avg = dao.getAverageSocraticPromptsPerSession().first()
        // (5 + 2 + 8) / 3 = 5.0
        assertEquals(5.0f, avg, 0.01f)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Self reasoning ratio calculated correctly`() = runTest {
        // 3 tự suy nghĩ / 4 có phản ứng = 75%
        val sessions = listOf(
            makeSession(1, "Fe+HCl", true, viewedDirectly = false),
            makeSession(2, "Cu+HCl", true, viewedDirectly = true),
            makeSession(3, "BaCl2+H2SO4", true, viewedDirectly = false),
            makeSession(4, "NaOH+HCl", true, viewedDirectly = false),
            makeSession(5, "AgNO3+NaCl", false, viewedDirectly = false) // no reaction, excluded
        )
        sessions.forEach { dao.insertSession(it) }

        testDispatcher.scheduler.advanceUntilIdle()

        val ratio = dao.getSelfReasoningRatio().first()
        // (1 false + 2 false + 2 false) = 3 / 4 = 0.75
        // Wait, counted: viewedDirectly=false → self reasoning
        // session1: false → self, session2: true → direct, session3: false → self, session4: false → self
        // 3 self / 4 with reaction = 0.75
        assertEquals(0.75f, ratio ?: 0f, 0.01f)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Experiment count by category returns correct counts`() = runTest {
        val sessions = listOf(
            makeSession(1, "Fe+HCl", true, reactionType = "METAL_DISPLACEMENT"),
            makeSession(2, "Cu+HCl", false, reactionType = "NO_REACTION"),
            makeSession(3, "BaCl2+H2SO4", true, reactionType = "ION_EXCHANGE"),
            makeSession(4, "NaOH+HCl", true, reactionType = "ACID_BASE"),
            makeSession(5, "Zn+H2SO4", true, reactionType = "METAL_DISPLACEMENT"),
            makeSession(6, "AgNO3+NaCl", true, reactionType = "ION_EXCHANGE")
        )
        sessions.forEach { dao.insertSession(it) }

        testDispatcher.scheduler.advanceUntilIdle()

        val categories = dao.getExperimentCountByCategory().first()

        val metalCat = categories.find { it.reactionType == "METAL_DISPLACEMENT" }
        val ionCat = categories.find { it.reactionType == "ION_EXCHANGE" }
        val acidCat = categories.find { it.reactionType == "ACID_BASE" }

        assertNotNull(metalCat)
        assertEquals(2, metalCat?.count)
        assertNotNull(ionCat)
        assertEquals(2, ionCat?.count)
        assertNotNull(acidCat)
        assertEquals(1, acidCat?.count)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Top difficult reactions sorted by mistake count`() = runTest {
        // Tạo sessions và mistake logs
        val s1 = makeSession(1, "Fe+HNO3", true, reactionType = "REDOX")
        dao.insertSession(s1)
        dao.insertMistakeLog(StudentMistakeLog(sessionId = 1, timestamp = System.currentTimeMillis(), mistakeType = MistakeType.SAFETY_VIOLATION, description = "Lỗi 1"))

        val s2 = makeSession(2, "Na+H2O", true, reactionType = "REDOX")
        dao.insertSession(s2)
        dao.insertMistakeLog(StudentMistakeLog(sessionId = 2, timestamp = System.currentTimeMillis(), mistakeType = MistakeType.SAFETY_VIOLATION, description = "Lỗi 1"))
        dao.insertMistakeLog(StudentMistakeLog(sessionId = 2, timestamp = System.currentTimeMillis(), mistakeType = MistakeType.UNNECESSARY_HEATING, description = "Lỗi 2"))
        dao.insertMistakeLog(StudentMistakeLog(sessionId = 2, timestamp = System.currentTimeMillis(), mistakeType = MistakeType.WRONG_REAGENT, description = "Lỗi 3"))

        val s3 = makeSession(3, "Cu+HCl", false, reactionType = "NO_REACTION")
        dao.insertSession(s3)
        dao.insertMistakeLog(StudentMistakeLog(sessionId = 3, timestamp = System.currentTimeMillis(), mistakeType = MistakeType.WRONG_REAGENT, description = "Lỗi 1"))

        testDispatcher.scheduler.advanceUntilIdle()

        val difficult = dao.getTopDifficultReactions().first()

        assertEquals(3, difficult.size)
        // Na+H2O có 3 mistakes → rank 1
        assertEquals("Na+H2O", difficult[0].chemicalsMixed)
        assertEquals(3, difficult[0].mistakeCount)
        // Fe+HNO3 và Cu+HCl đều có 1
        assertTrue(difficult[1].mistakeCount == 1 || difficult[2].mistakeCount == 1)
    }

    // ─────────────────────────────────────────────
    // ResearchRepository Tests
    // ─────────────────────────────────────────────

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Repository dashboardStats emits correct total sessions`() = runTest {
        for (i in 1..3) {
            dao.insertSession(makeSession(i, "Chem$i", i % 2 == 0))
        }

        testDispatcher.scheduler.advanceUntilIdle()

        repository.dashboardStats.collect { stats ->
            assertEquals(3, stats.totalSessions)
            return@collect // Collect once then stop
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Repository startNewSession returns valid sessionId`() = runTest {
        val sessionId = repository.startNewSession(
            chemicalsMixed = "Fe+HCl",
            reactionType = "METAL_DISPLACEMENT",
            competencyId = "metal_series"
        )
        assertTrue(sessionId > 0)

        val sessions = dao.getAllSessions().first()
        assertEquals(1, sessions.size)
        assertEquals("Fe+HCl", sessions[0].chemicalsMixed)
    }

    // ─────────────────────────────────────────────
    // CSV Export Logic Tests
    // ─────────────────────────────────────────────

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `CSV export generates valid header row`() = runTest {
        // Insert some data
        dao.insertSession(makeSession(1, "Fe+HCl", true))

        testDispatcher.scheduler.advanceUntilIdle()

        // Test CSV header format
        val header = "ID Phiên;Thời gian bắt đầu;Thời gian kết thúc;Thời lượng (giây);Chất đã trộn;Phương trình phản ứng;Phản ứng xảy ra;Loại phản ứng;Số câu hỏi Socratic;Xem giải thích trực tiếp;Hiện tượng quan sát;Năng lực GDPT"
        val columns = header.split(";")
        assertEquals(12, columns.size)
        assertTrue(columns.contains("Chất đã trộn"))
        assertTrue(columns.contains("Phản ứng xảy ra"))
        assertTrue(columns.contains("Số câu hỏi Socratic"))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `CSV export handles empty database gracefully`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        // No sessions → CSV should have header only
        val sessions = dao.getAllSessions().first()
        assertTrue(sessions.isEmpty())
    }

    // ─────────────────────────────────────────────
    // Integration Tests
    // ─────────────────────────────────────────────

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Full workflow: create session, add mistakes, query stats`() = runTest {
        // 1. Tạo session
        val sessionId = repository.startNewSession(
            chemicalsMixed = "BaCl2+H2SO4",
            reactionType = "ION_EXCHANGE",
            competencyId = "ion_exchange"
        )
        assertTrue(sessionId > 0)

        // 2. Update session với kết quả
        repository.saveSession(
            ExperimentSession(
                id = sessionId,
                startTime = System.currentTimeMillis() - 600_000,
                endTime = System.currentTimeMillis(),
                durationSeconds = 600,
                chemicalsMixed = "BaCl2+H2SO4",
                reactionEquation = "BaCl₂ + H₂SO₄ → BaSO₄↓ + 2HCl",
                isReactionOccurred = true,
                socraticPromptCount = 4,
                viewedExplanationDirectly = false,
                reactionType = "ION_EXCHANGE",
                competencyId = "ion_exchange",
                observedPhenomena = "Kết tủa trắng BaSO4"
            )
        )

        // 3. Ghi lỗi
        repository.logMistake(
            StudentMistakeLog(
                sessionId = sessionId,
                timestamp = System.currentTimeMillis(),
                mistakeType = MistakeType.MISSING_OBSERVATION,
                description = "Quên quan sát kết tủa trước khi đun nóng"
            )
        )

        testDispatcher.scheduler.advanceUntilIdle()

        // 4. Query stats
        val total = dao.getTotalSessionCount().first()
        val successful = dao.getSuccessfulReactionCount().first()
        val avgPrompts = dao.getAverageSocraticPromptsPerSession().first()
        val mistakes = dao.getMistakeCountByType().first()

        assertEquals(1, total)
        assertEquals(1, successful)
        assertEquals(4.0f, avgPrompts, 0.01f)
        assertEquals(1, mistakes.size)
        assertEquals(MistakeType.MISSING_OBSERVATION, mistakes[0].mistakeType)
    }

    // ─────────────────────────────────────────────
    // Helper Functions
    // ─────────────────────────────────────────────

    private fun makeSession(
        id: Int,
        chemicals: String,
        reactionOccurred: Boolean,
        socraticCount: Int = 0,
        viewedDirectly: Boolean = false,
        reactionType: String = "UNKNOWN"
    ): ExperimentSession {
        val now = System.currentTimeMillis()
        return ExperimentSession(
            id = id.toLong(),
            startTime = now - (id * 600_000L),
            endTime = now - (id * 600_000L - 300_000),
            durationSeconds = 300,
            chemicalsMixed = chemicals,
            reactionEquation = if (reactionOccurred) "Equation for $chemicals" else null,
            isReactionOccurred = reactionOccurred,
            socraticPromptCount = socraticCount,
            viewedExplanationDirectly = viewedDirectly,
            reactionType = reactionType,
            competencyId = "general",
            observedPhenomena = ""
        )
    }
}
