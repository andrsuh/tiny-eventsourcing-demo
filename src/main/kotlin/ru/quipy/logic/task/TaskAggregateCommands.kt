package ru.quipy.logic.task

import ru.quipy.api.task.*
import java.time.LocalDateTime
import java.util.*

fun TaskAggregateState.createTask(
        id: UUID,
        title: String,
        description: String,
        projectId: UUID,
        deadlineTimestamp: LocalDateTime,
        statusId: UUID
): TaskCreatedEvent {
    return TaskCreatedEvent(
            taskId = id,
            title = title,
            description = description,
            projectId = projectId,
            deadlineTimestamp = deadlineTimestamp,
            statusId = statusId
    )
}

fun TaskAggregateState.updateTitle(id: UUID, title: String): TaskTitleUpdatedEvent {
    return TaskTitleUpdatedEvent(
            taskId = id,
            newTitle = title
    )
}

fun TaskAggregateState.updateDescription(id: UUID, description: String): TaskDescriptionUpdatedEvent {
    return TaskDescriptionUpdatedEvent(
            taskId = id,
            newDescription = description
    )
}

fun TaskAggregateState.updateStatus(id: UUID, statusId: UUID): TaskStatusUpdatedEvent {
    return TaskStatusUpdatedEvent(
            taskId = id,
            newStatusId = statusId
    )
}

fun TaskAggregateState.addExecutor(executorId: UUID): TaskExecutorAddedEvent {
    if (taskExecutors.values.any { it.userId == executorId }) {
        throw IllegalArgumentException("User already exists: $executorId")
    }
    return TaskExecutorAddedEvent(
            taskId = this.getId(),
            executorId = executorId
    )
}

fun TaskAggregateState.deleteTask(projectId: UUID, id: UUID): TaskDeletedEvent {
    return TaskDeletedEvent(
            taskId = id,
            projectId = projectId
    )
}
