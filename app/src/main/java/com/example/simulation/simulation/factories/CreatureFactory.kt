package com.example.simulation.simulation.factories

import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.CreatureStates
import com.example.simulation.simulation.DietComponent
import com.example.simulation.simulation.Diets
import com.example.simulation.simulation.EnergyComponent
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.HistoryComponent
import com.example.simulation.simulation.PerceptionComponent
import com.example.simulation.simulation.PositionComponent
import com.example.simulation.simulation.ReproductionComponent
import com.example.simulation.simulation.SimulationConfiguration.creatureDetectionMultiplier
import com.example.simulation.simulation.SimulationConfiguration.creatureInitialReproductionCost
import com.example.simulation.simulation.SimulationConfiguration.creatureInitialSizeMultiplier
import com.example.simulation.simulation.SimulationConfiguration.creatureInitialVelocity
import com.example.simulation.simulation.SimulationConfiguration.maxCreatureInitialDetection
import com.example.simulation.simulation.SimulationConfiguration.maxCreatureInitialEnergy
import com.example.simulation.simulation.SimulationConfiguration.maxCreatureInitialMaxAge
import com.example.simulation.simulation.SimulationConfiguration.maxCreatureInitialReproductionCD
import com.example.simulation.simulation.SimulationConfiguration.maxCreatureInitialReproductionEnergyThreshold
import com.example.simulation.simulation.SimulationConfiguration.minCreatureInitialDetection
import com.example.simulation.simulation.SimulationConfiguration.minCreatureInitialEnergy
import com.example.simulation.simulation.SimulationConfiguration.minCreatureInitialMaxAge
import com.example.simulation.simulation.SimulationConfiguration.minCreatureInitialReproductionCD
import com.example.simulation.simulation.SimulationConfiguration.minCreatureInitialReproductionEnergyThreshold
import com.example.simulation.simulation.SimulationConfiguration.spawnOffset
import com.example.simulation.simulation.SizeComponent
import com.example.simulation.simulation.StateComponent
import com.example.simulation.simulation.TargetComponent
import com.example.simulation.simulation.VelocityComponent
import com.example.simulation.simulation.World
import com.example.simulation.simulation.systems.ReproductiveCreature
import kotlin.math.max
import kotlin.random.Random

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

    fun spawnRandomCreature() {
        val entity = world.entityManager.create()

        world.positions[entity] = PositionComponent(
            Random.nextFloat() * width,
            Random.nextFloat() * height,
        )

        world.velocities[entity] = VelocityComponent(
            Random.nextFloat() * creatureInitialVelocity - creatureInitialVelocity / 2,
            Random.nextFloat() * creatureInitialVelocity - creatureInitialVelocity / 2
        )

        world.energies[entity] = EnergyComponent(
            energy = Random.nextInt(
                minCreatureInitialEnergy,
                maxCreatureInitialEnergy
            ).toFloat(),
        )

        world.states[entity] = StateComponent(CreatureStates.IDLE)

        world.perceptions[entity] = PerceptionComponent(
            Random.nextInt(
                minCreatureInitialDetection,
                maxCreatureInitialDetection
            ) * creatureDetectionMultiplier
        )

        world.sizes[entity] = SizeComponent(Random.nextFloat() * creatureInitialSizeMultiplier)

        world.diets[entity] = DietComponent(diet = dietsProbability.random())

        world.reproductions[entity] = ReproductionComponent(
            Random.nextInt(
                minCreatureInitialReproductionEnergyThreshold,
                maxCreatureInitialReproductionEnergyThreshold
            ),
            creatureInitialReproductionCost,
            Random.nextFloat(),
            0,
            Random.nextInt(
                minCreatureInitialReproductionCD,
                maxCreatureInitialReproductionCD)
        )

        world.histories[entity] = HistoryComponent()
        world.ages[entity] = AgeComponent(0, Random.nextInt(
            minCreatureInitialMaxAge,
            maxCreatureInitialMaxAge
            )
        )
        world.targets[entity] = TargetComponent()

        world.creatureTags.add(entity)
    }

    fun spawnCopy(
        parent: ReproductiveCreature,
    ): EntityId {

        val offspring = world.entityManager.create()

        world.positions[offspring] = PositionComponent(
            parent.position.x + spawnOffset,
            parent.position.y + spawnOffset
        )

        world.velocities[offspring] = VelocityComponent(
            parent.velocity.dx,
            parent.velocity.dy
        )

        world.energies[offspring] = EnergyComponent(
            energy = Random.nextInt(minCreatureInitialEnergy, maxCreatureInitialEnergy).toFloat()
        )

        world.states[offspring] = StateComponent(CreatureStates.IDLE)

        world.perceptions[offspring] =
            PerceptionComponent(parent.perception.detectionRadius)

        world.sizes[offspring] =
            SizeComponent(max(1f, parent.size.size / 5f))

        world.diets[offspring] =
            DietComponent(parent.diet.diet)

        world.reproductions[offspring] =
            parent.reproduction.copy(cooldown = 0)

        world.histories[offspring] =
            HistoryComponent(generation = parent.history.generation + 1)

        world.ages[offspring] =
            AgeComponent(0, parent.age.maxAge)

        world.targets[offspring] = TargetComponent()

        return offspring
    }
}