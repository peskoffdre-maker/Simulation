package com.example.simulation.simulation.systems

import com.example.simulation.simulation.CreatureStates
import com.example.simulation.simulation.DietComponent
import com.example.simulation.simulation.Diets
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.Intents
import com.example.simulation.simulation.PerceptionComponent
import com.example.simulation.simulation.StateComponent
import com.example.simulation.simulation.TargetComponent
import com.example.simulation.simulation.World
import com.example.simulation.simulation.utils.SpatialQueryService
import com.example.simulation.simulation.utils.SpeciesCompatibilityService
import kotlin.math.sqrt
import kotlin.reflect.KClass

class TargetSeekingSystem(
    private val world: World,
    private val spatial: SpatialQueryService
) : System {

    private val compatibilityService = SpeciesCompatibilityService(world)

    override fun update() {
        for (id in world.creatureTags) {

            val perception = world.perceptions[id] ?: continue
            val target = world.targets[id] ?: continue
            val state = world.states[id]?.state ?: continue
            val diet = world.diets[id]?.diet ?: continue

            if (state == CreatureStates.HUNGER) {
                if (diet == Diets.HERBIVORE || diet == Diets.OMNIVORE) {
                    seekFood(id, target, perception.detectionRadius)
                } else if (diet == Diets.CARNIVORE) {
                    seekPrey(id, target, perception.detectionRadius)
                }
            } else if (state == CreatureStates.REPRODUCTION) {
                seekMate(id, target, perception.mateRadius)
            }
        }
    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            DietComponent::class,
            StateComponent::class,
            PerceptionComponent::class,
            TargetComponent::class,
        )
    }

    override fun writes(): List<KClass<*>> {
        return listOf(
            TargetComponent::class,
        )
    }

    private fun seekFood(id: EntityId, target: TargetComponent, perception: Float) {
        val (nearestTargetId, nearestTargetDistance) = spatial.findClosestFood(
            from = id,
            radius = perception
        ) ?: return

        target.targetId = nearestTargetId
        target.distance = sqrt(nearestTargetDistance) // TODO consider store squared distance
        target.intent = Intents.EAT
    }

    private fun seekPrey(id: EntityId, target: TargetComponent, perception: Float) {

        val (nearestTargetId, nearestTargetDistance) = spatial.findClosestPrey(
            from = id,
            radius = perception
        ) ?: return

        val targetState = world.states[nearestTargetId]?.state ?: return

        target.targetId = nearestTargetId
        target.distance = sqrt(nearestTargetDistance) // TODO consider store squared distance
        target.intent = if (targetState == CreatureStates.DECAYING) Intents.EAT else Intents.ATTACK
    }

    private fun seekMate(id: EntityId, target: TargetComponent, perception: Float) {

        fun seekMale() : Pair<EntityId, Float>? {
            return spatial.findClosestMale(
                from = id,
                radius = perception
            )
        }

        fun seekFemale() : Pair<EntityId, Float>? {
            return spatial.findClosestFemale(
                from = id,
                radius = perception
            )
        }

        val isFemale = world.sexes[id]?.value ?: return
        val (nearestTargetId, nearestTargetDistance) =
            if (isFemale) {
                seekMale()
            } else {
                seekFemale()
            } ?: return

        val genome1 = world.genomes[id] ?: return
        val genome2 = world.genomes[nearestTargetId] ?: return
        if (!compatibilityService.checkIfCompatible(genome1, genome2)) return

        target.targetId = nearestTargetId
        target.distance = sqrt(nearestTargetDistance) // TODO consider store squared distance
        target.intent = Intents.REPRODUCE
    }


}