package ru.quipy.logic

import ru.quipy.api.*
import java.util.*

fun TaskAndStatusAggregateStatec.createTask(id: UUID, projectId: UUID, name: String, description: String, color: String): TaskCreatedEvent {
    if (name.isEmpty()) {
        throw IllegalArgumentException("Task name should not be empty.")
    }
//    if (projectTags.values.any { it.name == name }) {
//        throw IllegalArgumentException("Task name already exists: $name")
//    }

    return TaskCreatedEvent(id, projectId, name, description, color)
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

fun TaskAndStatusAggregateStatec.updateTask(name: String, description: String): TaskUpdatedEvent {
    if (name.isEmpty()) {
        throw IllegalArgumentException("Task name should not be empty.")
    }

    return TaskUpdatedEvent(this.getId(), name, description)
}

fun TaskAndStatusAggregateStatec.addExecutor(userId: UUID): ExecutorAddedEvent {
    if (executors.contains(userId)) {
        throw IllegalArgumentException("User is already assigned as an executor for this task.")
    }

    return ExecutorAddedEvent(this.getId(), userId)
}

fun TaskAndStatusAggregateStatec.assignStatus(statusId: UUID): StatusAssignedToTaskEvent {
    return StatusAssignedToTaskEvent(this.getId(), statusId)
}

fun TaskAndStatusAggregateStatec.removeStatus(): StatusRemovedFromTaskEvent {
    return StatusRemovedFromTaskEvent(this.getId())
}

fun TaskAndStatusAggregateStatec.changeStatus(statusId: UUID): TaskStatusChangedEvent {
    return TaskStatusChangedEvent(this.getId(), statusId)
}