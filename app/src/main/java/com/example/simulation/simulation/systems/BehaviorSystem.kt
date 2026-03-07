package com.example.simulation.simulation.systems

import com.example.simulation.simulation.CreatureStates
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.SimulationConfiguration.maxSpeed
import com.example.simulation.simulation.StateComponent
import com.example.simulation.simulation.TargetComponent
import com.example.simulation.simulation.VelocityComponent
import com.example.simulation.simulation.World
import kotlin.random.Random
import kotlin.reflect.KClass

class BehaviorSystem() : System {
    override fun update(world: World, delta: Int) {
        for (id in world.creatureTags) {
            val state = world.states[id] ?: continue
            val velocity = world.velocities[id] ?: continue

            when (state.state) {
                CreatureStates.IDLE -> stop(velocity)
                CreatureStates.ROAMING -> roam(velocity)
                CreatureStates.HUNGER -> moveTowardsFood(world, id, velocity)
                else -> {}
            }
        }
    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            VelocityComponent::class,
            StateComponent::class,
            TargetComponent::class
        )
    }

    override fun writes(): List<KClass<*>> {
        return listOf(
            VelocityComponent::class
        )
    }

    private fun stop(velocity: VelocityComponent) {
        velocity.dx = 0f
        velocity.dy = 0f
    }

    private fun roam(velocity: VelocityComponent) {
        velocity.dx += Random.nextFloat() * 0.2f - 0.1f
        velocity.dy += Random.nextFloat() * 0.2f - 0.1f
    }

    private fun moveTowardsFood(
        world: World,
        id: EntityId,
        velocity: VelocityComponent,
    ) {
        val position = world.positions[id] ?: return
        val target = world.targets[id] ?: return

        val foodId = target.targetId ?: return
        val foodPosition = world.positions[foodId] ?: return

        val dx = foodPosition.x - position.x
        val dy = foodPosition.y - position.y
        val distance = target.distance ?: return

        // Move toward food
        if (distance > 0f) {
            velocity.dx = (dx / distance) * maxSpeed
            velocity.dy = (dy / distance) * maxSpeed
        }
    }
}