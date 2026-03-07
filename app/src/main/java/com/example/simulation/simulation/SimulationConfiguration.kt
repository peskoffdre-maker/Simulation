package com.example.simulation.simulation

import kotlin.random.Random

object SimulationConfiguration {
    val initialTimeModifier = 1
    val creatureInitialVelocity = 4
    val maxSpeed = 2f

    // Energy
    val minCreatureInitialEnergy = 500
    val maxCreatureInitialEnergy = 1000

    val minFoodInitialEnergy = 400
    val maxPlantInitialEnergy = 800

    // Detection

    val minCreatureInitialDetection = 1
    val maxCreatureInitialDetection = 5
    val creatureDetectionMultiplier = 100f
    val cellSize = minCreatureInitialDetection * creatureDetectionMultiplier * 2

    // Size

    val creatureInitialSizeMultiplier = 10f

    // Reproduction

    val minCreatureInitialReproductionEnergyThreshold = 700
    val maxCreatureInitialReproductionEnergyThreshold = 1200

    val creatureInitialReproductionCost = 500

    val minCreatureInitialReproductionCD = 720
    val maxCreatureInitialReproductionCD = 1440

    val spawnOffset = (Random.nextFloat() * 10f - 5f)

    // Age

    val minCreatureInitialMaxAge = 720
    val maxCreatureInitialMaxAge = 1440

    val minPlantInitialMaxAge = 360
    val maxPlantInitialMaxAge = 1080

    val maxPlantCooldown = 60

}