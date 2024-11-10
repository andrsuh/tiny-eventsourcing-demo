package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(id: UUID, title: String, creatorId: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        creatorId = creatorId,
    )
}

fun ProjectAggregateState.update(title: String, description: String): ProjectUpdatedEvent {
    return ProjectUpdatedEvent(projectId = this.getId(), title = title, description = description)
}

fun ProjectAggregateState.addParticipant(taskId: UUID, userId: UUID): ProjectUserAddedEvent {
    return ProjectUserAddedEvent(projectId = taskId, userId = userId)
}

fun ProjectAggregateState.removeParticipant(taskId: UUID, userId: UUID): ProjectUserRemovedEvent {
    return ProjectUserRemovedEvent(projectId = taskId, userId = userId)
}

fun ProjectAggregateState.addTask(name: String): TaskCreatedEvent {
    return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name)
}

fun ProjectAggregateState.updateTask(taskId: UUID, taskName: String, taskDescription: String): TaskUpdatedEvent {
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task with id $taskId doesn't exist")
    }

    return TaskUpdatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName, taskDescription)
}

fun ProjectAggregateState.addExecutor(taskId: UUID, userId: UUID) : TaskExecutorAddedEvent {
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task with id $taskId doesn't exist")
    }

    if (!participants.containsKey(userId)) {
        throw IllegalArgumentException("User with id $userId is not a participant of the project")
    }

    return TaskExecutorAddedEvent(projectId = this.getId(), taskId = taskId, userId = userId)
}

fun ProjectAggregateState.removeTask(taskId: UUID): TaskDeletedEvent {
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task with id $taskId doesn't exist")
    }
    return TaskDeletedEvent(projectId = this.getId(), taskId = taskId)
}

fun ProjectAggregateState.createTag(name: String): TagCreatedEvent {
    if (projectTags.values.any { it.name == name }) {
        throw IllegalArgumentException("Tag already exists: $name")
    }
    return TagCreatedEvent(projectId = this.getId(), tagId = UUID.randomUUID(), tagName = name)
}

fun ProjectAggregateState.assignTagToTask(tagId: UUID, taskId: UUID): TagAssignedToTaskEvent {
    if (!projectTags.containsKey(tagId)) {
        throw IllegalArgumentException("Tag doesn't exists: $tagId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return TagAssignedToTaskEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
}