package com.example.simulation.simulation.utils

import com.example.simulation.simulation.CreatureStates
import com.example.simulation.simulation.Diets
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.PositionComponent
import com.example.simulation.simulation.World
import kotlin.math.ceil

typealias SpatialGrid = Map<Long, MutableList<EntityId>>

class SpatialQueryService(
    private val world: World,
    private val cellSize: Float
) {

    private val _foodGrid = mutableMapOf<Long, MutableList<EntityId>>()
    val foodGrid: SpatialGrid
            get() = _foodGrid

    private val _creatureGrid = mutableMapOf<Long, MutableList<EntityId>>()
    val creatureGrid: SpatialGrid
        get() = _creatureGrid

    fun rebuild(){
        _creatureGrid.clear()
        _foodGrid.clear()
        for (id in world.foodTags) {
            val pos = world.positions[id] ?: continue
            val key = cellKeyOf(pos.x, pos.y)
            _foodGrid.getOrPut(key) { mutableListOf() }.add(id)
        }

        for (id in world.creatureTags) {
            val pos = world.positions[id] ?: continue

            val key = cellKeyOf(pos.x, pos.y)
            _creatureGrid.getOrPut(key) { mutableListOf() }.add(id)
        }
    }

    private fun cellKeyOf(x: Float, y: Float): Long {
        val cx = (x / cellSize).toInt()
        val cy = (y / cellSize).toInt()
        return cellKey(cx, cy)
    }

    private fun cellKey(x: Int, y: Int): Long {
        return (x.toLong() shl 32) or (y.toLong() and 0xffffffff)
    }

    private fun findClosestTargetIn(
        grid: SpatialGrid,
        from: EntityId,
        radius: Float,
        predicate: (EntityId) -> Boolean = {true},
    ): Pair<EntityId, Float>? {

        val positions = world.positions
        val pos = positions[from] ?: return null
        val key = cellKeyOf(pos.x, pos.y)

        val range = ceil(radius / cellSize).toInt()
        val radiusSq = radius * radius

        var best: EntityId? = null
        var bestDist = Float.MAX_VALUE
        for (dx in -range..range) {
            for (dy in -range..range) {
                val cell = cellKey(dx, dy)
                val bucket = grid[cell + key] ?: continue
                for (id in bucket) {
                    if (id == from) continue

                    if (!predicate(id)) continue

                    val other = positions[id] ?: continue

                    val dist = calculateDistanceSq(pos, other)

                    if (dist < bestDist && dist <= radiusSq) {
                        bestDist = dist
                        best = id
                    }
                }
            }
        }
        return best?.let { it to bestDist }
    }

    fun findClosestMale(from: EntityId, radius: Float): Pair<EntityId, Float>? {
        val fromDiet = world.diets[from]?.diet ?: return null

        return findClosestTargetIn(creatureGrid, from, radius) { id ->
            val sex = world.sexes[id]?.value
            val diet = world.diets[id]?.diet
            val canReproduce = world.states[id]?.state == CreatureStates.REPRODUCTION

            sex == false && diet == fromDiet && canReproduce
        }
    }

    fun findClosestFemale(from: EntityId, radius: Float): Pair<EntityId, Float>? {
        val fromDiet = world.diets[from]?.diet ?: return null

        return findClosestTargetIn(creatureGrid, from, radius) { id ->
            val sex = world.sexes[id]?.value
            val diet = world.diets[id]?.diet
            val canReproduce = world.states[id]?.state == CreatureStates.REPRODUCTION

            sex == true && diet == fromDiet && canReproduce
        }
    }

    fun findClosestPrey(from: EntityId, radius: Float): Pair<EntityId, Float>? {
        return findClosestTargetIn(creatureGrid, from, radius) {
            id ->
                val hunterPower = world.powers[from]?.value ?: 0f
                val preyPower = world.powers[id]?.value ?: 0f

                world.diets[id]?.diet == Diets.HERBIVORE &&
                        preyPower <= hunterPower
        }
    }

    fun findClosestFood(from: EntityId, radius: Float): Pair<EntityId, Float>? {
        return findClosestTargetIn(foodGrid, from, radius)
    }



    private fun calculateDistanceSq(point1: PositionComponent, point2: PositionComponent): Float {
        val dx = point1.x - point2.x
        val dy = point1.y - point2.y
        return dx * dx + dy * dy
    }
}