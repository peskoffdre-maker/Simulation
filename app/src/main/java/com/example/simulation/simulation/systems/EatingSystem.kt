package com.example.simulation.simulation.systems

import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.EnergyComponent
import com.example.simulation.simulation.EntitiesCollide
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.FoodEnergyComponent
import com.example.simulation.simulation.HistoryComponent
import com.example.simulation.simulation.Intents
import com.example.simulation.simulation.SizeComponent
import com.example.simulation.simulation.TargetComponent
import com.example.simulation.simulation.World
import kotlin.math.sqrt
import kotlin.reflect.KClass


/**
 * Class manages process of consuming food by creatures.
 * It checks if the target is food and check collision.
 */
class EatingSystem(private val world: World): System {
    override fun update() {
        for (event in world.eventBus.getEvents()) {
            if (event is EntitiesCollide) {
                if (event.intent == Intents.EAT) {
                    eatOnCollision(event.entityId, event.targetId)
                }
            }
        }
    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            TargetComponent::class,
            AgeComponent::class,
            FoodEnergyComponent::class,
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

    private fun eatOnCollision(id: EntityId, targetId: EntityId) {

        val target = world.targets[id] ?: return

        val energy = world.energies[id] ?: return
        val history = world.histories[id] ?: return

        if (targetId in world.foodTags) {

            val foodEnergyValue = world.foodValues[targetId] ?: return
            energy.currentEnergy += foodEnergyValue.value * sqrt(energy.metabolism)
            history.energyGained += foodEnergyValue.value
            history.foodConsumed++

        }

        if (targetId in world.creatureTags) {
            val targetSize = world.sizes[id]?.value ?: return
            val targetDecay = world.decays[id]?.currentDecay ?: return

            val energyValue = sqrt(targetSize) * targetDecay
            energy.currentEnergy += energyValue * sqrt(energy.metabolism)
            history.energyGained += (energyValue * sqrt(energy.metabolism)).toInt()
            history.foodConsumed++
        }

        target.targetId = null
        target.distance = null
        world.deathQ.addLast(targetId)

    }
}