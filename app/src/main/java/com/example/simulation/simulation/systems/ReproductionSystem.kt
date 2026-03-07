package com.example.simulation.simulation.systems

import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.CooldownComponent
import com.example.simulation.simulation.CreatureStates
import com.example.simulation.simulation.DietComponent
import com.example.simulation.simulation.EnergyComponent
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.FoodValueComponent
import com.example.simulation.simulation.HistoryComponent
import com.example.simulation.simulation.PerceptionComponent
import com.example.simulation.simulation.PositionComponent
import com.example.simulation.simulation.ReproductionComponent
import com.example.simulation.simulation.SizeComponent
import com.example.simulation.simulation.StateComponent
import com.example.simulation.simulation.TargetComponent
import com.example.simulation.simulation.VelocityComponent
import com.example.simulation.simulation.World
import com.example.simulation.simulation.factories.CreatureFactory
import kotlin.collections.plusAssign
import kotlin.math.max
import kotlin.random.Random
import kotlin.reflect.KClass


data class ReproductiveCreature(
    val id: EntityId,
    val position: PositionComponent,
    val velocity: VelocityComponent,
    val perception: PerceptionComponent,
    val size: SizeComponent,
    val diet: DietComponent,
    val reproduction: ReproductionComponent,
    val history: HistoryComponent,
    val age: AgeComponent,
    val energy: EnergyComponent
)

class ReproductionSystem(
    private val width: Float,
    private val height: Float
) : System {

    override fun update(world: World, delta: Int) {

        val creatureFactory = CreatureFactory(world, width, height)
        val newborns = mutableListOf<EntityId>()

        for (entity in world.creatureTags) {
            val parent = world.getReproductiveCreature(entity) ?: continue

            val child = updateReproduction(parent, creatureFactory, delta)
            if (child != null) newborns += child
        }

        world.creatureTags.addAll(newborns)
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
            FoodValueComponent::class,
            CooldownComponent::class,
            TargetComponent::class,
        )
    }

    private fun updateReproduction(
        parent: ReproductiveCreature,
        creatureFactory: CreatureFactory,
        delta: Int
    ): EntityId? {

        parent.reproduction.cooldown += delta

        val canReproduce =
            parent.energy.energy > parent.reproduction.energyThreshold &&
            parent.reproduction.cooldown >= parent.reproduction.maxCooldown

        if (!canReproduce) return null

        val offspring = creatureFactory.spawnCopy(parent)

        parent.energy.energy -= parent.reproduction.energyCost
        parent.reproduction.cooldown = 0

        return offspring
    }

}