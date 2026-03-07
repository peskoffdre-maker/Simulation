package com.example.simulation.simulation.systems

import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.EnergyComponent
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.FoodValueComponent
import com.example.simulation.simulation.HistoryComponent
import com.example.simulation.simulation.SizeComponent
import com.example.simulation.simulation.StateComponent
import com.example.simulation.simulation.TargetComponent
import com.example.simulation.simulation.VelocityComponent
import com.example.simulation.simulation.World
import kotlin.reflect.KClass

class EatingSystem: System {
    override fun update(world: World, delta: Int) {
        for ((id, target) in world.targets) {
            eatOnCollision(id, world, target)
        }
    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            TargetComponent::class,
            AgeComponent::class,
            FoodValueComponent::class,
            SizeComponent::class,
            EnergyComponent::class,
            HistoryComponent::class,
        )
    }

    override fun writes(): List<KClass<*>> {
        return listOf(
            EnergyComponent::class,
            HistoryComponent::class,
            TargetComponent::class,
        )
    }

    private fun eatOnCollision(id: EntityId, world: World, target: TargetComponent) {
        val targetDistance = target.distance ?: return
        val targetId = target.targetId ?: return
        val targetAge = world.ages[targetId] ?: return
        val foodEnergyValue = world.foodValues[targetId] ?: return

        val size = world.sizes[id]?.size ?: return
        val energy = world.energies[id] ?: return
        val history = world.histories[id] ?: return

        if (targetDistance <= size) {
            energy.energy += foodEnergyValue.energyValue
            history.energyGained += foodEnergyValue.energyValue
            history.foodConsumed++

            target.targetId = null
            target.distance = null

            targetAge.age += targetAge.maxAge
        }
    }
}