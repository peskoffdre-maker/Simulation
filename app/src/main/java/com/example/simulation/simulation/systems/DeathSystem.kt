package com.example.simulation.simulation.systems

import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.CooldownComponent
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
import kotlin.reflect.KClass

class DeathSystem : System {
    override fun update(world: World, delta: Int) {
        val toRemove = mutableListOf<EntityId>()

        for ((id, age) in world.ages) {
            val energy = world.energies[id]

            if (age.age >= age.maxAge || (energy != null && energy.energy <= 0f)) {
                toRemove.add(id)
            }
        }

        toRemove.forEach { removeEntity(world, it) }
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
            FoodValueComponent::class,
            CooldownComponent::class,
            TargetComponent::class,
        )
    }

    private fun removeEntity(world: World, entityId: EntityId) {
        world.allComponents.forEach { it.remove(entityId) }
        world.allTags.forEach { it.remove(entityId) }
    }
}