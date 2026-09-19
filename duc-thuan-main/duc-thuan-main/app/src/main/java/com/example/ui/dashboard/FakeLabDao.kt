package com.example.ui.dashboard

import com.example.data.DifficultReaction
import com.example.data.ExperimentSession
import com.example.data.LabDao
import com.example.data.MistakeCountByType
import com.example.data.ReactionCountByCategory
import com.example.data.StudentMistakeLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Fake LabDao dùng cho Compose Preview với dữ liệu mẫu phong phú.
 */
class FakeLabDaoForPreview : LabDao {

    private val sampleSessions = listOf(
        ExperimentSession(
            id = 1, startTime = System.currentTimeMillis() - 3600_000,
            endTime = System.currentTimeMillis() - 3000_000,
            durationSeconds = 600, chemicalsMixed = "Fe+HCl",
            reactionEquation = "Fe + 2HCl → FeCl₂ + H₂↑",
            isReactionOccurred = true, socraticPromptCount = 3,
            viewedExplanationDirectly = false, reactionType = "METAL_DISPLACEMENT",
            competencyId = "metal_series", observedPhenomena = "Sủi bọt khí H2"
        ),
        ExperimentSession(
            id = 2, startTime = System.currentTimeMillis() - 7200_000,
            endTime = System.currentTimeMillis() - 6600_000,
            durationSeconds = 600, chemicalsMixed = "BaCl2+H2SO4",
            reactionEquation = "BaCl₂ + H₂SO₄ → BaSO₄↓ + 2HCl",
            isReactionOccurred = true, socraticPromptCount = 5,
            viewedExplanationDirectly = false, reactionType = "ION_EXCHANGE",
            competencyId = "ion_exchange", observedPhenomena = "Kết tủa trắng BaSO4"
        ),
        ExperimentSession(
            id = 3, startTime = System.currentTimeMillis() - 10800_000,
            endTime = System.currentTimeMillis() - 10200_000,
            durationSeconds = 600, chemicalsMixed = "Cu+HCl",
            reactionEquation = "Không phản ứng",
            isReactionOccurred = false, socraticPromptCount = 2,
            viewedExplanationDirectly = true, reactionType = "NO_REACTION",
            competencyId = "metal_series", observedPhenomena = "Không có hiện tượng"
        ),
        ExperimentSession(
            id = 4, startTime = System.currentTimeMillis() - 14400_000,
            endTime = System.currentTimeMillis() - 13800_000,
            durationSeconds = 600, chemicalsMixed = "NaOH+Phenolphthalein",
            reactionEquation = "NaOH + Phenolphthalein → Màu hồng",
            isReactionOccurred = true, socraticPromptCount = 4,
            viewedExplanationDirectly = false, reactionType = "ACID_BASE",
            competencyId = "acid_base", observedPhenomena = "Dung dịch chuyển sang màu hồng"
        ),
        ExperimentSession(
            id = 5, startTime = System.currentTimeMillis() - 18000_000,
            endTime = System.currentTimeMillis() - 17400_000,
            durationSeconds = 600, chemicalsMixed = "AgNO3+NaCl",
            reactionEquation = "AgNO₃ + NaCl → AgCl↓ + NaNO₃",
            isReactionOccurred = true, socraticPromptCount = 6,
            viewedExplanationDirectly = false, reactionType = "ION_EXCHANGE",
            competencyId = "ion_exchange", observedPhenomena = "Kết tủa trắng vón AgCl"
        )
    )

    private val sampleCategoryStats = listOf(
        ReactionCountByCategory("ION_EXCHANGE", 3),
        ReactionCountByCategory("METAL_DISPLACEMENT", 2),
        ReactionCountByCategory("ACID_BASE", 1),
        ReactionCountByCategory("NO_REACTION", 1)
    )

    private val sampleMistakes = listOf(
        MistakeCountByType("WRONG_REAGENT", 4),
        MistakeCountByType("PREMATURE_REVELATION", 3),
        MistakeCountByType("UNNECESSARY_HEATING", 2),
        MistakeCountByType("MISSING_OBSERVATION", 1)
    )

    private val sampleDifficult = listOf(
        DifficultReaction("Cu+HNO3", 7),
        DifficultReaction("Na+H2O", 5),
        DifficultReaction("Fe+CuSO4", 3)
    )

    override fun getAllSessions(): Flow<List<ExperimentSession>> = flowOf(sampleSessions)
    override fun getRecentSessions(limit: Int): Flow<List<ExperimentSession>> = flowOf(sampleSessions.take(limit))
    override fun getTotalSessionCount(): Flow<Int> = flowOf(5)
    override fun getSuccessfulReactionCount(): Flow<Int> = flowOf(4)
    override fun getTotalSocraticPromptCount(): Flow<Int?> = flowOf(20)
    override fun getAverageSessionDuration(): Flow<Float?> = flowOf(600f)
    override fun getSelfReasoningRatio(): Flow<Float?> = flowOf(0.75f)
    override fun getAverageSocraticPromptsPerSession(): Flow<Float> = flowOf(4f)
    override fun getExperimentCountByCategory(): Flow<List<ReactionCountByCategory>> = flowOf(sampleCategoryStats)
    override fun getTopDifficultReactions(): Flow<List<DifficultReaction>> = flowOf(sampleDifficult)
    override fun getMistakeCountByType(): Flow<List<MistakeCountByType>> = flowOf(sampleMistakes)
    override fun getAllExperimentRecords(): Flow<List<com.example.data.ExperimentRecord>> = flowOf(emptyList())
    override fun getAllBktSkills(): Flow<List<com.example.data.BktSkillRecord>> = flowOf(emptyList())
    override fun getAllResearchStudents(): Flow<List<com.example.data.ResearchStudentSample>> = flowOf(emptyList())

    override suspend fun insertSession(session: ExperimentSession): Long = 1L
    override suspend fun updateSession(session: ExperimentSession) {}
    override suspend fun insertMistakeLog(log: StudentMistakeLog): Long = 1L
    override suspend fun insertMistakeLogs(logs: List<StudentMistakeLog>) {}
    override suspend fun insertExperimentRecord(record: com.example.data.ExperimentRecord): Long = 1L
    override suspend fun upsertBktSkill(skill: com.example.data.BktSkillRecord) {}
    override suspend fun insertResearchStudents(students: List<com.example.data.ResearchStudentSample>) {}
    override suspend fun getResearchStudentCount(): Int = 0
    override suspend fun getSessionCount(): Int = 5
    override suspend fun getSuccessfulReactionCountSync(): Int = 4
    override fun getMistakeLogsForSession(sessionId: Long): Flow<List<StudentMistakeLog>> = flowOf(emptyList())
}

/**
 * Fake LabDao trả về dữ liệu trống cho preview trạng thái rỗng.
 */
class FakeEmptyDaoForPreview : LabDao {
    override fun getAllSessions(): Flow<List<ExperimentSession>> = flowOf(emptyList())
    override fun getRecentSessions(limit: Int): Flow<List<ExperimentSession>> = flowOf(emptyList())
    override fun getTotalSessionCount(): Flow<Int> = flowOf(0)
    override fun getSuccessfulReactionCount(): Flow<Int> = flowOf(0)
    override fun getTotalSocraticPromptCount(): Flow<Int?> = flowOf(0)
    override fun getAverageSessionDuration(): Flow<Float?> = flowOf(0f)
    override fun getSelfReasoningRatio(): Flow<Float?> = flowOf(0f)
    override fun getAverageSocraticPromptsPerSession(): Flow<Float> = flowOf(0f)
    override fun getExperimentCountByCategory(): Flow<List<ReactionCountByCategory>> = flowOf(emptyList())
    override fun getTopDifficultReactions(): Flow<List<DifficultReaction>> = flowOf(emptyList())
    override fun getMistakeCountByType(): Flow<List<MistakeCountByType>> = flowOf(emptyList())
    override fun getAllExperimentRecords(): Flow<List<com.example.data.ExperimentRecord>> = flowOf(emptyList())
    override fun getAllBktSkills(): Flow<List<com.example.data.BktSkillRecord>> = flowOf(emptyList())
    override fun getAllResearchStudents(): Flow<List<com.example.data.ResearchStudentSample>> = flowOf(emptyList())
    override suspend fun insertSession(session: ExperimentSession): Long = 1L
    override suspend fun updateSession(session: ExperimentSession) {}
    override suspend fun insertMistakeLog(log: StudentMistakeLog): Long = 1L
    override suspend fun insertMistakeLogs(logs: List<StudentMistakeLog>) {}
    override suspend fun insertExperimentRecord(record: com.example.data.ExperimentRecord): Long = 1L
    override suspend fun upsertBktSkill(skill: com.example.data.BktSkillRecord) {}
    override suspend fun insertResearchStudents(students: List<com.example.data.ResearchStudentSample>) {}
    override suspend fun getResearchStudentCount(): Int = 0
    override suspend fun getSessionCount(): Int = 0
    override suspend fun getSuccessfulReactionCountSync(): Int = 0
    override fun getMistakeLogsForSession(sessionId: Long): Flow<List<StudentMistakeLog>> = flowOf(emptyList())
}
