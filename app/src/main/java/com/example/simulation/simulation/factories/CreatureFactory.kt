package com.example.simulation.simulation.factories

import android.util.Size
import androidx.compose.ui.graphics.Color
import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.AgingRatioComponent
import com.example.simulation.simulation.ColorComponent
import com.example.simulation.simulation.CreatureStates
import com.example.simulation.simulation.DecayComponent
import com.example.simulation.simulation.DietComponent
import com.example.simulation.simulation.Diets
import com.example.simulation.simulation.EnergyComponent
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.FertilityComponent
import com.example.simulation.simulation.GenerationComponent
import com.example.simulation.simulation.Genome
import com.example.simulation.simulation.GrowthRateComponent
import com.example.simulation.simulation.HistoryComponent
import com.example.simulation.simulation.MaturingRatioComponent
import com.example.simulation.simulation.PerceptionComponent
import com.example.simulation.simulation.PositionComponent
import com.example.simulation.simulation.PowerComponent
import com.example.simulation.simulation.ReproductionComponent
import com.example.simulation.simulation.ReproductionEnergyCostComponent
import com.example.simulation.simulation.ReproductionEnergyThresholdComponent
import com.example.simulation.simulation.SexComponent
import com.example.simulation.simulation.SimulationConfiguration.creatureInitialVelocity
import com.example.simulation.simulation.SimulationConfiguration.maxCreatureInitialEnergy
import com.example.simulation.simulation.SimulationConfiguration.minCreatureInitialEnergy
import com.example.simulation.simulation.SimulationConfiguration.spawnOffset
import com.example.simulation.simulation.SimulationConfiguration.mutationLowerLimit
import com.example.simulation.simulation.SimulationConfiguration.mutationUpperLimit
import com.example.simulation.simulation.SizeComponent
import com.example.simulation.simulation.SpeedComponent
import com.example.simulation.simulation.StateComponent
import com.example.simulation.simulation.TargetComponent
import com.example.simulation.simulation.VelocityComponent
import com.example.simulation.simulation.World
import com.example.simulation.simulation.World.ReproductiveCreature
import kotlin.math.sqrt
import kotlin.random.Random

data class Offspring(
    val id: EntityId,
    val genome: Genome,
    val position: PositionComponent,
    val perception: PerceptionComponent,
    val size: SizeComponent,
    val growthRate: GrowthRateComponent,
    val reproduction: ReproductionComponent,
    val sex: SexComponent,
    val reproductionEnergyThreshold: ReproductionEnergyThresholdComponent,
    val reproductionEnergyCost: ReproductionEnergyCostComponent,
    val fertility: FertilityComponent,
    val age: AgeComponent,
    val maturingRatio: MaturingRatioComponent,
    val agingRatio: AgingRatioComponent,
    val speed: SpeedComponent,
    val velocity: VelocityComponent,
    val energy: EnergyComponent,
    val diet: DietComponent,
    val generation: GenerationComponent,
    val state: StateComponent,
    val history: HistoryComponent,
    val color: ColorComponent,
    val target: TargetComponent,
    val power: PowerComponent,
    val decay: DecayComponent,
)


/**
 *  A class, that is used for creating new creatures, random or offspring of two parents.
 *  Width and height are boundaries of the area, where new creatures are spawned.
 *  Takes entity manager from world to generate new ID and spawn random initial creatures.
 */
class CreatureFactory(
    private val world: World,
    private val width: Float,
    private val height: Float
    ) {

    private val dietsProbability = listOf(
        Diets.HERBIVORE,
        Diets.HERBIVORE,
        Diets.HERBIVORE,
        Diets.CARNIVORE,
        Diets.OMNIVORE
    )

    val traitFactory = TraitFactory()

    val rng = world.rng

    fun spawnInitialCreature(diet: DietComponent = getRandomDietComponent()) {
        val entity = world.entityManager.create()

        val genome = generateInitialGenome()
        val traits = traitFactory.generateTraitsFromGenome(genome)
        world.genomes[entity] = genome

        world.positions[entity] = getRandomPositionComponent()
        world.velocities[entity] = getRandomVelocityComponent()
        world.sexes[entity] = getRandomSexComponent()

        world.diets[entity] = diet

        world.energies[entity] = generateEnergyFromTraits(traits)
        world.perceptions[entity] = generatePerceptionFromTraits(traits)
        world.sizes[entity] = generateSizeFromTraits(traits)
        world.growthRates[entity] = generateGrowthRateFromTraits(traits)
        world.reproductions[entity] = generateReproductionFromTraits(traits)
        world.reproductionEnergyThresholds[entity] = generateReproductionEnergyThresholdFromTraits(traits)
        world.reproductionEnergyCosts[entity] = generateReproductionEnergyCostFromTraits(traits)
        world.fertilities[entity] = generateFertilityFromTraits(traits)
        world.maturingRatios[entity] = generateMaturingRatioFromTraits(traits)
        world.agingRatios[entity] = generateAgingRatioFromTraits(traits)
        world.ages[entity] = generateAgeFromTraits(traits)
        world.speeds[entity] = generateSpeedFromTraits(traits)
        world.powers[entity] = generatePowerFromTraits(traits)

        world.decays[entity] = generateDecayComponent(world.sizes[entity]!!)


        world.states[entity] = StateComponent(CreatureStates.IDLE)
        world.histories[entity] = HistoryComponent()
        world.generations[entity] = GenerationComponent(0)
        world.targets[entity] = TargetComponent()

        world.colors[entity] = ColorComponent(
            entityToColor(
                perception = world.perceptions[entity]!!,
                size = world.sizes[entity]!!,
                speed = world.speeds[entity]!!,
                age = world.ages[entity]!!
            )
        )

        world.creatureTags.add(entity)
    }

    // Creates new creature randomly choosing an inheritable trait between two parents and mutates it

    fun spawnOffspring(parent1: ReproductiveCreature, parent2: ReproductiveCreature): Offspring {

        val offspringId = world.entityManager.create()

        val offspringGenome = inheritMutatedGenome(parent1.genome, parent2.genome)
        val traits = traitFactory.generateTraitsFromGenome(offspringGenome)

        val offspringPosition = inheritPositionComponent(parent1, parent2)
        val offspringVelocity = inheritVelocityComponent(parent1, parent2)

        val offspringEnergy = generateEnergyFromTraits(traits)
        val offspringPerception = generatePerceptionFromTraits(traits)
        val offspringSize = generateSizeFromTraits(traits)
        val offspringGrowthRate = generateGrowthRateFromTraits(traits)
        val offspringReproduction = generateReproductionFromTraits(traits)
        val offspringReproductionEnergyThreshold = generateReproductionEnergyThresholdFromTraits(traits)
        val offspringReproductionEnergyCost = generateReproductionEnergyCostFromTraits(traits)
        val offspringFertility = generateFertilityFromTraits(traits)
        val offspringMaturingRatio = generateMaturingRatioFromTraits(traits)
        val offspringAgingRation = generateAgingRatioFromTraits(traits)
        val offspringAge = generateAgeFromTraits(traits)
        val offspringSpeed = generateSpeedFromTraits(traits)
        val offspringPower = generatePowerFromTraits(traits)

        val offspringSex = getRandomSexComponent()
        val offspringDiet = DietComponent(parent1.diet.diet)
        val offspringGeneration = GenerationComponent(parent1.generation.generation + 1)

        val offspringDecay = generateDecayComponent(offspringSize)

        val offspringState = StateComponent(CreatureStates.IDLE)
        val offspringHistory = HistoryComponent()

        val offspringColor = ColorComponent(
            entityToColor(
                perception = offspringPerception,
                size = offspringSize,
                speed = offspringSpeed,
                age = offspringAge
            )
        )
        val offspringTarget = TargetComponent()

        return Offspring(
            id = offspringId,
            genome = offspringGenome,
            position = offspringPosition,
            perception = offspringPerception,
            size = offspringSize,
            growthRate = offspringGrowthRate,
            reproduction = offspringReproduction,
            sex = offspringSex,
            reproductionEnergyThreshold = offspringReproductionEnergyThreshold,
            reproductionEnergyCost = offspringReproductionEnergyCost,
            fertility = offspringFertility,
            age = offspringAge,
            maturingRatio = offspringMaturingRatio,
            agingRatio = offspringAgingRation,
            speed = offspringSpeed,
            velocity = offspringVelocity,
            energy = offspringEnergy,
            diet = offspringDiet,
            generation = offspringGeneration,
            state = offspringState,
            history = offspringHistory,
            color = offspringColor,
            target = offspringTarget,
            power = offspringPower,
            decay = offspringDecay
        )
    }
    private fun generateInitialGenome() : Genome {
        val genome = FloatArray(world.genes.size) {0.5f}
        return Genome(genome)
    }

    private fun inheritMutatedGenome(genome1: Genome, genome2: Genome) : Genome {
        val n = genome1.genes.size
        val offspringGenes = FloatArray(n)
        for (i in 0 until n) {
            val parent = if (rng.nextBoolean()) genome1.genes else genome2.genes
            offspringGenes[i] = calculateMutatedValue(parent[i])
        }
        return Genome(offspringGenes)
    }

    //---------------------------------------
    // RANDOM COMPONENTS
    //---------------------------------------

    private fun getRandomPositionComponent() : PositionComponent {
        return PositionComponent(
            rng.nextFloat() * width,
            rng.nextFloat() * height,
        )
    }
    private fun getRandomVelocityComponent() : VelocityComponent {
        return VelocityComponent(
            rng.nextFloat() * creatureInitialVelocity - creatureInitialVelocity / 2,
            rng.nextFloat() * creatureInitialVelocity - creatureInitialVelocity / 2
        )
    }

    private fun getRandomDietComponent() : DietComponent {
        return DietComponent(diet = dietsProbability.random())
    }

    private fun getRandomSexComponent() : SexComponent = SexComponent(rng.nextBoolean())

    //---------------------------------------
    // GENERATED FROM TRAITS COMPONENTS
    //---------------------------------------

    private fun generateEnergyFromTraits(traits: Traits) : EnergyComponent {
        return EnergyComponent(
            currentEnergy = rng.nextInt(
                minCreatureInitialEnergy,
                maxCreatureInitialEnergy
            ).toFloat(),
            metabolism = traits.metabolism
        )
    }

    private fun generatePerceptionFromTraits(traits: Traits) : PerceptionComponent {
        val radius = traits.perception
        return PerceptionComponent(radius, radius * 3f)
    }

    private fun generateSizeFromTraits(traits: Traits) : SizeComponent {
        val maxSize = traits.size
        val initialSize = maxSize * 0.2f
        return SizeComponent(initialSize, maxSize)
    }

    private fun generateGrowthRateFromTraits(traits: Traits) : GrowthRateComponent {
        return GrowthRateComponent(traits.growthRate)
    }

    private fun generateReproductionFromTraits(traits: Traits) : ReproductionComponent {
        val maxCD = traits.reproductionCD
        return ReproductionComponent(
            cooldown = maxCD / 4,
            maxCooldown = maxCD
        )
    }

    private fun generateReproductionEnergyThresholdFromTraits(traits: Traits) : ReproductionEnergyThresholdComponent {
        return ReproductionEnergyThresholdComponent(traits.reproductionEnergyThreshold)
    }

    private fun generateReproductionEnergyCostFromTraits(traits: Traits) : ReproductionEnergyCostComponent {
        return ReproductionEnergyCostComponent(traits.reproductionEnergyCost)
    }

    private fun generateFertilityFromTraits(traits: Traits) : FertilityComponent {
        return FertilityComponent(traits.fertility)
    }

    private fun generateMaturingRatioFromTraits(traits: Traits) : MaturingRatioComponent {
        return MaturingRatioComponent(traits.maturingRatio)
    }

    private fun generateAgingRatioFromTraits(traits: Traits) : AgingRatioComponent {
        return AgingRatioComponent(traits.agingRatio)
    }

    private fun generateAgeFromTraits(traits: Traits) : AgeComponent {
        val maxAge = traits.maxAge
        val maturityRatio = traits.maturingRatio
        val agingRatio = traits.agingRatio
        val (youngAdult, adultOld) = calculateAgeBoundaries(maxAge, maturityRatio, agingRatio)
        return AgeComponent(
            value = 0,
            maxAge = maxAge,
            youngAdult = youngAdult,
            adultOld = adultOld,
        )
    }

    private fun generateSpeedFromTraits(traits: Traits) : SpeedComponent {
        return SpeedComponent(traits.speed)
    }

    private fun generatePowerFromTraits(traits: Traits) : PowerComponent {
        return PowerComponent(traits.power)
    }

    //-------------------------------------------
    // INHERITABLE COMPONENTS
    //-------------------------------------------
    private fun inheritPositionComponent(parent1: ReproductiveCreature, parent2: ReproductiveCreature) : PositionComponent {
        val parent = if (rng.nextBoolean()) parent1 else parent2
        return PositionComponent(
            parent.position.x + spawnOffset,
            parent.position.y + spawnOffset
        )
    }

    private fun inheritVelocityComponent(parent1: ReproductiveCreature, parent2: ReproductiveCreature) : VelocityComponent {
        val dx = if (rng.nextBoolean()) parent1.velocity.dx else parent2.velocity.dx
        val dy = if (rng.nextBoolean()) parent1.velocity.dy else parent2.velocity.dy
        return VelocityComponent(dx, dy)
    }

    //--------------------------------------------

    private fun generateDecayComponent(size: SizeComponent) : DecayComponent {
        val max = sqrt(size.value).toInt() * 1000
        return DecayComponent(max, max)
    }

    private val mutationChance = 10
    private val baseMut = 0.02f
    private fun calculateMutatedValue(value: Float): Float {
        if (rng.nextInt(1,20) <= mutationChance) {
            val percent = rng.nextDouble(mutationLowerLimit, mutationUpperLimit) // from 0.05 to 0.15
            val direction = if (rng.nextBoolean()) 1 else -1
            return value * (1 + direction * (percent.toFloat())) + baseMut * direction // for genes with 0f
        }
        return value
    }

//    private fun calculateMutatedValue(value: Float): Float {
//        if (rng.nextInt(20) < mutationChance) {
//            val delta = rng.nextFloat() * mutationLowerLimit.toFloat() - mutationUpperLimit.toFloat()
//            return (value + delta).coerceIn(-1f, 1f)
//        }
//        return value
//    }


    private fun normalize(value: Float, min: Float, max: Float): Float {
        return ((value - min) / (max - min))
            .coerceIn(0f, 1f)
    }

    private fun normalize(value: Int, min: Int, max: Int): Float {
        return normalize(value.toFloat(), min.toFloat(), max.toFloat())
    }


    // Generates color from entities components
    private fun entityToColor(
        perception: PerceptionComponent,
        size: SizeComponent,
        speed: SpeedComponent,
        age: AgeComponent
    ): Color {
        val hue = normalize(perception.detectionRadius, 0f, 1000f) * 360f
        val saturation = normalize(size.maxSize, 0f, 50f)
        val value = normalize(speed.speed, 0f, 4f)

        val color = Color.hsv(
            hue = hue,
            saturation = saturation,
            value = value
        )

        return color
    }

    // Calculates boundaries for aging stages (young, adult, old)
    private fun calculateAgeBoundaries(
        maxAge: Int,
        maturityRatio: Float,
        agingRatio: Float
    ): Pair<Int, Int> {
        val youngAdult = (maxAge * maturityRatio).toInt()
        val adultOld = (maxAge * agingRatio).toInt()

        return Pair(youngAdult, adultOld)
    }
}