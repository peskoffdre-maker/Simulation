package com.example.simulation.simulation

import androidx.compose.ui.graphics.Color
import com.example.simulation.simulation.factories.Offspring
import kotlin.random.Random
import kotlin.reflect.KClass

typealias EntityId = Int
typealias gene = Float
typealias GeneArray = FloatArray


class EntityManager {
    private var nextId = 0
    fun create(): EntityId = nextId++
}

// list of possible genes, that influence traits

abstract class MuscleDensity // +power, +speed, +energy consumption, +reproduction energy cost
abstract class MuscleFibersSpeed // <0.5 -speed, -power, -energy consumption, +max age, >0.5 opposite
abstract class TendonElasticity // +speed, +power, -energy consumption
abstract class BoneStrength // +size, +power, -speed, +max age
abstract class BodyMassDistribution // power, speed, energy consumption, <0.5 negative
abstract class MaxBodyGrowth // +size, +growth rate, +reproduction energy cost
abstract class MetabolicRate // +speed, +growth rate, +reproduction rate, -max age
abstract class EnergyStorage // -energy consumption, -speed
abstract class EnergyEfficiency // +speed, +growth rate, -reproduction energy cost
abstract class VisualAcuity // +perception, +energy consumption
abstract class MotionDetection // +perception, +energy consumption
abstract class SensoryRange // +perception, +energy consumption
abstract class NeuralProcessingSpeed // +perception, +speed
abstract class GrowthHormoneLevel // +growth rate, +size, -age
abstract class ReproductiveHormoneLevel // +reproduction, +fertility, -age
abstract class ParentalInvestment // -fertility, +reproduction energy cost
abstract class LongevityRegulation // +max age, -growth rate, -reproduction rate
abstract class MaturationTiming // +maturingRation, +size
abstract class AgingSpeed // -aging ration, max age

interface Component
data class Genome(val genes: GeneArray)

enum class Intents {
    ATTACK,
    EAT,
    REPRODUCE
}

data class PositionComponent(
    var x: Float,
    var y: Float
) : Component

data class VelocityComponent(
    var dx: Float,
    var dy: Float
) : Component


data class EnergyComponent(
    var currentEnergy: Float,
    val metabolism: Float, // genetic trait
) : Component

data class StateComponent(
    var state: CreatureStates
) : Component

data class PerceptionComponent(
    var detectionRadius: Float, // genetic trait
    var mateRadius: Float
) : Component

data class PowerComponent(
    var value: Float // genetic trait
)

data class SizeComponent(
    var value: Float,
    val maxSize: Float, // genetic trait
) : Component

data class GrowthRateComponent(
    val value: Float // genetic trait
)

data class DietComponent(
    val diet: Diets
) : Component

data class ReproductionComponent(
    var canReproduce: Boolean = false,
    var cooldown: Int,
    val maxCooldown: Int // genetic trait
) : Component

data class SexComponent(
    val value: Boolean // 1 - female, 0 - male; genetic trait
)

data class ReproductionEnergyThresholdComponent(
    val value: Int // genetic trait
)

data class ReproductionEnergyCostComponent(
    val value: Int // genetic trait
)

data class FertilityComponent(
    val value: Float // genetic trait
)

data class HistoryComponent(
    var foodConsumed: Int = 0,
    var ticksSurvived: Int = 0,
    var distanceMoved: Float = 0f,
    var energySpent: Float = 0f,
    var energyGained: Int = 0,
) : Component

data class AgeComponent(
    var value: Int,
    var agingStage: AgingStages = AgingStages.YOUNG,
    val maxAge: Int, // genetic trait
    val youngAdult: Int = 0,
    val adultOld: Int = 0,
) : Component

data class DecayComponent(
    var currentDecay: Int,
    val maxDecay: Int,
)

data class MaturingRatioComponent(
    val value: Float = 0f // genetic trait
)

data class AgingRatioComponent(
    val value: Float = 0f // genetic trait
)

data class PlantCooldownComponent(
    var maxCooldown: Int,
    var cooldown: Int
) : Component

data class FoodEnergyComponent(
    val value: Int
) : Component

data class TargetComponent(
    var targetId: EntityId? = null,
    var distance: Float? = null,
    var intent: Intents? = null
) : Component

data class FertileZoneComponent(
    val cx: Float,
    val cy: Float,
    val radius: Float,
    val weight: Float,
) : Component

data class SpeedComponent(
    val speed: Float, // genetic trait
) : Component

data class GenerationComponent(
    val generation: Int
) : Component

data class ColorComponent(
    val color: Color
) : Component

class World(val rng : Random) {
    val entityManager = EntityManager()
    val deathQ = ArrayDeque<EntityId>()
    val birthQ = ArrayDeque<Offspring>()

    val eventBus = EventBus()

    // Test
    val preceptionsPerGeneration = mutableMapOf<Int, MutableMap<EntityId, Float>>()
    val speedsPerGeneration = mutableMapOf<Int, MutableMap<EntityId, Float>>()
    val maxAgePerGeneration = mutableMapOf<Int, MutableMap<EntityId, Float>>()
    val agingRatioPerGeneration = mutableMapOf<Int, MutableMap<EntityId, Float>>()
    val maturingRatioPerGeneration = mutableMapOf<Int, MutableMap<EntityId, Float>>()
    val reproductionCostPerGeneration = mutableMapOf<Int, MutableMap<EntityId, Float>>()
    val reproductionThresholdPerGeneration = mutableMapOf<Int, MutableMap<EntityId, Float>>()
    val fertilityPerGeneration = mutableMapOf<Int, MutableMap<EntityId, Float>>()
    val maxSizePerGeneration = mutableMapOf<Int, MutableMap<EntityId, Float>>()
    val sexesPerGeneration = mutableMapOf<Int, MutableMap<EntityId, Boolean>>()
    val metabolismPerGeneration = mutableMapOf<Int, MutableMap<EntityId, Float>>()
    val dietsPerGeneration = mutableMapOf<Int, MutableMap<EntityId, Diets>>()

    val traitsPerGeneration = mapOf(
        "Reproduction Cost" to reproductionCostPerGeneration,
        "Reproduction Threshold" to reproductionThresholdPerGeneration,
        "Fertility" to fertilityPerGeneration,
        "Maturing ratio" to maturingRatioPerGeneration,
        "Aging ratio" to agingRatioPerGeneration,
        "Perception" to preceptionsPerGeneration,
        "Speed" to speedsPerGeneration,
        "Max age" to maxAgePerGeneration,
        "Max size" to maxSizePerGeneration,
        "Metabolism" to metabolismPerGeneration,
    )

    val creatureTags = mutableSetOf<EntityId>()
    val genomes = mutableMapOf<EntityId, Genome>()
    val positions = mutableMapOf<EntityId, PositionComponent>()
    val velocities = mutableMapOf<EntityId, VelocityComponent>()
    val energies = mutableMapOf<EntityId, EnergyComponent>()
    val states = mutableMapOf<EntityId, StateComponent>()
    val perceptions = mutableMapOf<EntityId, PerceptionComponent>()
    val sizes = mutableMapOf<EntityId, SizeComponent>()
    val diets = mutableMapOf<EntityId, DietComponent>()
    val reproductions = mutableMapOf<EntityId, ReproductionComponent>()
    val sexes = mutableMapOf<EntityId, SexComponent>()
    val reproductionEnergyThresholds = mutableMapOf<EntityId, ReproductionEnergyThresholdComponent>()
    val reproductionEnergyCosts = mutableMapOf<EntityId, ReproductionEnergyCostComponent>()
    val fertilities = mutableMapOf<EntityId, FertilityComponent>()
    val histories = mutableMapOf<EntityId, HistoryComponent>()
    val plantTags = mutableSetOf<EntityId>()
    val ages = mutableMapOf<EntityId, AgeComponent>()
    val maturingRatios = mutableMapOf<EntityId, MaturingRatioComponent>()
    val agingRatios = mutableMapOf<EntityId, AgingRatioComponent>()
    val growthRates = mutableMapOf<EntityId, GrowthRateComponent>()
    val speeds = mutableMapOf<EntityId, SpeedComponent>()
    val generations = mutableMapOf<EntityId, GenerationComponent>()

    val targets = mutableMapOf<EntityId, TargetComponent>()

    val foodTags = mutableSetOf<EntityId>()
    val foodValues = mutableMapOf<EntityId, FoodEnergyComponent>()

    val cooldowns = mutableMapOf<EntityId, PlantCooldownComponent>()

    val colors = mutableMapOf<EntityId, ColorComponent>()

    val zoneTags = mutableSetOf<EntityId>()

    val zones = mutableMapOf<EntityId, FertileZoneComponent>()

    val powers = mutableMapOf<EntityId, PowerComponent>()

    val decays = mutableMapOf<EntityId, DecayComponent>()

    val allComponents = listOf (
        genomes,
        positions,
        velocities,
        energies,
        states,
        perceptions,
        sizes,
        diets,
        reproductions,
        sexes,
        reproductionEnergyThresholds,
        reproductionEnergyCosts,
        fertilities,
        histories,
        ages,
        maturingRatios,
        agingRatios,
        growthRates,
        foodValues,
        cooldowns,
        targets,
        speeds,
        generations,
        colors,
        powers,
        decays,
    )

    val allTags = listOf (
        creatureTags,
        plantTags,
        foodTags,
    )

    val genes = listOf<KClass<*>>(
        MuscleDensity::class,
        MuscleFibersSpeed::class,
        TendonElasticity::class,
        BoneStrength::class,
        BodyMassDistribution::class,
        MaxBodyGrowth::class,
        MetabolicRate::class,
        EnergyStorage::class,
        EnergyEfficiency::class,
        VisualAcuity::class,
        MotionDetection::class,
        SensoryRange::class,
        NeuralProcessingSpeed::class,
        GrowthHormoneLevel::class,
        ReproductiveHormoneLevel::class,
        LongevityRegulation::class,
        MaturationTiming::class,
        ParentalInvestment::class,
        AgingSpeed::class,
    )

    data class ReproductiveCreature(
        val id: EntityId,
        val genome: Genome,
        val position: PositionComponent,
        val velocity: VelocityComponent,
        val perception: PerceptionComponent,
        val size: SizeComponent,
        val growthRateComponent: GrowthRateComponent,
        val diet: DietComponent,
        val reproduction: ReproductionComponent,
        val sex: SexComponent,
        val reproductionEnergyThreshold: ReproductionEnergyThresholdComponent,
        val reproductionEnergyCost: ReproductionEnergyCostComponent,
        val fertility: FertilityComponent,
        val generation: GenerationComponent,
        val age: AgeComponent,
        val maturingRatio: MaturingRatioComponent,
        val agingRatio: AgingRatioComponent,
        val energy: EnergyComponent,
        val speed: SpeedComponent
    )

    fun getReproductiveCreature(id: EntityId): ReproductiveCreature? {
        return ReproductiveCreature(
            id = id,
            genome = genomes[id] ?: return null,
            position = positions[id] ?: return null,
            velocity = velocities[id] ?: return null,
            perception = perceptions[id] ?: return null,
            size = sizes[id] ?: return null,
            growthRateComponent = growthRates[id] ?: return null,
            diet = diets[id] ?: return null,
            reproduction = reproductions[id] ?: return null,
            sex = sexes[id] ?: return null,
            reproductionEnergyThreshold = reproductionEnergyThresholds[id] ?: return null,
            reproductionEnergyCost = reproductionEnergyCosts[id] ?: return null,
            fertility = fertilities[id] ?: return null,
            generation = generations[id] ?: return null,
            age = ages[id] ?: return null,
            maturingRatio = maturingRatios[id] ?: return null,
            agingRatio = agingRatios[id] ?: return null,
            energy = energies[id] ?: return null,
            speed = speeds[id] ?: return null,
        )
    }
}
