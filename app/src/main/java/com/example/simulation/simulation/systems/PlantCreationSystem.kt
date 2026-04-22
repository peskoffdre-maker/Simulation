package com.example.simulation.simulation.systems

import com.example.simulation.simulation.FertileZoneComponent
import com.example.simulation.simulation.World
import com.example.simulation.simulation.factories.PlantFactory
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.reflect.KClass


/**
 * Class manages creation of new plants in vegetation zones
 */
class PlantCreationSystem(
    private val world: World,
    private val width: Float,
    private val height: Float,
): System
{

    private val rng = world.rng
    val plantFactory = PlantFactory(world, width, height)

    override fun update() {
        for ((id, zone) in world.zones) {
            val cooldown = world.cooldowns[id] ?: return
            if(cooldown.cooldown < 0) {
                spawnPlant(zone, world)
                cooldown.cooldown = cooldown.maxCooldown
            }
        }
    }

    override fun reads(): List<KClass<*>> {
        return listOf()
    }

    override fun writes(): List<KClass<*>> {
        return listOf()
    }

    private fun spawnPlant(zone: FertileZoneComponent, world: World) {
        val spawnInZone = world.zones.isNotEmpty()
        if (spawnInZone) {
            val (x, y) = randomPointInZone(zone)
            plantFactory.spawnPlantAt(x, y)
        } else {
            plantFactory.spawnPlantAtRandomCoordinates()
        }
    }

    private fun randomPointInZone(zone: FertileZoneComponent): Pair<Float, Float> {
        val angle = rng.nextFloat() * (2 * Math.PI).toFloat()
        val distance = sqrt(rng.nextFloat()) * zone.radius

        val x = zone.cx + cos(angle) * distance
        val y = zone.cy + sin(angle) * distance

        return Pair(x, y)
    }

}