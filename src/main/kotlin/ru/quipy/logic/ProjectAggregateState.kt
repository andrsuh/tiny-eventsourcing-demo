package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*


class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    private var createdAt: Long = System.currentTimeMillis()
    private var updatedAt: Long = System.currentTimeMillis()

    private lateinit var projectTitle: String
    private var participantsIds: MutableList<UUID> = mutableListOf()
    private var projectStatuses: MutableMap<Pair<UUID, UUID>, StatusEntity> = mutableMapOf()

    override fun getId() = projectId

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        participantsIds.add(event.creatorId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun participantAddedApply(event: ParticipantAddedEvent) {
        projectId = event.projectId
        participantsIds.add(event.participantId)
    }

    @StateTransitionFunc
    fun editProjectTitleApply(event: EditProjectTitleEvent) {
        projectId = event.projectId
        projectTitle = event.title
    }

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        val key: Pair<UUID, UUID> = Pair(event.projectId, event.statusId)
        projectStatuses[key] = StatusEntity(event.statusId, event.projectId, event.statusName, event.color)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        val key: Pair<UUID, UUID> = Pair(event.projectId, event.statusId)
        projectStatuses.remove(key)
    }
}

data class StatusEntity(
    val id: UUID = UUID.randomUUID(),
    val projectId: UUID,
    val title: String,
    val color: String
)
