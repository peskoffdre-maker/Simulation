package com.example.simulation.simulation.systems

import com.example.simulation.simulation.CreatureStates
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.SizeComponent
import com.example.simulation.simulation.SpeedComponent
import com.example.simulation.simulation.StateComponent
import com.example.simulation.simulation.TargetComponent
import com.example.simulation.simulation.VelocityComponent
import com.example.simulation.simulation.World
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.reflect.KClass


/**
 * Class responsible for behavior of creatures.
 * It checks the state of a creature and gives a direction according to the state.
 */
class BehaviorSystem(private val world: World) : System {

    private val rng = world.rng

    override fun update() {
        for (id in world.creatureTags) {
            val state = world.states[id] ?: continue
            val velocity = world.velocities[id] ?: continue
            val speed = world.speeds[id] ?: continue
            val size = world.sizes[id] ?: continue

            // Adjusting speed to size of a creature
            val sizeFactor = 1f / sqrt(size.value / 2)
            val adjustedSpeed = speed.speed * sizeFactor

            when (state.state) {
                CreatureStates.IDLE, CreatureStates.DECAYING -> stop(velocity)
                CreatureStates.ROAMING -> roam(velocity)
                else -> moveTowardsTarget(world, id, velocity, adjustedSpeed)
            }
        }
    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            VelocityComponent::class,
            StateComponent::class,
            TargetComponent::class,
            SpeedComponent::class,
            SizeComponent::class,
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

    // Randomly, slightly changes the direction
    private fun roam(velocity: VelocityComponent) {
        velocity.dx += (rng.nextFloat() * 0.1f - 0.05f)
        velocity.dy += (rng.nextFloat() * 0.1f - 0.05f)
    }

    private fun moveTowardsTarget(
        world: World,
        id: EntityId,
        velocity: VelocityComponent,
        speedMult: Float,
    ) {
        val position = world.positions[id] ?: return
        val target = world.targets[id] ?: return

        val targetId = target.targetId ?: return
        val targetPosition = world.positions[targetId] ?: return

        val dx = targetPosition.x - position.x
        val dy = targetPosition.y - position.y
        val distance = target.distance ?: return

        // Move toward target
        if (distance > 0f) {
            velocity.dx = (dx / distance) * speedMult
            velocity.dy = (dy / distance) * speedMult
        }
    }
}