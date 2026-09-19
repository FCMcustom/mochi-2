package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LabDao {
    // ─── Experiment Sessions ────────────────────────────────────

    @Insert
    suspend fun insertSession(session: ExperimentSession): Long

    @Update
    suspend fun updateSession(session: ExperimentSession)

    @Query("SELECT * FROM experiment_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<ExperimentSession>>

    @Query("SELECT * FROM experiment_sessions ORDER BY startTime DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<ExperimentSession>>

    @Query("SELECT COUNT(*) FROM experiment_sessions")
    fun getTotalSessionCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM experiment_sessions WHERE isReactionOccurred = 1")
    fun getSuccessfulReactionCount(): Flow<Int>

    @Query("SELECT SUM(socraticPromptCount) FROM experiment_sessions")
    fun getTotalSocraticPromptCount(): Flow<Int?>

    @Query("SELECT AVG(durationSeconds) FROM experiment_sessions WHERE durationSeconds > 0")
    fun getAverageSessionDuration(): Flow<Float?>

    /**
     * KPI: Tỷ lệ học sinh tự suy nghĩ trước khi xem đáp án.
     * = (số phiên KHÔNG xem trực tiếp) / (tổng số phiên có phản ứng)
     */
    @Query("""
        SELECT
            CAST(SUM(CASE WHEN viewedExplanationDirectly = 0 AND isReactionOccurred = 1 THEN 1 ELSE 0 END) AS FLOAT)
            / NULLIF(SUM(CASE WHEN isReactionOccurred = 1 THEN 1 ELSE 0 END), 0)
        FROM experiment_sessions
    """)
    fun getSelfReasoningRatio(): Flow<Float?>

    /**
     * KPI: Số câu hỏi Socratic trung bình mỗi buổi.
     */
    @Query("""
        SELECT CAST(COALESCE(SUM(socraticPromptCount), 0) AS FLOAT)
        / NULLIF(COUNT(*), 0)
        FROM experiment_sessions
    """)
    fun getAverageSocraticPromptsPerSession(): Flow<Float>

    /**
     * KPI: Số lượng thí nghiệm theo từng chương (reaction type).
     */
    @Query("SELECT reactionType, COUNT(*) as count FROM experiment_sessions GROUP BY reactionType ORDER BY count DESC")
    fun getExperimentCountByCategory(): Flow<List<ReactionCountByCategory>>

    /**
     * KPI: Top 3 phản ứng hay gặp khó khăn nhất (nhiều mistake logs).
     */
    @Query("""
        SELECT es.chemicalsMixed, COUNT(sm.id) as mistakeCount
        FROM experiment_sessions es
        LEFT JOIN student_mistake_logs sm ON es.id = sm.sessionId
        GROUP BY es.chemicalsMixed
        ORDER BY mistakeCount DESC
        LIMIT 3
    """)
    fun getTopDifficultReactions(): Flow<List<DifficultReaction>>

    // ─── Student Mistake Logs ─────────────────────────────────

    @Insert
    suspend fun insertMistakeLog(log: StudentMistakeLog): Long

    @Insert
    suspend fun insertMistakeLogs(logs: List<StudentMistakeLog>)

    @Query("SELECT * FROM student_mistake_logs WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMistakeLogsForSession(sessionId: Long): Flow<List<StudentMistakeLog>>

    /**
     * Thống kê lỗi theo loại.
     */
    @Query("SELECT mistakeType, COUNT(*) as count FROM student_mistake_logs GROUP BY mistakeType ORDER BY count DESC")
    fun getMistakeCountByType(): Flow<List<MistakeCountByType>>

    // ─── Experiment Records (legacy) ───────────────────────────

    @Query("SELECT * FROM experiment_records ORDER BY timestamp DESC")
    fun getAllExperimentRecords(): Flow<List<ExperimentRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperimentRecord(record: ExperimentRecord): Long

    // ─── BKT Skills ────────────────────────────────────────────

    @Query("SELECT * FROM bkt_skills")
    fun getAllBktSkills(): Flow<List<BktSkillRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBktSkill(skill: BktSkillRecord)

    // ─── Research Students ─────────────────────────────────────

    @Query("SELECT * FROM research_students ORDER BY id ASC")
    fun getAllResearchStudents(): Flow<List<ResearchStudentSample>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResearchStudents(students: List<ResearchStudentSample>)

    @Query("SELECT COUNT(*) FROM research_students")
    suspend fun getResearchStudentCount(): Int

    @Query("SELECT COUNT(*) FROM experiment_sessions")
    suspend fun getSessionCount(): Int

    @Query("SELECT COUNT(*) FROM experiment_sessions WHERE isReactionOccurred = 1")
    suspend fun getSuccessfulReactionCountSync(): Int
}

// ─── Data class cho query kết quả tổng hợp ───────────────────

data class ReactionCountByCategory(
    val reactionType: String,
    val count: Int
)

data class DifficultReaction(
    val chemicalsMixed: String,
    val mistakeCount: Int
)

data class MistakeCountByType(
    val mistakeType: String,
    val count: Int
)
