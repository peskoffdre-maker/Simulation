package com.example.simulation.simulation.systems

import com.example.simulation.simulation.CreatureStates
import com.example.simulation.simulation.DietComponent
import com.example.simulation.simulation.Diets
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.PerceptionComponent
import com.example.simulation.simulation.StateComponent
import com.example.simulation.simulation.TargetComponent
import com.example.simulation.simulation.World
import com.example.simulation.simulation.utils.SpatialQueryService
import kotlin.math.sqrt
import kotlin.reflect.KClass

class TargetSeekingSystem(private val spatial: SpatialQueryService) : System {
    override fun update(world: World, delta: Int) {
        for ((id, diet) in world.diets) {
            if (diet.diet == Diets.HERBIVORE && world.states[id]!!.state == CreatureStates.HUNGER) {
                updateHerbivores(id, world)
            }
        }
    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            DietComponent::class,
            StateComponent::class,
            PerceptionComponent::class,
            TargetComponent::class,
        )
    }

    override fun writes(): List<KClass<*>> {
        return listOf(
            TargetComponent::class,
        )
    }

    private fun updateHerbivores(id: EntityId, world: World) {
        val perception = world.perceptions[id]?.detectionRadius ?: return
        val target = world.targets[id] ?: return
        val (nearestFood, distanceSq) = spatial.findClosestFood(
            from = id,
            radius = perception
        ) ?: return
        target.targetId = nearestFood
        target.distance = sqrt(distanceSq)

    }
}