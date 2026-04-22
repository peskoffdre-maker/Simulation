package com.example.simulation.simulation.systems

import com.example.simulation.simulation.World
import com.example.simulation.simulation.utils.SpatialQueryService
import kotlin.reflect.KClass

class SpatialIndexSystem(
    private val spatial: SpatialQueryService
) : System {
    override fun update() {
        spatial.rebuild()
    }

    override fun reads(): List<KClass<*>> {
        return listOf()
    }

    override fun writes(): List<KClass<*>> {
        return listOf()
    }
}