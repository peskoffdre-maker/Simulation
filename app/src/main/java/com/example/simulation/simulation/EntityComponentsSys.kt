package com.example.simulation.simulation

import com.example.simulation.simulation.systems.Creature
import com.example.simulation.simulation.systems.Plant
import com.example.simulation.simulation.systems.ReproductiveCreature

typealias EntityId = Int


class EntityManager {
    private var nextId = 0
    fun create(): EntityId = nextId++
}

interface Component

data class PositionComponent(
    var x: Float,
    var y: Float
) : Component

data class VelocityComponent(
    var dx: Float,
    var dy: Float
) : Component

data class EnergyComponent(
    var energy: Float,
    val energyCost: Map<CreatureStates, Float> = mapOf(
        CreatureStates.HUNGER to 0.8f,
        CreatureStates.ROAMING to 2f,
        CreatureStates.IDLE to 0.2f,
    ),
) : Component

data class StateComponent(
    var state: CreatureStates
) : Component

data class PerceptionComponent(
    var detectionRadius: Float
) : Component

data class SizeComponent(
    var size: Float
) : Component

data class DietComponent(
    val diet: Diets
) : Component

data class ReproductionComponent(
    val energyThreshold: Int,
    val energyCost: Int,
    val fertility: Float,
    var cooldown: Int,
    val maxCooldown: Int
) : Component

data class HistoryComponent(
    var foodConsumed: Int = 0,
    var ticksSurvived: Int = 0,
    var distanceMoved: Float = 0f,
    var energySpent: Float = 0f,
    var energyGained: Int = 0,
    var generation: Int = 0
) : Component

class PlantTagComponent

data class AgeComponent(
    var age: Int,
    val maxAge: Int
) : Component

data class CooldownComponent(
    var maxCooldown: Int,
    var cooldown: Int
) : Component

data class FoodValueComponent(
    val energyValue: Int
) : Component

data class TargetComponent(
    var targetId: EntityId? = null,
    var distance: Float? = null
)

class World {
    val entityManager = EntityManager()

    val creatureTags = mutableSetOf<EntityId>()
    val positions = mutableMapOf<EntityId, PositionComponent>()
    val velocities = mutableMapOf<EntityId, VelocityComponent>()
    val energies = mutableMapOf<EntityId, EnergyComponent>()
    val states = mutableMapOf<EntityId, StateComponent>()
    val perceptions = mutableMapOf<EntityId, PerceptionComponent>()
    val sizes = mutableMapOf<EntityId, SizeComponent>()
    val diets = mutableMapOf<EntityId, DietComponent>()
    val reproductions = mutableMapOf<EntityId, ReproductionComponent>()
    val histories = mutableMapOf<EntityId, HistoryComponent>()
    val plantTags = mutableSetOf<EntityId>()
    val ages = mutableMapOf<EntityId, AgeComponent>()

    val targets = mutableMapOf<EntityId, TargetComponent>()

    val foodTags = mutableSetOf<EntityId>()
    val foodValues = mutableMapOf<EntityId, FoodValueComponent>()

    val cooldowns = mutableMapOf<EntityId, CooldownComponent>()

    val allComponents = listOf (
        positions,
        velocities,
        energies,
        states,
        perceptions,
        sizes,
        diets,
        reproductions,
        histories,
        ages,
        foodValues,
        cooldowns,
        targets
    )

    val allTags = listOf (
        creatureTags,
        plantTags,
        foodTags
    )

    fun getCreature(id: EntityId): Creature? {
        return Creature(
            id = id,
            position = positions[id] ?: return null,
            velocity = velocities[id] ?: return null,
            energy = energies[id] ?: return null,
            state = states[id] ?: return null,
            history = histories[id] ?: return null,
            age = ages[id] ?: return null,
            size = sizes[id] ?: return null,
            perception = perceptions[id]
        )
    }

    fun getPlant(id: EntityId): Plant? {
        return Plant(
            id = id,
            position = positions[id] ?: return null,
            age = ages[id] ?: return null,
            cooldown = cooldowns[id] ?: return null,
        )
    }

    fun getReproductiveCreature(id: EntityId): ReproductiveCreature? {
        return ReproductiveCreature(
            id = id,
            position = positions[id] ?: return null,
            velocity = velocities[id] ?: return null,
            perception = perceptions[id] ?: return null,
            size = sizes[id] ?: return null,
            diet = diets[id] ?: return null,
            reproduction = reproductions[id] ?: return null,
            history = histories[id] ?: return null,
            age = ages[id] ?: return null,
            energy = energies[id] ?: return null
        )
    }
}
