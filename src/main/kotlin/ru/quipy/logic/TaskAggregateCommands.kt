package ru.quipy.logic

import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskNameChangedEvent
import ru.quipy.api.TaskStatusChangedEvent
import ru.quipy.api.UserAddedToTaskEvent
import java.util.*


fun TaskAggregateState.createTask(
    projectId: UUID,
    name: String,
    description: String,
    creatorId: UUID
): TaskCreatedEvent {
    return TaskCreatedEvent(
        projectId = projectId,
        taskId = UUID.randomUUID(),
        taskName = name,
        description = description,
        creatorId = creatorId
    )
}

fun TaskAggregateState.assignStatusToTask(statusId: UUID, taskId: UUID): TaskStatusChangedEvent {
    if (this.statusId == statusId) {
        error("This status already assigned")
    }
    return TaskStatusChangedEvent(statusId = statusId, taskId = taskId)
}

fun TaskAggregateState.assignUser(userId: UUID, taskId: UUID): UserAddedToTaskEvent {
    if (users.contains(userId)) {
        error("This user has been already assigned to given task")
    }
    return UserAddedToTaskEvent(taskId = taskId, userId = userId)
}

fun TaskAggregateState.changeName(taskId: UUID, name: String): TaskNameChangedEvent {
    if (this.taskName == name) {
        error("This name already assigned")
    }
    return TaskNameChangedEvent(taskId = taskId, taskName = name)
}