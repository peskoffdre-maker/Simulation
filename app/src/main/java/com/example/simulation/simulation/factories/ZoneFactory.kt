package com.example.simulation.simulation.factories

import com.example.simulation.simulation.PlantCooldownComponent
import com.example.simulation.simulation.FertileZoneComponent
import com.example.simulation.simulation.SimulationConfiguration.fertileZoneRadius
import com.example.simulation.simulation.SimulationConfiguration.zoneSpawnCD
import com.example.simulation.simulation.World
import kotlin.random.Random


/**
 * Class creates vegetation zones
 */
class ZoneFactory(
    private val world: World,
    private val width: Float,
    private val height: Float
) {

    private val rng = world.rng
    fun createNewZone(){
        val zoneId = world.entityManager.create()
        val newZone = FertileZoneComponent(
            cx = rng.nextFloat() * width,
            cy = rng.nextFloat() * height,
            radius = fertileZoneRadius,
            weight = rng.nextFloat()
        )
        val newCooldown = PlantCooldownComponent(zoneSpawnCD, 0)
        world.zoneTags.add(zoneId)
        world.cooldowns[zoneId] = newCooldown
        world.zones[zoneId] = newZone
    }
}