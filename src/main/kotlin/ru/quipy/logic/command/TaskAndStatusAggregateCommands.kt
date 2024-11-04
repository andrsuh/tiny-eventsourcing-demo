package ru.quipy.logic.command

import javassist.NotFoundException
import ru.quipy.api.*
import ru.quipy.enum.ColorEnum
import ru.quipy.logic.state.TaskAndStatusAggregateState
import java.util.UUID

fun TaskAndStatusAggregateState.createTask(
        id: UUID,
        name: String,
        description: String,
        projectId: UUID,
        statusId: UUID
): TaskCreatedEvent {
    if (name.isEmpty()) {
        throw IllegalArgumentException("Task name should not be empty.")
    }
    if (getTasksByName(name).isNotEmpty()) {
        throw IllegalArgumentException("Task with name $name already exists.")
    }

    return TaskCreatedEvent(
            taskId = id,
            taskName = name,
            description = description,
            projectId = projectId,
            statusId = statusId,
    )
}

fun TaskAndStatusAggregateState.updateTask(
        taskId: UUID,
        name: String,
        description: String
): TaskUpdatedEvent {
    if (name.isEmpty()) {
        throw IllegalArgumentException("Task name should not be empty.")
    }
    if (getTasksByName(name).isNotEmpty()) {
        throw IllegalArgumentException("Task with name $name already exists.")
    }

    return return TaskUpdatedEvent(
            taskId = taskId,
            taskName = name,
            description = description,
    )
}

fun TaskAndStatusAggregateState.addExecutor(
        taskId: UUID,
        userId: UUID
): ExecutorAddedEvent {
    val task = getTaskById(taskId) ?: throw NotFoundException("Task with id $taskId was not found.")

    if (task.executors.contains(userId)) {
        throw IllegalArgumentException("User is already an executor of this task.")
    }

    return ExecutorAddedEvent(taskId, userId)
}


fun TaskAndStatusAggregateState.changeStatus(taskId: UUID, statusId: UUID): TaskStatusChangedEvent {
    return TaskStatusChangedEvent(
            taskId = taskId,
            statusId = statusId,
    )
}

fun TaskAndStatusAggregateState.createStatus(
        statusId: UUID,
        statusName: String,
        projectId: UUID,
        color: ColorEnum): StatusCreatedEvent {
    if (getStatusesByName(statusName).isNotEmpty()) {
        throw IllegalArgumentException("Status $statusName already exists.")
    }

    return StatusCreatedEvent(
            projectId = projectId,
            statusId = statusId,
            statusName = statusName,
            color = color
    )
}

fun TaskAndStatusAggregateState.deleteStatus(statusId: UUID): StatusDeletedEvent {
    if (getStatusById(statusId) == null) {
        throw NotFoundException("Status was not found.")
    }

    if (getStatuses().size == 1) {
        throw IllegalStateException("Can not delete the only status in the project.")
    }

    if (getTasks().any { it.statusId == statusId })
        throw IllegalStateException("Tasks with status $statusId exist.")

    return StatusDeletedEvent(statusId = statusId)
}

fun TaskAndStatusAggregateState.changeTaskStatusPosition(
        statusId: UUID,
        position: Int,
): StatusPositionChangedEvent {
    return StatusPositionChangedEvent(
            statusId = statusId,
            position = position,
    )
}

