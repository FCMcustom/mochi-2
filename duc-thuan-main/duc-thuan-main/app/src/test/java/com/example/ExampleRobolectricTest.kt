package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.BktEngine
import com.example.engine.ChemicalEngine
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app_name string from context matches Smart ChemLab`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Smart ChemLab", appName)
  }

  @Test
  fun `test Zn plus HCl reaction outcome`() {
    val outcome = ChemicalEngine.evaluateReaction(SubstanceCatalog.ZINC, SubstanceCatalog.HYDROCHLORIC_ACID)
    assertEquals("H2", outcome.gasFormula)
    assertEquals(ReactionType.METAL_DISPLACEMENT, outcome.reactionType)
    assertTrue(outcome.deltaH < 0f) // Exothermic
    assertTrue(outcome.balancedEquation.contains("Zn") && outcome.balancedEquation.contains("HCl"))
  }

  @Test
  fun `test Cu plus HCl negative control test`() {
    val outcome = ChemicalEngine.evaluateReaction(SubstanceCatalog.COPPER, SubstanceCatalog.HYDROCHLORIC_ACID)
    assertEquals(ReactionType.NO_REACTION, outcome.reactionType)
    assertNull(outcome.gasFormula)
    assertNull(outcome.precipitateFormula)
  }

  @Test
  fun `test BKT probability update on correct observation`() {
    val comp = CompetencyRegistry.METAL_SERIES
    val initP = 0.25f
    val nextP = BktEngine.updateMastery(comp, initP, isCorrect = true)
    assertTrue("Next mastery should be higher on correct observation", nextP > initP)
  }

  @Test
  fun `test statistical analysis engine computes Cohen d and t-statistic`() {
    val samples = listOf(
      StudentSample("E1", "EXPERIMENTAL", preTestScore = 5.0f, postTestScore = 8.5f, interactionCount = 15),
      StudentSample("E2", "EXPERIMENTAL", preTestScore = 5.5f, postTestScore = 9.0f, interactionCount = 18),
      StudentSample("C1", "CONTROL", preTestScore = 5.0f, postTestScore = 6.2f, interactionCount = 5),
      StudentSample("C2", "CONTROL", preTestScore = 5.5f, postTestScore = 6.4f, interactionCount = 6)
    )
    val stats = ResearchAnalysisEngine.calculateResearchStats(samples)
    assertEquals(4, stats.totalN)
    assertEquals(2, stats.expN)
    assertEquals(2, stats.ctlN)
    assertTrue(stats.postMeanExp > stats.postMeanCtl)
    assertTrue(stats.cohenD > 0.8f)
  }
}
