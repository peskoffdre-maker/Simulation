package com.example.simulation.simulation

import kotlin.random.Random

object SimulationConfiguration {
    val initialTimeModifier = 1
    val creatureInitialVelocity = 4

    // Energy
    val minCreatureInitialEnergy = 3000
    val maxCreatureInitialEnergy = 5000

    val minFoodInitialEnergy = 500

    val hungerStateEnergyThreshhold = 4000
    val roamingStateEnergyThreshhold = 5000

    // Detection

    val minCreatureInitialDetection = 1
    val creatureDetectionMultiplier = 100f
    val cellSize = minCreatureInitialDetection * creatureDetectionMultiplier * 2

    // Size

    val creatureInitialSizeMultiplier = 5f

    // Reproduction

    val spawnOffset = (0.5f * 10f - 5f)

    // Age
    val agingPerTick = 1

    val minPlantInitialMaxAge = 360
    val maxPlantInitialMaxAge = 10080

    val foodMaxAge = 720

    val maxPlantCooldown = 400
    val cooldownPerTick = 1


    // Zones
    val fertileZoneRadius = 300f
    val initialZonesCount = 3
    val zoneSpawnCD = 250

    // Mutation
    val mutationLowerLimit = 0.05
    val mutationUpperLimit = 0.15

}