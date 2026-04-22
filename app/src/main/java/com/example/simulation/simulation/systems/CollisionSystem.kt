package com.example.simulation.simulation.systems

import com.example.simulation.simulation.EntitiesCollide
import com.example.simulation.simulation.World
import kotlin.reflect.KClass

class CollisionSystem(private val world: World) : System {
    override fun update() {
        for ((id, target) in world.targets) {

            val size = world.sizes[id] ?: continue
            val distance = target.distance ?: continue
            val targetId = target.targetId ?: continue
            val intent = target.intent ?: continue

            if (size.value >= distance) {
                world.eventBus.emit(EntitiesCollide(id, targetId, intent))
            }
        }
    }

    override fun reads(): List<KClass<*>> {
        TODO("Not yet implemented")
    }

    override fun writes(): List<KClass<*>> {
        TODO("Not yet implemented")
    }
}