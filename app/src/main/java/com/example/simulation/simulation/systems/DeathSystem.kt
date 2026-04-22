package com.example.simulation.simulation.systems

import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.CreatureStates
import com.example.simulation.simulation.PlantCooldownComponent
import com.example.simulation.simulation.DietComponent
import com.example.simulation.simulation.EnergyComponent
import com.example.simulation.simulation.EnergyDepletedEvent
import com.example.simulation.simulation.EntityDiedOfAgeEvent
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.EntityKilled
import com.example.simulation.simulation.FoodEnergyComponent
import com.example.simulation.simulation.HistoryComponent
import com.example.simulation.simulation.PerceptionComponent
import com.example.simulation.simulation.PositionComponent
import com.example.simulation.simulation.ReproductionComponent
import com.example.simulation.simulation.SizeComponent
import com.example.simulation.simulation.StateComponent
import com.example.simulation.simulation.TargetComponent
import com.example.simulation.simulation.VelocityComponent
import com.example.simulation.simulation.World
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.reflect.KClass


/**
 * Class manages dying of all entities with age components present.
 *
 */
class DeathSystem(private val world: World) : System {

    override fun update() {
        for (event in world.eventBus.getEvents()) {
            when (event) {
                is EnergyDepletedEvent -> world.states[event.entityId]?.state = CreatureStates.DECAYING
                is EntityDiedOfAgeEvent -> world.states[event.entityId]?.state = CreatureStates.DECAYING
                is EntityKilled -> world.states[event.entityId]?.state = CreatureStates.DECAYING
            }
        }
        removeDead()

    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            AgeComponent::class,
            EnergyComponent::class,
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

    private fun removeDead() {
        while (world.deathQ.isNotEmpty()) {
            val entity = world.deathQ.removeFirst()
            removeEntity(entity)
        }
    }

    private fun removeEntity(entityId: EntityId) {
        world.allComponents.forEach { it.remove(entityId) }
        world.allTags.forEach { it.remove(entityId) }
    }
}