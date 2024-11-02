package ru.quipy.logic.command

import ru.quipy.api.*
import ru.quipy.logic.state.ProjectAggregateState
import ru.quipy.logic.state.TaskAndStatusAggregateState
import java.util.*

fun TaskAndStatusAggregateState.createTask(id: UUID, projectId: UUID, name: String, description: String): TaskCreatedEvent {
    if (name.isEmpty()) {
        throw IllegalArgumentException("Task name should not be empty.")
    }
//    if (projectTags.values.any { it.name == name }) {
//        throw IllegalArgumentException("Task name already exists: $name")
//    }

    return TaskCreatedEvent(id, projectId, name, description)
}
//fun ProjectAggregateState.createTag(name: String): TagCreatedEvent {
//    if (projectTags.values.any { it.name == name }) {
//        throw IllegalArgumentException("Tag already exists: $name")
//    }
//    return TagCreatedEvent(projectId = this.getId(), tagId = UUID.randomUUID(), tagName = name)
//}
//
//fun ProjectAggregateState.assignTagToTask(tagId: UUID, taskId: UUID): TagAssignedToTaskEvent {
//    if (!projectTags.containsKey(tagId)) {
//        throw IllegalArgumentException("Tag doesn't exists: $tagId")
//    }
//
//    if (!tasks.containsKey(taskId)) {
//        throw IllegalArgumentException("Task doesn't exists: $taskId")
//    }
//
//    return TagAssignedToTaskEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
//}

fun TaskAndStatusAggregateState.updateTask(name: String, description: String): TaskUpdatedEvent {
    if (name.isEmpty()) {
        throw IllegalArgumentException("Task name should not be empty.")
    }

    return TaskUpdatedEvent(this.getId(), name, description)
}

fun TaskAndStatusAggregateState.addExecutor(userId: UUID): ExecutorAddedEvent {
    if (executors.contains(userId)) {
        throw IllegalArgumentException("User is already assigned as an executor for this task.")
    }

    return ExecutorAddedEvent(this.getId(), userId)
}


fun TaskAndStatusAggregateState.changeStatus(statusId: UUID): TaskStatusChangedEvent {
    return TaskStatusChangedEvent(this.getId(), statusId)
}

fun ProjectAggregateState.createStatus(statusId: UUID, statusName: String, color: String): StatusCreatedEvent {
    if (statusName.length > 255) {
        throw IllegalArgumentException("Status name should be less than 255 characters!")
    }

    if (projectStatuses.size == 50) {
        throw IllegalArgumentException("Maximum number of statuses reached for the project.")
    }

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