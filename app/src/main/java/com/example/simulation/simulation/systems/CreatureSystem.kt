package com.example.simulation.simulation.systems

import com.example.simulation.simulation.AgeComponent
import com.example.simulation.simulation.EnergyComponent
import com.example.simulation.simulation.EntityId
import com.example.simulation.simulation.HistoryComponent
import com.example.simulation.simulation.PerceptionComponent
import com.example.simulation.simulation.PositionComponent
import com.example.simulation.simulation.SizeComponent
import com.example.simulation.simulation.StateComponent
import com.example.simulation.simulation.TargetComponent
import com.example.simulation.simulation.VelocityComponent
import com.example.simulation.simulation.World
import kotlin.reflect.KClass

data class Creature(
    val id: EntityId,
    val position: PositionComponent,
    val velocity: VelocityComponent,
    val energy: EnergyComponent,
    val state: StateComponent,
    val history: HistoryComponent,
    val age: AgeComponent,
    val size: SizeComponent,
    val perception: PerceptionComponent?
)

class CreatureSystem : System {
    override fun update(world: World, delta: Int) {

        for (entity in world.creatureTags) {
            world.ages[entity]?.age += delta
            world.sizes[entity]?.size += 0.01f * delta
            world.histories[entity]?.ticksSurvived += delta
        }
    }

    override fun reads(): List<KClass<*>> {
        return listOf(
            SizeComponent::class,
            HistoryComponent::class,
            AgeComponent::class
        )
    }

    override fun writes(): List<KClass<*>> {
        return listOf(
            SizeComponent::class,
            HistoryComponent::class,
            AgeComponent::class
        )
    }
}