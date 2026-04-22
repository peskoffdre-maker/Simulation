package com.example.simulation.simulation.factories

import com.example.simulation.simulation.Genome
import com.example.simulation.simulation.GeneArray
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign


/*
    genes:
        0 MuscleDensity (influence +power, +speed, +metabolism, +reproduction energy cost)
        1 MuscleFibersSpeed (influence +speed, +power, +metabolism, +maxAge)
        2 TendonElasticity (influence +speed, +power, -metabolism)
        3 BoneStrength (influence +size, +power, -speed, +maxAge)
        4 BodyMassDistribution (influence +power, +speed, +metabolism)
        5 MaxBodyGrowth (influence +size, +growthRate, +reproduction energy cost)
        6 MetabolicRate (influence +speed, +growth rate, +reproduction rate, -maxAge)
        7 EnergyStorage (influence -metabolism, -speed)
        8 EnergyEfficiency (influence +speed, +growth rate, -reproduction energy cost)
        9 VisualAcuity (influence +perception, +metabolism)
        10 MotionDetection (influence +perception, +metabolism)
        11 SensoryRange (influence +perception, +metabolism)
        12 NeuralProcessingSpeed (influence +perception, +speed)
        13 GrowthHormoneLevel (influence +growth rate, +size, -age)
        14 ReproductiveHormoneLevel (influence -reproductionCD, +fertility, -maxAge)
        15 LongevityRegulation (influence -fertility, +reproduction energy cost)
        16 MaturationTiming (influence +max age, -growth rate, -reproduction rate)
        17 ParentalInvestment (influence +maturingRation, +size)
        18 AgingSpeed (influence -aging ration, max age)
 */

data class Traits(
    val metabolism: Float, // influenced by 0 1 2 4 7 9 10 11
    val perception: Float, // influenced by 9 10 11 12
    val size: Float, // influenced by 3 5 13 17
    val growthRate: Float, // influenced by 5 6 8 13 16
    val reproductionCD: Int, // influenced by 6 16 14
    val reproductionEnergyCost: Int, // influenced by 0 5 8 15
    val reproductionEnergyThreshold: Int, // influenced by 14 8
    val fertility: Float, // influenced by 14 15
    val maxAge: Int, // influenced by 1 3 6 13 14 16 18
    val maturingRatio: Float, // influenced by 17
    val agingRatio: Float, // influenced by 18
    val speed: Float, // influenced by 0 1 2 3 4 6 7 8 12 18
    val power: Float, // influenced by 0 1 2 3 4
)

class TraitFactory() {

    fun generateTraitsFromGenome(genome: Genome) : Traits {
        val genes = genome.genes
        return Traits(
            metabolism = calculateMetabolism(genes),
            perception = calculatePerception(genes),
            size = calculateSize(genes),
            growthRate = calculateGrowthRate(genes),
            reproductionCD = calculateReproductionCD(genes),
            reproductionEnergyCost = calculateReproductionEnergyCost(genes),
            reproductionEnergyThreshold = calculateReproductionEnergyThreshold(genes),
            fertility = calculateFertility(genes),
            maxAge = calculateMaxAge(genes),
            maturingRatio = calculateMaturingRatio(genes),
            agingRatio = calculateAgingRatio(genes),
            speed = calculateSpeed(genes),
            power = calculatePower(genes)
        )
    }

    private fun geneEffect(g: Float, k: Float = 0.5f, n: Float = 2f): Float {
        val a = abs(g).pow(n)
        val effect = a / (a + k.pow(n))
        return effect * sign(g)
    }

    private fun normalize(x: Float): Float {
        val y = x / (1f + abs(x))
        return (y + 1f) * 0.5f
    }

    private fun calculateMetabolism(genes: GeneArray): Float {

        val genesMap = listOf(
            geneEffect(genes[0]) to 1f,
//            geneEffect(genes[1])to 0.12f,
//            geneEffect(genes[2])to -0.14f,
//            geneEffect(genes[4])to 0.18f,
//            geneEffect(genes[7])to -0.16f,
//            geneEffect(genes[9])to 0.05f,
//            geneEffect(genes[10]) to 0.06f,
//            geneEffect(genes[11]) to 0.07f,
        )

        // positive
        var result = computeResult(genesMap)

        // TODO synergy

        // TODO antagonism

        result = (result + 1f) / 2f

        return lerp(1f, 10f, result)
    }

    private fun calculatePower(genes: GeneArray): Float {

        val genesMap = listOf (
            geneEffect(genes[0]) to 1f,
//            geneEffect(genes[1]) to 0.25f,
//            geneEffect(genes[2]) to 0.15f,
//            geneEffect(genes[3]) to 0.20f,
//            geneEffect(genes[4]) to 0.10f,
        )

        var result = computeResult(genesMap)

        // synergy
//        result += 0.2f * genes[0] * genes[3]

        // antagonism
//        result -= 0.1f * genes[1] * genes[2]

        result = (result + 1f) / 2f

        return lerp(100f, 1000f, result)
    }

    private fun calculatePerception(genes: GeneArray): Float {

        val genesMap = listOf(
            geneEffect(genes[9]) to 1f,
//            geneEffect(genes[10]) to 0.15f,
//            geneEffect(genes[11]) to 0.35f,
//            geneEffect(genes[12]) to 0.15f,
        )

        var result = computeResult(genesMap)

        // TODO synergy

        // TODO antagonism

//        result = normalize(result)
        result = (result + 1f) / 2f
        return lerp(100f, 500f, result)
    }

    private fun calculateSize(genes: GeneArray): Float {
        val genesMap = listOf(
//            geneEffect(genes[3])to 0.35f,
            geneEffect(genes[5]) to 1f,
//            geneEffect(genes[13]) to 1f,
//            geneEffect(genes[17]) to 0.15f,
        )

        var result = computeResult(genesMap)

        // TODO synergy

        // TODO antagonism
        result = (result + 1f) / 2f
//        result = result.coerceIn(0f, 1f)
        return lerp(3f, 20f, result)
    }

    private fun calculateGrowthRate(genes: GeneArray): Float {
        val genesMap = listOf(
//            geneEffect(genes[5])to 0.35f,
//            geneEffect(genes[6]) to 0.20f,
//            geneEffect(genes[8]) to 0.10f,
            geneEffect(genes[13]) to 1f,
//            geneEffect(genes[16]) to -0.15f,
        )

        var result = computeResult(genesMap)

        // TODO synergy

        // TODO antagonism

        result = (result + 1f) / 2f
        return lerp(0.0003f, 0.003f, result)
    }

    private fun calculateReproductionCD(genes: GeneArray): Int {
        val genesMap = listOf(
//            geneEffect(genes[6])to 0.45f,
            geneEffect(genes[14]) to 1f,
//            geneEffect(genes[16]) to -0.25f,
        )

        var result = computeResult(genesMap)

        // TODO synergy

        // TODO antagonism

        result = (result + 1f) / 2f

        return lerp(1000, 2000, result)
    }

    private fun calculateReproductionEnergyCost(genes: GeneArray): Int {
        val genesMap = listOf(
//            geneEffect(genes[0])to 0.35f,
            geneEffect(genes[5]) to 1f,
//            geneEffect(genes[8]) to -0.35f,
//            geneEffect(genes[15]) to 0.05f,
        )

        var result = computeResult(genesMap)

        // TODO synergy

        // TODO antagonism

        result = (result + 1f) / 2f

        return lerp(1000, 2000, result)
    }

    private fun calculateReproductionEnergyThreshold(genes: GeneArray): Int {
        val genesMap = listOf(
            geneEffect(genes[8]) to 1f,
//            geneEffect(genes[14]) to 1f,
        )

        var result = computeResult(genesMap)

        // TODO synergy

        // TODO antagonism

        result = (result + 1f) / 2f
        return lerp(1500, 3000, result)
    }

    private fun calculateFertility(genes: GeneArray): Float {
        val genesMap = listOf(
            geneEffect(genes[14]) to 1f,
//            geneEffect(genes[15]) to 0.35f,
        )

        var result = computeResult(genesMap)

        // TODO synergy

        // TODO antagonism

        result = (result + 1f) / 2f
        return lerp(0.5f, 2f, result)
    }

    private fun calculateMaxAge(genes: GeneArray): Int {
        val genesMap = listOf(
//            geneEffect(genes[1]) to 0.1f,
//            geneEffect(genes[3]) to 0.15f,
//            geneEffect(genes[6]) to -0.1f,
//            geneEffect(genes[13]) to -0.15f,
//            geneEffect(genes[14]) to -0.1f,
//            geneEffect(genes[16]) to 0.2f,
            geneEffect(genes[18]) to 1f,
        )

        var result = computeResult(genesMap)

        // TODO synergy

        // TODO antagonism

        result = (result + 1f) / 2f
        return lerp(3600, 7200, result)
    }

    private fun calculateMaturingRatio(genes: GeneArray): Float {
        val genesMap = listOf(
            geneEffect(genes[17])to 1f,
        )

        var result = computeResult(genesMap)

        // TODO synergy

        // TODO antagonism

        result = (result + 1f) / 2f
        return lerp(0.15f, 0.45f, result)
    }

    private fun calculateAgingRatio(genes: GeneArray): Float {
        val genesMap = listOf(
            geneEffect(genes[18])to 1f,
        )

        var result = computeResult(genesMap)

        // TODO synergy

        // TODO antagonism

        result = (result + 1f) / 2f
        return lerp(0.55f, 0.85f, result)
    }

    private fun calculateSpeed(genes: GeneArray): Float {
        val genesMap = listOf(
//            geneEffect(genes[0]) to 0.14f,
            geneEffect(genes[1]) to 1f,
//            geneEffect(genes[2]) to 0.1f,
//            geneEffect(genes[3]) to -0.16f,
//            geneEffect(genes[4]) to 0.08f,
//            geneEffect(genes[6]) to 0.09f,
//            geneEffect(genes[7]) to -0.14f,
//            geneEffect(genes[8]) to 0.1f,
//            geneEffect(genes[12]) to 0.05f,
        )

        var result = computeResult(genesMap)

        // TODO synergy

        // TODO antagonism

        result = (result + 1f) / 2f
        return lerp(0.1f, 2f, result)
    }


    private fun computeResult(pairs: List<Pair<Float, Float>>): Float {
        var result = 0f
        for ((effect, weight) in pairs) {
            result += effect * weight
        }
        return result
    }

    private fun lerp(min: Float, max: Float, t: Float): Float {
        return min + (max - min) * t
    }
    private fun lerp(min: Int, max: Int, t: Float): Int {
        return (min.toFloat() + (max.toFloat() - min.toFloat()) * t).toInt()
    }
}