package com.example.simulation.simulation.statistics

import com.example.simulation.simulation.Diets
import com.example.simulation.simulation.World
import java.io.File
import kotlin.math.sqrt

// Used in development stage. Remove later
class StatisticsService(private val world: World) {
    fun printSimulationStateIntoConsole() {
        val averageFoodConsumed = world.positions.keys.map { world.histories[it]?.foodConsumed ?: 0 }.average()
        val averageDistanceMoved = world.positions.keys.map { world.histories[it]?.distanceMoved ?: 0f }.average()
        val averageEnergySpent = world.positions.keys.map { world.histories[it]?.energySpent ?: 0f }.average()
        val averageEnergyGained = world.positions.keys.map { world.histories[it]?.energyGained ?: 0 }.average()
        val herbivores = world.diets.keys.map {world.diets[it]?.diet}.count() {it == Diets.HERBIVORE }
        val carnivores = world.diets.keys.map {world.diets[it]?.diet}.count() {it == Diets.CARNIVORE }
        val omnivores = world.diets.keys.map {world.diets[it]?.diet}.count() {it == Diets.OMNIVORE }
        val generations = world.generations.keys.maxOfOrNull { world.generations[it]?.generation ?: 0 }
        val food = world.foodTags.count()

        println("Average food consumed: $averageFoodConsumed, " +
                "average energy gained: $averageEnergyGained, " +
                "average energy gained per distance: ${averageEnergyGained / averageDistanceMoved}")
        println("Average distance moved: $averageDistanceMoved, " +
                "average energy spend: $averageEnergySpent, " +
                "average energy spent per distance: ${averageEnergySpent / averageDistanceMoved}")
        println("Herbivores: $herbivores, carnivores: $carnivores, omnivores: $omnivores")
        println("Generations: $generations")
        println("Number of food: $food")
    }

    fun printStatistic(file: File) {

        file.printWriter().use { writer ->
            writer.println(world.rng)
            world.traitsPerGeneration.forEach { (key, value) ->
                writer.println(key)
                writer.printf(
                    "%-8s %-8s %-8s %-8s %-12s %-12s %-12s %-8s %-8s %-8s %-8s %-10s %-8s%n",
                    "Gen","Size","Males","Females", "Herbivores", "Carnivores", "Omnivores","Min","Max","Mean","Median","StdDev","CV"
                )
                value.forEach { (generation, traits) ->

                    val count = traits.values.size
                    val mean = traits.values.average()

                    val sorted = traits.values.sorted()
                    val median = sorted[sorted.size / 2]

                    val females = world.sexesPerGeneration[generation]?.values?.count { it } ?: 0
                    val males = world.sexesPerGeneration[generation]?.values?.count { !it } ?: 0

                    val herbivores = world.dietsPerGeneration[generation]?.values?.count {it == Diets.HERBIVORE } ?: 0
                    val carnivores = world.dietsPerGeneration[generation]?.values?.count {it == Diets.CARNIVORE } ?: 0
                    val omnivores = world.dietsPerGeneration[generation]?.values?.count {it == Diets.OMNIVORE } ?: 0

                    val variance = traits.values.map { (it - mean) * (it - mean) }.average()
                    val stdDev = sqrt(variance)
                    val cv = stdDev / mean

                    val min = traits.values.min()
                    val max = traits.values.max()

                    writer.printf(
                        "%-8d %-8d %-8d %-8d %-12d %-12d %-12d %-8.2f %-8.2f %-8.2f %-8.2f %-10.2f %-8.2f%n",
                        generation,
                        count,
                        males,
                        females,
                        herbivores,
                        carnivores,
                        omnivores,
                        min,
                        max,
                        mean,
                        median,
                        stdDev,
                        cv
                    )
                }
            }
        }
    }
}