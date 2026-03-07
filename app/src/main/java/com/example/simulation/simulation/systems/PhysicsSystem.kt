package com.example.simulation.simulation.systems

import com.example.simulation.simulation.PositionComponent
import com.example.simulation.simulation.VelocityComponent
import com.example.simulation.simulation.World
import kotlin.math.abs
import kotlin.reflect.KClass

class PhysicsSystem(
    private val width: Float,
    private val height: Float,
) : System {

    override fun update(world: World, delta: Int) {
        for (entityId in world.creatureTags) {
            val creature = world.getCreature(entityId) ?: continue

            updatePosition(creature, delta)
            handleBoundaries(creature)
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
        creature: Creature,
        delta: Int
    ) {
        creature.history.distanceMoved += abs(creature.velocity.dx) + abs(creature.velocity.dy)
        creature.position.x += creature.velocity.dx * delta
        creature.position.y += creature.velocity.dy * delta
    }

    fun handleBoundaries(creature: Creature) {

        val isOutOfBoundsMinX = creature.position.x < 0f
        val isOutOfBoundsMaxX = creature.position.x > width

        val isOutOfBoundsMinY = creature.position.y < 0f
        val isOutOfBoundsMaxY = creature.position.y > height

        fun resetXTo(newValue: Float) {
            creature.position.x = newValue
            creature.velocity.dx *= -1
        }

        fun resetYTo(newValue: Float) {
            creature.position.y = newValue
            creature.velocity.dy *= -1
        }

        if (isOutOfBoundsMinX) {
            resetXTo(0f)
        }
        if (isOutOfBoundsMaxX) {
            resetXTo(width)
        }
        if (isOutOfBoundsMinY) {
            resetYTo(0f)
        }
        if (isOutOfBoundsMaxY) {
            resetYTo(height)
        }
    }

}