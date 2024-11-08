package ru.quipy.logic.task

import ru.quipy.api.task.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class TaskAggregateState : AggregateState<UUID, TaskAggregate> {
    private lateinit var taskId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var taskTitle: String

    lateinit var statusId: UUID
    lateinit var projectId: UUID
    var assignedUserId: UUID? = null;

    override fun getId() = taskId
    @StateTransitionFunc
    fun taskRenamedApply(event: TaskRenamedEvent) {
        taskTitle = event.title
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun userAssignedApply(event: UserAssignedToTaskEvent) {
        assignedUserId = event.userId
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        taskId = event.taskId
        taskTitle = event.taskTitle
        projectId = event.projectId
        statusId = event.statusId
    }

    @StateTransitionFunc
    fun statusAssignedApply(event: StatusAssignedToTaskEvent) {
        statusId = event.statusId
        updatedAt = createdAt
    }
}

