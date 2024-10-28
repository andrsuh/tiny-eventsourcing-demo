package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class ParticipantAggregateState : AggregateState<UUID, ParticipantAggregate> {
    private lateinit var participantId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    var projects = mutableListOf<UUID>()

    override fun getId() = participantId

    fun create(id: UUID, projectId: UUID): ParticipantCreatedEvent {
        return ParticipantCreatedEvent(id, projectId)
    }

    fun addProject(projectId: UUID) : ParticipantAddedToProjectEvent {
        if (projects.contains(getId())) {
            throw IllegalStateException("The user ${getId()} is already a member of the project $projectId")
        }

        return ParticipantAddedToProjectEvent(projectId)
    }

    @StateTransitionFunc
    fun participantCreatedApply(event: ParticipantCreatedEvent) {
        participantId = event.participantId
        projects = mutableListOf(event.projectId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun participantAddedToProjectApply(event: ParticipantAddedToProjectEvent) {
        projects.add(event.projectId)
        updatedAt = createdAt
    }
}