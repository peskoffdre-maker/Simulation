package com.example.simulation.simulation.systems

import com.example.simulation.simulation.HistoryComponent
import com.example.simulation.simulation.PositionComponent
import com.example.simulation.simulation.VelocityComponent
import com.example.simulation.simulation.World
import kotlin.math.abs
import kotlin.reflect.KClass


/**
 * Class applies calculated direction to position. Manages collisions with boundaries
 */
class PhysicsSystem(
    private val world: World,
    private val width: Float,
    private val height: Float,
) : System {

    override fun update() {
        for (id in world.creatureTags) {
            val entityPosition = world.positions[id] ?: continue
            val entityVelocity = world.velocities[id] ?: continue
            val history = world.histories[id] ?: continue

            updatePosition(entityPosition, entityVelocity, history)
            handleBoundaries(entityPosition, entityVelocity)
        }
    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            PositionComponent::class,
            VelocityComponent::class,
        )
    }

    override fun writes(): List<KClass<*>> {
        return listOf(
            VelocityComponent::class,
        )
    }

    fun updatePosition(
        position: PositionComponent,
        velocity: VelocityComponent,
        history: HistoryComponent
    ) {
        history.distanceMoved += abs(velocity.dx) + abs(velocity.dy)
        position.x += velocity.dx
        position.y += velocity.dy
    }

    fun handleBoundaries(
        position: PositionComponent,
        velocity: VelocityComponent,
        ) {

        val isOutOfBoundsMinX = position.x < 0f
        val isOutOfBoundsMaxX = position.x > width

        val isOutOfBoundsMinY = position.y < 0f
        val isOutOfBoundsMaxY = position.y > height

        fun resetXTo(newValue: Float) {
            position.x = newValue
            velocity.dx *= -1
        }

        fun resetYTo(newValue: Float) {
            position.y = newValue
            velocity.dy *= -1
        }

        if (isOutOfBoundsMinX) {
            resetXTo(0f)
        }
        else if (isOutOfBoundsMaxX) {
            resetXTo(width)
        }
        if (isOutOfBoundsMinY) {
            resetYTo(0f)
        }
        else if (isOutOfBoundsMaxY) {
            resetYTo(height)
        }
    }

}