package com.example.simulation.simulation.systems

import com.example.simulation.simulation.PlantCooldownComponent
import com.example.simulation.simulation.SimulationConfiguration.cooldownPerTick
import com.example.simulation.simulation.World
import kotlin.reflect.KClass


/**
 * Class manages cooldowns.
 */
class CooldownSystem(private val world: World): System {
    override fun update() {
        world.cooldowns.values.forEach { it.cooldown -= cooldownPerTick }
        world.reproductions.values.forEach { it.cooldown -= cooldownPerTick }
    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            PlantCooldownComponent::class
        )
    }

    override fun writes(): List<KClass<*>> {
        return listOf(
            PlantCooldownComponent::class
        )
    }
}