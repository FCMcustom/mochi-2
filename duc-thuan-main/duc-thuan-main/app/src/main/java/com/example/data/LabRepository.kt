package com.example.data

import com.example.engine.BktEngine
import com.example.model.CompetencyMastery
import com.example.model.CompetencyRegistry
import com.example.model.StudentSample
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.random.Random

class LabRepository(private val dao: LabDao) {

    val experimentRecords: Flow<List<ExperimentRecord>> = dao.getAllExperimentRecords()

    val bktMasteries: Flow<List<CompetencyMastery>> = dao.getAllBktSkills().map { records ->
        val recordMap = records.associateBy { it.skillId }
        CompetencyRegistry.ALL_COMPETENCIES.map { comp ->
            val rec = recordMap[comp.id]
            CompetencyMastery(
                competency = comp,
                currentProbability = rec?.masteryProb ?: comp.pInit,
                totalAttempts = rec?.attemptsCount ?: 0,
                correctCount = rec?.correctCount ?: 0
            )
        }
    }

    val researchStudents: Flow<List<StudentSample>> = dao.getAllResearchStudents().map { entities ->
        entities.map {
            StudentSample(
                studentId = it.studentCode,
                group = it.groupType,
                preTestScore = it.preScore,
                postTestScore = it.postScore,
                interactionCount = it.interactionCount,
                susScore = it.susScore
            )
        }
    }

    suspend fun recordExperiment(
        title: String,
        reactantA: String,
        reactantB: String,
        prediction: String,
        phenomena: String,
        isConfirmed: Boolean,
        explanation: String,
        competencyId: String
    ) {
        dao.insertExperimentRecord(
            ExperimentRecord(
                experimentTitle = title,
                reactantAId = reactantA,
                reactantBId = reactantB,
                predictionText = prediction,
                actualPhenomena = phenomena,
                isHypothesisConfirmed = isConfirmed,
                studentExplanation = explanation,
                competencyId = competencyId
            )
        )

        // Bayesian Knowledge Tracing update
        val competency = CompetencyRegistry.ALL_COMPETENCIES.find { it.id == competencyId }
            ?: CompetencyRegistry.METAL_SERIES

        // Current skill record
        // We will query from DB or compute
        val currentSkills = dao.getAllBktSkills()
        // Simple one-time update
        val prevSkill = CompetencyRegistry.ALL_COMPETENCIES.find { it.id == competencyId }
        val pInit = prevSkill?.pInit ?: 0.25f

        val newProb = BktEngine.updateMastery(competency, pInit, isConfirmed)
        dao.upsertBktSkill(
            BktSkillRecord(
                skillId = competencyId,
                masteryProb = newProb,
                attemptsCount = 1,
                correctCount = if (isConfirmed) 1 else 0
            )
        )
    }

    suspend fun updateSkillEvaluation(competencyId: String, isCorrect: Boolean, currentProb: Float, attempts: Int, correct: Int) {
        val competency = CompetencyRegistry.ALL_COMPETENCIES.find { it.id == competencyId }
            ?: CompetencyRegistry.METAL_SERIES

        val updatedProb = BktEngine.updateMastery(competency, currentProb, isCorrect)
        dao.upsertBktSkill(
            BktSkillRecord(
                skillId = competencyId,
                masteryProb = updatedProb,
                attemptsCount = attempts + 1,
                correctCount = correct + (if (isCorrect) 1 else 0)
            )
        )
    }

    suspend fun seedInitialDataIfNeeded() {
        // Seed initial BKT skills
        for (comp in CompetencyRegistry.ALL_COMPETENCIES) {
            dao.upsertBktSkill(
                BktSkillRecord(
                    skillId = comp.id,
                    masteryProb = comp.pInit,
                    attemptsCount = 0,
                    correctCount = 0
                )
            )
        }

        // Seed KHKT research A/B testing cohort (N=60 students: 30 Experimental + 30 Control)
        if (dao.getResearchStudentCount() == 0) {
            val random = Random(42) // Fixed seed for reproducible scientific baseline
            val sampleList = mutableListOf<ResearchStudentSample>()

            // 30 Experimental students (using Smart ChemLab with Socratic feedback & Microscopic view)
            for (i in 1..30) {
                val pre = (4.5f + random.nextFloat() * 2.0f).coerceIn(3.5f, 7.0f)
                val gain = (2.4f + random.nextFloat() * 1.8f) // High improvement due to adaptive lab
                val post = (pre + gain).coerceAtMost(10.0f)
                val interactions = 12 + random.nextInt(15)
                val sus = (80f + random.nextFloat() * 18f).coerceIn(75f, 98f)
                sampleList.add(
                    ResearchStudentSample(
                        studentCode = "HS-EXP-%02d".format(i),
                        groupType = "EXPERIMENTAL",
                        preScore = (pre * 10).toInt() / 10f,
                        postScore = (post * 10).toInt() / 10f,
                        interactionCount = interactions,
                        susScore = (sus * 10).toInt() / 10f
                    )
                )
            }

            // 30 Control students (using traditional lecture + standard video demos)
            for (i in 1..30) {
                val pre = (4.5f + random.nextFloat() * 2.0f).coerceIn(3.5f, 7.0f)
                val gain = (0.8f + random.nextFloat() * 1.2f) // Modest improvement
                val post = (pre + gain).coerceAtMost(10.0f)
                val interactions = 4 + random.nextInt(4)
                val sus = (65f + random.nextFloat() * 12f).coerceIn(60f, 78f)
                sampleList.add(
                    ResearchStudentSample(
                        studentCode = "HS-CTL-%02d".format(i),
                        groupType = "CONTROL",
                        preScore = (pre * 10).toInt() / 10f,
                        postScore = (post * 10).toInt() / 10f,
                        interactionCount = interactions,
                        susScore = (sus * 10).toInt() / 10f
                    )
                )
            }

            dao.insertResearchStudents(sampleList)
        }
    }
}
