package com.example.simulation.simulation.utils

import com.example.simulation.simulation.Genome
import com.example.simulation.simulation.World
import kotlin.math.abs
import kotlin.math.sqrt

class SpeciesCompatibilityService(world: World) {

    private val compatibilityLimit = 100f

    fun checkIfCompatible(genome1: Genome, genome2: Genome) : Boolean {
        var sum = 0f
        for (i in genome1.genes.indices) {
            val d = genome1.genes[i] - genome2.genes[i]
            sum += d * d
        }
        sum = sqrt(sum)
        return sum < compatibilityLimit

    }
}