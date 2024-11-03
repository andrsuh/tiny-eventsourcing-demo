package ru.quipy.logic.command

import ru.quipy.api.*
import ru.quipy.enum.ColorEnum
import ru.quipy.logic.state.ProjectAggregateState
import ru.quipy.logic.state.TaskAndStatusAggregateState
import java.util.*

fun TaskAndStatusAggregateState.createTask(id: UUID, name: String, description: String, projectId: UUID, statusId: UUID): TaskCreatedEvent {
    if (name.isEmpty()) {
        throw IllegalArgumentException("Task name should not be empty.")
    }
//    if (projectTags.values.any { it.name == name }) {
//        throw IllegalArgumentException("Task name already exists: $name")
//    }

    return TaskCreatedEvent(id, projectId, name, description, statusId)
}

fun TaskAndStatusAggregateState.updateTask(taskId: UUID, name: String, description: String): TaskUpdatedEvent {
    if (name.isEmpty()) {
        throw IllegalArgumentException("Task name should not be empty.")
    }

    return TaskUpdatedEvent(this.getId(), name, description)
}

fun TaskAndStatusAggregateState.addExecutor(taskId: UUID, userId: UUID): ExecutorAddedEvent {
    if (executors.contains(userId)) {
        throw IllegalArgumentException("User is already assigned as an executor for this task.")
    }

    return ExecutorAddedEvent(this.getId(), userId)
}


fun TaskAndStatusAggregateState.changeStatus(taskId: UUID, statusId: UUID): TaskStatusChangedEvent {
    return TaskStatusChangedEvent(this.getId(), statusId)
}

fun ProjectAggregateState.createStatus(statusId: UUID, statusName: String, projectId:UUID, color: ColorEnum): StatusCreatedEvent {


    return StatusCreatedEvent(projectId = this.getId(), statusId = statusId, statusName = statusName, color = color)
}

fun ProjectAggregateState.deleteStatus(statusId: UUID): StatusDeletedEvent {
    if (!projectStatuses.contains(statusId)) {
        throw IllegalArgumentException("Status not found!")
    }

    if (projectStatuses.size == 1) {
        throw IllegalArgumentException("Cannot delete the only status in the project.")
    }

    return StatusDeletedEvent(projectId = this.getId(), statusId = statusId)
}