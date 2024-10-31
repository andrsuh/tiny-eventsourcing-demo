package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var name: String
    lateinit var creatorId: String
    var tasks = mutableSetOf<UUID>()
    var users = mutableSetOf<UUID>()
    var projectStatuses = mutableMapOf<UUID, StatusEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreated(event: ProjectCreatedEvent) {
        projectId = event.projectId
        name = event.title
        creatorId = event.creatorId
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusCreated(event: StatusCreatedEvent) {
        projectStatuses[event.statusId] = StatusEntity(event.statusId, event.statusName, event.statusColor)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun addUserToProject(event: UserAddedToProjectEvent) {
        users.add(event.userId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun changeName(event: ProjectNameChangedEvent) {
        name = event.projectName
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun deleteStatus(event: StatusDeletedEvent) {
        projectStatuses.remove(event.statusId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun addTask(event: TaskAddedEvent) {
        tasks.add(event.taskId)
        updatedAt = createdAt
    }
}

data class StatusEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val color: String
)