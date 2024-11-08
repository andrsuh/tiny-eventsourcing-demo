package ru.quipy.logic.task

import ru.quipy.api.task.StatusAssignedToTaskEvent
import ru.quipy.api.task.TaskCreatedEvent
import ru.quipy.api.task.TaskRenamedEvent
import ru.quipy.api.task.UserAssignedToTaskEvent
import java.util.*

fun TaskAggregateState.renameTask(title: String): TaskRenamedEvent {
    return TaskRenamedEvent(
        taskId = this.getId(),
        title = title,
    )
}

fun TaskAggregateState.assignUserToTask(userId: UUID): UserAssignedToTaskEvent {
    return UserAssignedToTaskEvent(
        taskId = this.getId(),
        userId = userId,
    )
}

fun TaskAggregateState.assignStatusToTask(statusId: UUID): StatusAssignedToTaskEvent {
    return StatusAssignedToTaskEvent(
            taskId = this.getId(),
            statusId = statusId
    )
}

fun TaskAggregateState.create(id: UUID, projectId: UUID, statusId: UUID, taskTitle: String) : TaskCreatedEvent {
    return TaskCreatedEvent(
        projectId = projectId,
        taskId = id,
        statusId = statusId,
        taskTitle = taskTitle,
    )
}

