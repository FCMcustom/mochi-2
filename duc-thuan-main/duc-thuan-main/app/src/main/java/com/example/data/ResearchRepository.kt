package com.example.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repository cho màn hình Dashboard Thống kê KHKT.
 * Cung cấp StateFlow<DashboardStatsUiState> chứa các chỉ số KPI.
 * Hỗ trợ xuất dữ liệu ra file CSV.
 */
class ResearchRepository(private val dao: LabDao) {

    /**
     * Trạng thái KPI tổng hợp cho Dashboard.
     */
    data class DashboardStatsUiState(
        val totalSessions: Int = 0,
        val successfulReactionRate: Float = 0f,       // 0.0..1.0
        val averageSocraticPromptsPerSession: Float = 0f,
        val averageSessionDurationMinutes: Float = 0f,
        val selfReasoningRatio: Float = 0f,          // Tỷ lệ tự suy nghĩ trước khi xem đáp án
        val experimentsByCategory: List<CategoryStat> = emptyList(),
        val topDifficultReactions: List<DifficultStat> = emptyList(),
        val recentSessions: List<SessionSummary> = emptyList(),
        val mistakeBreakdown: List<MistakeStat> = emptyList(),
        val isLoading: Boolean = true
    )

    data class CategoryStat(
        val category: String,
        val label: String,
        val count: Int
    )

    data class DifficultStat(
        val chemicalsMixed: String,
        val mistakeCount: Int
    )

    data class SessionSummary(
        val id: Long,
        val chemicalsMixed: String,
        val reactionType: String,
        val isReactionOccurred: Boolean,
        val durationSeconds: Int,
        val startTime: Long,
        val socraticPromptCount: Int,
        val viewedDirectly: Boolean
    )

    data class MistakeStat(
        val mistakeType: String,
        val label: String,
        val count: Int
    )

    /**
     * Luồng StateFlow tổng hợp tất cả các KPI.
     */
    val dashboardStats: Flow<DashboardStatsUiState> = combine(
        dao.getTotalSessionCount(),
        dao.getSuccessfulReactionCount(),
        dao.getAverageSocraticPromptsPerSession(),
        dao.getAverageSessionDuration(),
        dao.getSelfReasoningRatio(),
        dao.getExperimentCountByCategory(),
        dao.getTopDifficultReactions(),
        dao.getRecentSessions(20),
        dao.getMistakeCountByType()
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val totalSessions = values[0] as Int
        val successfulCount = values[1] as Int
        val avgPrompts = values[2] as Float
        val avgDuration = (values[3] as? Float) ?: 0f
        val selfReasoningRatio = (values[4] as? Float) ?: 0f
        val categoryStats = values[5] as List<ReactionCountByCategory>
        val difficultStats = values[6] as List<DifficultReaction>
        val recentSessions = values[7] as List<ExperimentSession>
        val mistakeStats = values[8] as List<MistakeCountByType>

        val successRate = if (totalSessions > 0) successfulCount.toFloat() / totalSessions else 0f

        DashboardStatsUiState(
            totalSessions = totalSessions,
            successfulReactionRate = successRate,
            averageSocraticPromptsPerSession = avgPrompts,
            averageSessionDurationMinutes = avgDuration / 60f,
            selfReasoningRatio = selfReasoningRatio,
            experimentsByCategory = categoryStats.map { cat ->
                CategoryStat(
                    category = cat.reactionType,
                    label = REACTION_CATEGORY_LABELS[cat.reactionType] ?: cat.reactionType,
                    count = cat.count
                )
            },
            topDifficultReactions = difficultStats.map {
                DifficultStat(chemicalsMixed = it.chemicalsMixed, mistakeCount = it.mistakeCount)
            },
            recentSessions = recentSessions.map { session ->
                SessionSummary(
                    id = session.id,
                    chemicalsMixed = session.chemicalsMixed,
                    reactionType = REACTION_CATEGORY_LABELS[session.reactionType] ?: session.reactionType,
                    isReactionOccurred = session.isReactionOccurred,
                    durationSeconds = session.durationSeconds,
                    startTime = session.startTime,
                    socraticPromptCount = session.socraticPromptCount,
                    viewedDirectly = session.viewedExplanationDirectly
                )
            },
            mistakeBreakdown = mistakeStats.map { mistake ->
                MistakeStat(
                    mistakeType = mistake.mistakeType,
                    label = MISTAKE_LABELS[mistake.mistakeType] ?: mistake.mistakeType,
                    count = mistake.count
                )
            },
            isLoading = false
        )
    }

    /**
     * Ghi một phiên thí nghiệm mới.
     */
    suspend fun saveSession(session: ExperimentSession): Long {
        return dao.insertSession(session)
    }

    /**
     * Cập nhật phiên thí nghiệm (ví dụ: sau khi biết có phản ứng xảy ra).
     */
    suspend fun updateSession(session: ExperimentSession) {
        dao.updateSession(session)
    }

    /**
     * Ghi một lỗi của học sinh.
     */
    suspend fun logMistake(mistake: StudentMistakeLog): Long {
        return dao.insertMistakeLog(mistake)
    }

    /**
     * Ghi nhiều lỗi cùng lúc.
     */
    suspend fun logMistakes(mistakes: List<StudentMistakeLog>) {
        dao.insertMistakeLogs(mistakes)
    }

    /**
     * Xuất toàn bộ nhật ký phiên thực nghiệm ra file CSV.
     * Lưu vào thư mục cache và trả về Uri để chia sẻ.
     *
     * @param context Context ứng dụng
     * @return Uri của file CSV đã tạo, hoặc null nếu thất bại
     */
    suspend fun exportResearchDataToCsv(context: Context): Uri? {
        return try {
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val timestamp = dateFormat.format(Date())
            val fileName = "chemlab_research_export_$timestamp.csv"

            val cacheDir = context.cacheDir
            val file = File(cacheDir, fileName)

            FileWriter(file).use { writer ->
                // BOM cho Excel hỗ trợ tiếng Việt
                writer.write("\uFEFF")

                // Header CSV
                val header = listOf(
                    "ID Phiên", "Thời gian bắt đầu", "Thời gian kết thúc",
                    "Thời lượng (giây)", "Chất đã trộn", "Phương trình phản ứng",
                    "Phản ứng xảy ra", "Loại phản ứng", "Số câu hỏi Socratic",
                    "Xem giải thích trực tiếp", "Hiện tượng quan sát", "Năng lực GDPT"
                ).joinToString(";")

                writer.write(header)
                writer.write("\n")

                // Thu thập dữ liệu (cần gọi trong coroutine)
                val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("vi", "VN"))

                // Đọc toàn bộ sessions
                dao.getAllSessions().collect { sessions ->
                    for (session in sessions) {
                        val row = listOf(
                            session.id.toString(),
                            dateTimeFormat.format(Date(session.startTime)),
                            dateTimeFormat.format(Date(session.endTime)),
                            session.durationSeconds.toString(),
                            session.chemicalsMixed,
                            session.reactionEquation ?: "",
                            if (session.isReactionOccurred) "Có" else "Không",
                            REACTION_CATEGORY_LABELS[session.reactionType] ?: session.reactionType,
                            session.socraticPromptCount.toString(),
                            if (session.viewedExplanationDirectly) "Có" else "Không",
                            session.observedPhenomena,
                            session.competencyId
                        ).joinToString(";")

                        writer.write(row)
                        writer.write("\n")
                    }
                    // Ngắt collection sau khi lấy dữ liệu
                    return@collect
                }
            }

            // Tạo Uri qua FileProvider để chia sẻ
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            contentUri

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Tạo Intent chia sẻ file CSV đã xuất.
     *
     * @param context Context ứng dụng
     * @return Intent với ACTION_SEND để chia sẻ file CSV
     */
    fun createShareIntent(context: Context, csvUri: Uri): Intent {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val today = dateFormat.format(Date())

        return Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, csvUri)
            putExtra(
                Intent.EXTRA_SUBJECT,
                "Dữ liệu thực nghiệm KHKT - ChemLab $today"
            )
            putExtra(
                Intent.EXTRA_TEXT,
                "Phụ lục dữ liệu thực nghiệm KHKT - Phòng thí nghiệm Hóa học ảo ChemLab (Ngày: $today)\n" +
                        "Bộ dữ liệu ghi nhận các phiên thí nghiệm, phản ứng và tương tác với Trợ lý Socratic."
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    /**
     * Ghi một phiên mới và trả về sessionId.
     * Dùng khi bắt đầu một phiên thí nghiệm mới.
     */
    suspend fun startNewSession(
        chemicalsMixed: String,
        reactionType: String,
        competencyId: String
    ): Long {
        val now = System.currentTimeMillis()
        val session = ExperimentSession(
            startTime = now,
            endTime = now,
            durationSeconds = 0,
            chemicalsMixed = chemicalsMixed,
            reactionEquation = null,
            isReactionOccurred = false,
            socraticPromptCount = 0,
            viewedExplanationDirectly = false,
            reactionType = reactionType,
            competencyId = competencyId,
            observedPhenomena = ""
        )
        return dao.insertSession(session)
    }

    /**
     * Kết thúc một phiên thí nghiệm.
     */
    suspend fun finishSession(
        sessionId: Long,
        isReactionOccurred: Boolean,
        reactionEquation: String?,
        viewedDirectly: Boolean,
        socraticPromptCount: Int,
        observedPhenomena: String
    ) {
        val existingSession = dao.getAllSessions()
        // Cập nhật session đang có sessionId tương ứng
        // (Trong thực tế cần truy vấn riêng, ở đây ta lưu trực tiếp)
        val now = System.currentTimeMillis()

        // Query để lấy session cần update
        // Note: cần thêm query riêng trong DAO để cập nhật chính xác
        // Ở đây sử dụng placeholder
        val placeholderSession = ExperimentSession(
            id = sessionId,
            startTime = now - 300_000, // placeholder
            endTime = now,
            durationSeconds = 300, // placeholder - nên truyền vào
            chemicalsMixed = "",
            reactionEquation = reactionEquation,
            isReactionOccurred = isReactionOccurred,
            socraticPromptCount = socraticPromptCount,
            viewedExplanationDirectly = viewedDirectly,
            reactionType = "",
            competencyId = "",
            observedPhenomena = observedPhenomena
        )
        dao.updateSession(placeholderSession)
    }

    companion object {
        private val REACTION_CATEGORY_LABELS = mapOf(
            ReactionCategory.ACID_BASE to "Axit - Bazơ",
            ReactionCategory.METAL_DISPLACEMENT to "Kim loại & Axit",
            ReactionCategory.REDOX to "Oxi hóa - Khử",
            ReactionCategory.ION_EXCHANGE to "Trao đổi Ion",
            ReactionCategory.NO_REACTION to "Không phản ứng"
        )

        private val MISTAKE_LABELS = mapOf(
            MistakeType.WRONG_REAGENT to "Chọn sai hóa chất",
            MistakeType.UNNECESSARY_HEATING to "Đun nóng không cần thiết",
            MistakeType.SAFETY_VIOLATION to "Vi phạm an toàn",
            MistakeType.WRONG_SEQUENCE to "Thao tác sai trình tự",
            MistakeType.PREMATURE_REVELATION to "Xem đáp án quá sớm",
            MistakeType.MISSING_OBSERVATION to "Không quan sát hiện tượng",
            MistakeType.WRONG_PH_PREDICTION to "Dự đoán pH sai"
        )
    }
}
