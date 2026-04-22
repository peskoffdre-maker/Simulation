package com.example.simulation

import com.example.simulation.simulation.SimulationEngine
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import kotlin.random.Random

class EngineTests {
    @Test
    fun populationDoesNotImmediatelyCollapse() {

        val rng = Random(1)

        val engine = SimulationEngine(
            width = 1000f,
            height = 1000f,
            initialPopulation = 300,
            initialPlants = 400,
            rng
        )
        var ticks = 0
        while (engine.world.creatureTags.isNotEmpty() && ( engine.world.creatureTags.size < 10000 && ticks < 30000 )) {
            engine.tick()
            ticks++
        }

        val population = engine.world.creatureTags.size
        println(ticks)
        engine.statisticsService.printStatistic(File("stats_test"))
        assertTrue(population > 0)
    }
}