package com.example.simulation.simulation.systems

import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.AgingStages
import com.example.simulation.simulation.PlantCooldownComponent
import com.example.simulation.simulation.DietComponent
import com.example.simulation.simulation.EnergyComponent
import com.example.simulation.simulation.EntitiesCollide
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.FoodEnergyComponent
import com.example.simulation.simulation.GenerationComponent
import com.example.simulation.simulation.GrowthRateComponent
import com.example.simulation.simulation.HistoryComponent
import com.example.simulation.simulation.Intents
import com.example.simulation.simulation.PerceptionComponent
import com.example.simulation.simulation.PositionComponent
import com.example.simulation.simulation.ReproductionComponent
import com.example.simulation.simulation.ReproductionEnergyCostComponent
import com.example.simulation.simulation.ReproductionEnergyThresholdComponent
import com.example.simulation.simulation.SimulationConfiguration.cooldownPerTick
import com.example.simulation.simulation.SizeComponent
import com.example.simulation.simulation.SpeedComponent
import com.example.simulation.simulation.StateComponent
import com.example.simulation.simulation.TargetComponent
import com.example.simulation.simulation.VelocityComponent
import com.example.simulation.simulation.World
import com.example.simulation.simulation.factories.CreatureFactory
import com.example.simulation.simulation.factories.Offspring
import kotlin.reflect.KClass

/**
 * Class manages reproduction of creatures.
 */
class ReproductionSystem(
    private val world: World,
    private val width: Float,
    private val height: Float
) : System {
    val creatureFactory = CreatureFactory(world, width, height)


    override fun update() {

        updateReproductionState()
        reproduce()
        addNewborns()
    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            EnergyComponent::class,
            AgeComponent::class
        )
    }

    override fun writes(): List<KClass<*>> {
        return listOf(
            PositionComponent::class,
            VelocityComponent::class,
            EnergyComponent::class,
            StateComponent::class,
            PerceptionComponent:: class,
            DietComponent::class,
            SizeComponent::class,
            ReproductionComponent::class,
            HistoryComponent::class,
            AgeComponent::class,
            FoodEnergyComponent::class,
            PlantCooldownComponent::class,
            TargetComponent::class,
        )
    }

    private fun updateReproductionState()  {
        for (entity in world.creatureTags) {
            val parent = world.getReproductiveCreature(entity) ?: continue

            parent.reproduction.canReproduce =
                        parent.energy.currentEnergy > parent.reproductionEnergyThreshold.value &&
                        parent.reproduction.cooldown <= 0 &&
                        parent.age.agingStage == AgingStages.ADULT
        }
    }

    private fun reproduce() {

        for (event in world.eventBus.getEvents()) {

            if (event is EntitiesCollide) {
                if (event.intent == Intents.REPRODUCE) {

                    val parent = world.getReproductiveCreature(event.entityId) ?: continue

                    if (event.targetId in world.creatureTags) {
                        reproduceOnCollision(
                            parent1 = parent,
                            targetId = event.targetId,
                            world = world,
                        )
                    }
                }
            }
        }
    }

    private fun addNewborns(){
        while (world.birthQ.isNotEmpty()) {
            val newborn = world.birthQ.removeFirst()
            val id = newborn.id
            world.creatureTags.add(id)
            world.genomes[id] = newborn.genome
            world.positions[id] = newborn.position
            world.perceptions[id] = newborn.perception
            world.sizes[id] = newborn.size
            world.growthRates[id] = newborn.growthRate
            world.reproductions[id] = newborn.reproduction
            world.sexes[id] = newborn.sex
            world.reproductionEnergyThresholds[id] = newborn.reproductionEnergyThreshold
            world.reproductionEnergyCosts[id] = newborn.reproductionEnergyCost
            world.fertilities[id] = newborn.fertility
            world.ages[id] = newborn.age
            world.maturingRatios[id] = newborn.maturingRatio
            world.agingRatios[id] = newborn.agingRatio
            world.speeds[id] = newborn.speed
            world.velocities[id] = newborn.velocity
            world.energies[id] = newborn.energy
            world.diets[id] = newborn.diet
            world.generations[id] = newborn.generation
            world.states[id] = newborn.state
            world.histories[id] = newborn.history
            world.colors[id] = newborn.color
            world.targets[id] = newborn.target
            world.powers[id] = newborn.power
        }
    }

    private fun reproduceOnCollision(
        parent1: World.ReproductiveCreature,
        targetId: EntityId,
        world: World,
        )
    {

        val parent2 = world.getReproductiveCreature(targetId) ?: return

        if (
            !parent1.reproduction.canReproduce ||
            !parent2.reproduction.canReproduce ||
            !parent1.sex.value
            ) return

        val childrenNumber = (parent1.fertility.value + parent2.fertility.value).toInt()

        repeat(childrenNumber) {
            world.birthQ.addLast(creatureFactory.spawnOffspring(parent1, parent2))

            parent1.energy.currentEnergy -= parent1.reproductionEnergyCost.value
            parent2.energy.currentEnergy -= parent1.reproductionEnergyCost.value

            parent1.reproduction.cooldown = 0
            parent2.reproduction.cooldown = 0

        }
    }


}