package com.example.simulation.simulation

import com.example.simulation.simulation.SimulationConfiguration.creatureInitialSizeMultiplier


data class CreatureRenderModel(
    val id: Int,
    val x: Float,
    val y: Float,
    val size: Float,
)

data class PlantRenderModel(
    val id: Int,
    val x: Float,
    val y: Float
)

data class FoodRenderModel(
    val id: Int,
    val x: Float,
    val y: Float
)

data class RenderSnapshot(
    val creatures: List<CreatureRenderModel>,
    val plants: List<PlantRenderModel>,
    val foods: List<FoodRenderModel>,
    val population: Int
)

class SnapshotBuilder() {
    fun build (world: World): RenderSnapshot {

        val creatureList = mutableListOf<CreatureRenderModel>()
        val plantList = mutableListOf<PlantRenderModel>()
        val foodList = mutableListOf<FoodRenderModel>()

        for (creature in world.creatureTags) {

            val position = world.positions[creature] ?: continue
            val size = world.sizes[creature]?.size ?: creatureInitialSizeMultiplier

            creatureList.add(
                CreatureRenderModel(
                    id = creature,
                    x = position.x,
                    y = position.y,
                    size = size,
                )
            )
        }
        for (plant in world.plantTags) {
            val position = world.positions[plant] ?: continue

            plantList.add(
                PlantRenderModel(
                    id = plant,
                    x = position.x,
                    y = position.y
                )
            )
        }

        for (food in world.foodTags) {
            val position = world.positions[food] ?: continue

            foodList.add(
                FoodRenderModel(
                    id = food,
                    x = position.x,
                    y = position.y
                )
            )

        }
        val population = world.creatureTags.size
        return RenderSnapshot(creatureList, plantList, foodList, population)
    }
}