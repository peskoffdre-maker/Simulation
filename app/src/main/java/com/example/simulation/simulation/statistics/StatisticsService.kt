package com.example.simulation.simulation.statistics

import com.example.simulation.simulation.Diets
import com.example.simulation.simulation.World

class StatisticsService(private val world: World) {
    fun printSimulationStateIntoConsole() {
        val averageTicksSurvived = world.positions.keys.map { world.histories[it]?.ticksSurvived ?: 0 }.average()
        val averageFoodConsumed = world.positions.keys.map { world.histories[it]?.foodConsumed ?: 0 }.average()
        val averageDistanceMoved = world.positions.keys.map { world.histories[it]?.distanceMoved ?: 0f }.average()
        val averageEnergySpent = world.positions.keys.map { world.histories[it]?.energySpent ?: 0f }.average()
        val averageEnergyGained = world.positions.keys.map { world.histories[it]?.energyGained ?: 0 }.average()
        val herbivores = world.diets.keys.map {world.diets[it]?.diet}.count() {it == Diets.HERBIVORE }
        val carnivores = world.diets.keys.map {world.diets[it]?.diet}.count() {it == Diets.CARNIVORE }
        val omnivores = world.diets.keys.map {world.diets[it]?.diet}.count() {it == Diets.OMNIVORE }
        val generations = world.histories.keys.maxOfOrNull { world.histories[it]?.generation ?: 0 }
        val food = world.foodTags.count()

        println("Average ticks: $averageTicksSurvived")
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
}