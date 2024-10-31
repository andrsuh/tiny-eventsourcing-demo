package ru.quipy.logic

import ru.quipy.api.ExecutorsAddedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskEditedEvent
import ru.quipy.api.TaskStatusAssignedEvent
import java.util.*

fun TaskAggregateState.create(
    projectId: UUID,
    taskId: UUID,
    taskTitle: String,
    executorId: UUID,
    statusId: UUID,
): TaskCreatedEvent {
    return TaskCreatedEvent(
        projectId = projectId,
        taskId = taskId,
        taskTitle = taskTitle,
        executorId = executorId,
        statusId = statusId
    )
}

fun TaskAggregateState.addExecutors(executorsIds: Set<UUID>): ExecutorsAddedEvent {
    return ExecutorsAddedEvent(
        taskId = this.getId(),
        executorsIds = executorsIds
    )
}

fun TaskAggregateState.edit(title: String): TaskEditedEvent {
    return TaskEditedEvent(
        taskId = this.getId(),
        title = title
    )
}

fun TaskAggregateState.assignStatus(statusId: UUID): TaskStatusAssignedEvent {
    return TaskStatusAssignedEvent(
        taskId = this.getId(),
        statusId = statusId
    )
}