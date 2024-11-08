package ru.quipy.logic.project

import ru.quipy.api.project.*
import ru.quipy.api.task.UserAssignedToTaskEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.logic.StatusEntity
import ru.quipy.logic.user.UserAggregateState
import java.util.*

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectTitle: String
    lateinit var creatorId: String
    var projectStatuses = mutableMapOf<UUID, StatusEntity>()
    val users: MutableList<UUID> = ArrayList<UUID>()

    override fun getId() = projectId

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun addUserToProject(event: UserAssignedToProjectEvent) {
        users.add(event.userId);
    }

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        projectStatuses[event.statusId] = StatusEntity(event.statusId, event.statusName)
        updatedAt = createdAt
    }
}