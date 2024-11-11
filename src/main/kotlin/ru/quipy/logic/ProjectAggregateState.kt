package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    val DEFAULT_COLOR = "#000000"
//    val DEFAULT_STATUS;

    private lateinit var projectId: UUID
    lateinit var name: String
    lateinit var ownerId: UUID

    var members: MutableSet<UUID> = mutableSetOf()
    var tasks: MutableMap<UUID, TaskEntity> = mutableMapOf()
    var statuses: MutableMap<UUID, StatusEntity> = mutableMapOf()
//    lateinit var default_status_id: UUID;
    lateinit var DEFAULT_STATUS: StatusEntity;

    override fun getId(): UUID = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun createdApply(event: ProjectCreatedEvent) {

        val default_status = StatusEntity(
            name = "Default name",
            statusId = event.default_status_id,
            color = DEFAULT_COLOR
        )

        projectId = event.projectId
        name = event.projectName
        ownerId = event.ownerId
        statuses[DEFAULT_STATUS.statusId] = default_status
        DEFAULT_STATUS = default_status
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
            taskId = event.taskId,
            ownerId = event.userId,
            name = event.taskName,
            statusId = DEFAULT_STATUS.statusId,
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
            statusId = event.statusId,
            name = event.statusName,
            color = DEFAULT_COLOR
        )
        statuses[status.statusId] = status
    }

    @StateTransitionFunc
    fun statusRemovedApply(event: ProjectStatusRemovedEvent) {
        statuses.remove(event.statusId)
    }
}

data class TaskEntity(
    val taskId: UUID,
    val ownerId: UUID,
    var name: String,
    var statusId: UUID,
    var executors: MutableSet<UUID> = mutableSetOf()
)

data class StatusEntity(
    val statusId: UUID,
    val name: String,
    var color: String
)
