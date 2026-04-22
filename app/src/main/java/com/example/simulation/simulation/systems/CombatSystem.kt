package com.example.simulation.simulation.systems

import com.example.simulation.simulation.EntitiesCollide
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.EntityKilled
import com.example.simulation.simulation.Intents
import com.example.simulation.simulation.World
import kotlin.reflect.KClass

class CombatSystem(private val world: World) : System {

    private val killedEntities = mutableListOf<EntityId>()

    override fun update() {
        killedEntities.clear()

        for (event in world.eventBus.getEvents()) {
            if (event is EntitiesCollide) {
                if (event.intent == Intents.ATTACK) {
                    killedEntities.add(event.targetId)
                }
            }
        }
        kill()
    }

    override fun reads(): List<KClass<*>> {
        TODO("Not yet implemented")
    }

    override fun writes(): List<KClass<*>> {
        TODO("Not yet implemented")
    }

    private fun kill() {
        for (entity in killedEntities) {
            world.eventBus.emit(EntityKilled(entity))
        }
    }
}