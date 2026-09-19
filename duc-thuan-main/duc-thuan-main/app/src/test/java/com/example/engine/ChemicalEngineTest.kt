package com.example.engine

import com.example.model.ChemicalCatalog
import org.junit.Assert.*
import org.junit.Test

class ChemicalEngineTest {

    @Test
    fun `Fe reacts with HCl producing hydrogen gas`() {
        val result = ChemicalEngine.mix(ChemicalCatalog.Fe, ChemicalCatalog.HCl)
        
        assertEquals("Fe + 2HCl → FeCl₂ + H₂↑", result.equation)
        assertEquals("H₂", result.gasReleased)
        assertTrue(result.temperatureChange > 0)
        assertTrue(result.explanation.contains("khí H₂"))
    }

    @Test
    fun `BaCl2 reacts with H2SO4 producing white precipitate`() {
        val result = ChemicalEngine.mix(ChemicalCatalog.BaCl2, ChemicalCatalog.H2SO4)
        
        assertEquals("BaCl₂ + H₂SO₄ → BaSO₄↓ + 2HCl", result.equation)
        assertEquals("BaSO₄", result.precipitate)
        assertFalse(result.gasReleased != null)
    }

    @Test
    fun `NaOH with Phenolphthalein turns pink`() {
        val result = ChemicalEngine.mix(ChemicalCatalog.NaOH, ChemicalCatalog.Phenolphthalein)
        
        assertTrue(result.colorChange == 0xFFFF69B4.toLong())
        assertTrue(result.explanation.contains("bazơ"))
    }

    @Test
    fun `AgNO3 reacts with NaCl producing AgCl precipitate`() {
        val result = ChemicalEngine.mix(ChemicalCatalog.AgNO3, ChemicalCatalog.NaCl)
        
        assertEquals("AgNO₃ + NaCl → AgCl↓ + NaNO₃", result.equation)
        assertEquals("AgCl", result.precipitate)
    }

    @Test
    fun `Zn reacts with H2SO4 producing hydrogen`() {
        val result = ChemicalEngine.mix(ChemicalCatalog.Zn, ChemicalCatalog.H2SO4)
        
        assertEquals("Zn + H₂SO₄ → ZnSO₄ + H₂↑", result.equation)
        assertEquals("H₂", result.gasReleased)
        assertTrue(result.temperatureChange > 0)
    }

    @Test
    fun `CuSO4 reacts with NaOH producing Cu(OH)2 precipitate`() {
        val result = ChemicalEngine.mix(ChemicalCatalog.CuSO4, ChemicalCatalog.NaOH)
        
        assertEquals("CuSO₄ + 2NaOH → Cu(OH)₂↓ + Na₂SO₄", result.equation)
        assertEquals("Cu(OH)₂", result.precipitate)
    }

    @Test
    fun `HCl and NaOH neutralization reaction`() {
        val result = ChemicalEngine.mix(ChemicalCatalog.HCl, ChemicalCatalog.NaOH)
        
        assertEquals("HCl + NaOH → NaCl + H₂O", result.equation)
        assertEquals(6.8f, result.temperatureChange, 0.1f)
    }

    @Test
    fun `CaCO3 reacts with HCl producing CO2 gas`() {
        val result = ChemicalEngine.mix(ChemicalCatalog.CaCO3, ChemicalCatalog.HCl)
        
        assertEquals("CaCO₃ + 2HCl → CaCl₂ + CO₂↑ + H₂O", result.equation)
        assertEquals("CO₂", result.gasReleased)
    }

    @Test
    fun `Fe displaces Cu from CuSO4`() {
        val result = ChemicalEngine.mix(ChemicalCatalog.Fe, ChemicalCatalog.CuSO4)
        
        assertEquals("Fe + CuSO₄ → FeSO₄ + Cu↓", result.equation)
        assertEquals("Cu", result.precipitate)
    }

    @Test
    fun `Non-reactive pair returns no reaction`() {
        val result = ChemicalEngine.mix(ChemicalCatalog.NaCl, ChemicalCatalog.K2CrO4)
        
        assertTrue(result.equation.contains("Không xảy ra phản ứng"))
        assertTrue(result.temperatureChange == 0f)
    }

    @Test
    fun `Reaction order does not matter`() {
        val result1 = ChemicalEngine.mix(ChemicalCatalog.Fe, ChemicalCatalog.HCl)
        val result2 = ChemicalEngine.mix(ChemicalCatalog.HCl, ChemicalCatalog.Fe)
        
        assertEquals(result1.equation, result2.equation)
        assertEquals(result1.gasReleased, result2.gasReleased)
    }

    @Test
    fun `Available reactions list is not empty`() {
        val reactions = ChemicalEngine.getAvailableReactions()
        assertTrue(reactions.size >= 5)
    }
}
