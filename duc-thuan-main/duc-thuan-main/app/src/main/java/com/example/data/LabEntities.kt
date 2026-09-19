package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Ghi nhận một phiên thí nghiệm hoàn chỉnh trong Virtual Lab.
 */
@Entity(tableName = "experiment_sessions")
data class ExperimentSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val durationSeconds: Int,
    /** Danh sách các chất đã trộn, phân cách bởi dấu "+", ví dụ: "Fe+HCl" */
    val chemicalsMixed: String,
    /** Phương trình phản ứng (có thể null nếu không xảy ra phản ứng) */
    val reactionEquation: String?,
    /** Có xảy ra phản ứng hóa học hay không */
    val isReactionOccurred: Boolean,
    /** Số câu hỏi học sinh đã gửi cho AI Socratic trong phiên này */
    val socraticPromptCount: Int,
    /** Học sinh đã tự suy nghĩ (false) hay bấm xem giải thích trực tiếp (true) */
    val viewedExplanationDirectly: Boolean,
    /** Loại phản ứng: "METAL_DISPLACEMENT", "ION_EXCHANGE", "ACID_BASE", "REDOX", "NO_REACTION" */
    val reactionType: String,
    /** ID năng lực GDPT 2018 liên quan, ví dụ: "metal_series", "ion_exchange" */
    val competencyId: String,
    /** Hiện tượng vĩ mô quan sát được */
    val observedPhenomena: String
)

/**
 * Nhật ký lỗi / sai sót của học sinh trong quá trình thực hành.
 */
@Entity(tableName = "student_mistake_logs")
data class StudentMistakeLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val timestamp: Long,
    /** Loại lỗi, ví dụ: "WRONG_REAGENT", "UNNECESSARY_HEATING", "SAFETY_VIOLATION", "WRONG_SEQUENCE" */
    val mistakeType: String,
    val description: String
)

/**
 * Chuẩn hóa các loại lỗi thực hành.
 */
object MistakeType {
    const val WRONG_REAGENT = "WRONG_REAGENT"
    const val UNNECESSARY_HEATING = "UNNECESSARY_HEATING"
    const val SAFETY_VIOLATION = "SAFETY_VIOLATION"
    const val WRONG_SEQUENCE = "WRONG_SEQUENCE"
    const val PREMATURE_REVELATION = "PREMATURE_REVELATION"
    const val MISSING_OBSERVATION = "MISSING_OBSERVATION"
    const val WRONG_PH_PREDICTION = "WRONG_PH_PREDICTION"
}

/**
 * Chuẩn hóa các loại phản ứng hóa học.
 */
object ReactionCategory {
    const val ACID_BASE = "ACID_BASE"
    const val METAL_DISPLACEMENT = "METAL_DISPLACEMENT"
    const val REDOX = "REDOX"
    const val ION_EXCHANGE = "ION_EXCHANGE"
    const val NO_REACTION = "NO_REACTION"
}

// ─── Các entity có sẵn ────────────────────────────────────────

@Entity(tableName = "experiment_records")
data class ExperimentRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val experimentTitle: String,
    val reactantAId: String,
    val reactantBId: String,
    val predictionText: String,
    val actualPhenomena: String,
    val isHypothesisConfirmed: Boolean,
    val studentExplanation: String,
    val competencyId: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bkt_skills")
data class BktSkillRecord(
    @PrimaryKey val skillId: String,
    val masteryProb: Float,
    val attemptsCount: Int,
    val correctCount: Int,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "research_students")
data class ResearchStudentSample(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentCode: String,
    val groupType: String, // "EXPERIMENTAL" or "CONTROL"
    val preScore: Float,
    val postScore: Float,
    val interactionCount: Int,
    val susScore: Float = 85f
)
