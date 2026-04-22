package com.example.simulation.simulation.systems

import com.example.simulation.simulation.World
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.math.sqrt
import kotlin.reflect.KClass

class StatisticSystem(private val world: World): System {

    override fun update() {
        getPerceptionsData()
        getSpeedsData()
        getMaxSizesData()
        getMaxAgesData()
        getSexesData()
        getAgingData()
        getMaturingData()
        getFertilityData()
        getReproductionCostData()
        getReproductionThresholdData()
        getMetabolismData()
        getDietsData()
    }

    override fun reads(): List<KClass<*>> {
        return listOf()
    }

    override fun writes(): List<KClass<*>> {
        return listOf()
    }

    private fun getPerceptionsData() {
        for ((id, perception) in world.perceptions) {
            val generation = world.generations[id] ?: continue
            world.preceptionsPerGeneration.getOrPut(generation.generation)
            { mutableMapOf() }.put(id, perception.detectionRadius)
        }
    }

    private fun getSpeedsData() {
        for ((id, speed) in world.speeds) {
            val generation = world.generations[id] ?: continue
            world.speedsPerGeneration.getOrPut(generation.generation)
            { mutableMapOf() }.put(id, speed.speed)
        }
    }

    private fun getMaxAgesData() {
        for ((id, age) in world.ages) {
            val generation = world.generations[id] ?: continue
            world.maxAgePerGeneration.getOrPut(generation.generation)
            { mutableMapOf() }.put(id, age.maxAge.toFloat())
        }
    }

    private fun getMaxSizesData() {
        for ((id, size) in world.sizes) {
            val generation = world.generations[id] ?: continue
            world.maxSizePerGeneration.getOrPut(generation.generation)
            { mutableMapOf() }.put(id, size.maxSize)
        }
    }

    private fun getSexesData() {
        for ((id, sex) in world.sexes) {
            val generation = world.generations[id] ?: continue
            world.sexesPerGeneration.getOrPut(generation.generation)
            { mutableMapOf() }.put(id, sex.value)
        }
    }

    private fun getAgingData() {
        for ((id, agingRation) in world.agingRatios) {
            val generation = world.generations[id] ?: continue
            world.agingRatioPerGeneration.getOrPut(generation.generation)
            { mutableMapOf() }.put(id, agingRation.value)
        }
    }

    private fun getMaturingData() {
        for ((id, maturingRatio) in world.maturingRatios) {
            val generation = world.generations[id] ?: continue
            world.maturingRatioPerGeneration.getOrPut(generation.generation)
            { mutableMapOf() }.put(id, maturingRatio.value)
        }
    }

    private fun getFertilityData() {
        for ((id, fertility) in world.fertilities) {
            val generation = world.generations[id] ?: continue
            world.fertilityPerGeneration.getOrPut(generation.generation)
            { mutableMapOf() }.put(id, fertility.value)
        }
    }

    private fun getReproductionCostData() {
        for ((id, reproductionCost) in world.reproductionEnergyCosts) {
            val generation = world.generations[id] ?: continue
            world.reproductionCostPerGeneration.getOrPut(generation.generation)
            { mutableMapOf() }.put(id, reproductionCost.value.toFloat())
        }
    }

    private fun getReproductionThresholdData() {
        for ((id, reproductionThreshold) in world.reproductionEnergyThresholds) {
            val generation = world.generations[id] ?: continue
            world.reproductionThresholdPerGeneration.getOrPut(generation.generation)
            { mutableMapOf() }.put(id, reproductionThreshold.value.toFloat())
        }
    }

    private fun getMetabolismData() {
        for ((id, energy) in world.energies) {
            val generation = world.generations[id] ?: continue
            world.metabolismPerGeneration.getOrPut(generation.generation)
            { mutableMapOf() }.put(id, energy.metabolism.toFloat())
        }
    }

    private fun getDietsData() {
        for ((id, diet) in world.diets) {
            val generation = world.generations[id] ?: continue
            world.dietsPerGeneration.getOrPut(generation.generation)
            { mutableMapOf() }.put(id, diet.diet)
        }
    }
}