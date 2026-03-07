package com.example.simulation.simulation.utils

import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.PositionComponent
import com.example.simulation.simulation.World
import kotlin.collections.iterator
import kotlin.math.ceil

class SpatialQueryService(
    private val world: World,
    private val cellSize: Float
) {
    private val cellGrid = mutableMapOf<Pair<Int, Int>, MutableList<EntityId>>()
    private val foodGrid = mutableMapOf<Pair<Int, Int>, MutableList<EntityId>>()

    fun rebuild(){
        foodGrid.clear()
        for (id in world.foodTags) {
            val pos = world.positions[id] ?: continue
            val cell = cellOf(pos.x, pos.y)
            foodGrid.getOrPut(cell) { mutableListOf() }.add(id)
        }
    }

    private fun cellOf(x: Float, y: Float): Pair<Int, Int> {
        val cx = (x / cellSize).toInt()
        val cy = (y / cellSize).toInt()
        return cx to cy
    }

    fun findClosestFood(
        from: EntityId,
        radius: Float
    ): Pair<EntityId, Float>? {

        val positions = world.positions
        val pos = positions[from] ?: return null
        val cell = cellOf(pos.x, pos.y)

        val range = ceil(radius / cellSize).toInt()
        val radiusSq = radius * radius

        var best: EntityId? = null
        var bestDist = Float.MAX_VALUE

        for (dx in -range..range) {
            for (dy in -range..range) {

                val bucket = foodGrid[cell.first + dx to cell.second + dy] ?: continue

                for (id in bucket) {
                    if (id == from) continue

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

    fun calculateDistanceSq(point1: PositionComponent, point2: PositionComponent): Float {
        val dx = point1.x - point2.x
        val dy = point1.y - point2.y
        return dx * dx + dy * dy
    }
}