package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic

class ProjectAggregateState: AggregateState<UUID, ProjectAggregate> {
    val DEFAULT_COLOR = "#000000"
    val DEFAULT_STATUS = StatusEntity(
        name = "Default name",
        color = DEFAULT_COLOR
    )

    private lateinit var projectId: UUID
    lateinit var name: String
    lateinit var ownerId: UUID

    var members: MutableSet<UUID> = mutableSetOf()
    var tasks: MutableMap<UUID, TaskEntity> = mutableMapOf()
    var statuses: MutableMap<UUID, StatusEntity> = mutableMapOf()
    lateinit var defaultStatusId: UUID

    override fun getId(): UUID = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun createdApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        name = event.projectName
        ownerId = event.ownerId
        statuses[DEFAULT_STATUS.statusId] = DEFAULT_STATUS
        defaultStatusId = DEFAULT_STATUS.statusId
    }

    @StateTransitionFunc
    fun userAddedApply(event: ProjectUserAddedEvent) {
        members.add(event.userId)
    }

    @StateTransitionFunc
    fun userRemovedApply(event: ProjectUserRemovedEvent) {
        members.remove(event.userId)
    }

    @StateTransitionFunc
    fun createdTaskApply(event: ProjectTaskCreatedEvent) {
        val task = TaskEntity(
            ownerId = event.userId,
            name = event.taskName,
            statusId = TODO(),
            executors = mutableSetOf()
        )
        tasks[task.taskId] = task
    }

    @StateTransitionFunc
    fun modifiedTaskApply(event: ProjectTaskModifiedEvent) {
        val task = tasks[event.taskId] ?: return
        task.statusId = event.statusId ?: task.statusId
        task.executors = event.executors ?: task.executors
        task.name = event.taskName ?: task.name
    }

    @StateTransitionFunc
    fun statusCreatedApply(event: ProjectStatusCreatedEvent) {
        val status = StatusEntity(
            name = event.statusName,
            color = DEFAULT_COLOR
        )
        statuses[status.statusId] = status
    }

    @StateTransitionFunc
    fun statusDefaultModifiedApply(event: ProjectStatusDefaultModifiedEvent) {
        defaultStatusId = event.statusId
    }

    @StateTransitionFunc
    fun statusRemovedApply(event: ProjectStatusRemovedEvent) {
        statuses.remove(event.statusId)
    }
}

data class TaskEntity(
    val taskId: UUID = UUID.randomUUID(),
    val ownerId: UUID,
    var name: String,
    var statusId: UUID,
    var executors: MutableSet<UUID> = mutableSetOf()
)

data class StatusEntity(
    val statusId: UUID = UUID.randomUUID(),
    val name: String,
    var color: String
)
