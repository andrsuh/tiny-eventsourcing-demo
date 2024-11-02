package ru.quipy.logic.state

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.entity.StatusEntity
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

    fun getParticipants(id: UUID) = participants

    fun getParticipantById(id: UUID) = participants.firstOrNull() { it == id }

    fun getName() = name

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        taskAndStatusAggregateId = event.tasksAndStatusAggregateId
        name = event.projectName
        updatedAt = createdAt
    }

//    @StateTransitionFunc
//    fun projectUpdatedApply(event: ProjectUpdatedEvent) {
//        name = event.projectName
//        updatedAt = createdAt
//    }

    @StateTransitionFunc
    fun participantAddApply(event: ParticipantAddedEvent) {
        if (participants.contains(event.userId))
            throw IllegalArgumentException("User ${event.userId} is already a participant of the project ${event.projectId}.")

        participants.add(element = event.userId)
        updatedAt = event.createdAt
    }
}

