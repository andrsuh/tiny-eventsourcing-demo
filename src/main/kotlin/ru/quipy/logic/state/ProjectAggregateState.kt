package ru.quipy.logic.state

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*


class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    private lateinit var taskAndStatusAggregateId: UUID
    private lateinit var name: String


    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    val participants = mutableListOf<UUID>()

    override fun getId() = projectId
    fun getTaskAndStatusAggregateId() = taskAndStatusAggregateId

    fun getParticipants() = participants

    fun getParticipantById(id: UUID) = participants.firstOrNull() { it == id }

    fun getName() = name

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        name = event.projectName
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun participantAddedApply(event: ParticipantAddedEvent) {
        participants.add(element = event.userId)
        updatedAt = event.createdAt
    }
}

