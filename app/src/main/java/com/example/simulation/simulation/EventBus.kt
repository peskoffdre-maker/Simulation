package com.example.simulation.simulation

interface Event

data class EnergyDepletedEvent(val entityId: EntityId) : Event
data class EntityDiedOfAgeEvent(val entityId: EntityId) : Event
data class EntityKilled(val entityId: EntityId) : Event
data class EntitiesCollide(val entityId: EntityId, val targetId: EntityId, val intent: Intents) : Event

class EventBus {
    private val events = mutableListOf<Event>()

    fun emit(event: Event) {
        events.add(event)
    }

    fun getEvents(): List<Event> = events

    fun clear() {
        events.clear()
    }
}