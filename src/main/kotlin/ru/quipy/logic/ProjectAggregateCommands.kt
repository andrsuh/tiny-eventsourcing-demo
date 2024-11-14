package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(id: UUID, title: String, creatorId: String): ProjectCreatedEvent {
    if (title.isBlank() || title.isEmpty()) {
        throw IllegalArgumentException("Project title must contain symbols")
    }

    if (creatorId.isBlank() || creatorId.isEmpty()) {
        throw IllegalArgumentException("Invalid creator id: $creatorId")
    }

    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        creatorId = UUID.fromString(creatorId),
    )
}

fun ProjectAggregateState.update(title: String, description: String): ProjectUpdatedEvent {
    if (title.isBlank() || title.isEmpty()) {
        throw IllegalArgumentException("New project title must contain symbols")
    }

    if (description.isBlank() || description.isEmpty()) {
        throw IllegalArgumentException("Project description must contain symbols")
    }

    return ProjectUpdatedEvent(projectId = this.getId(), title = title, description = description)
}

fun ProjectAggregateState.addMember(userId: UUID): ProjectMemberCreatedEvent {
    if (participants.containsKey(userId)) {
        throw IllegalArgumentException("User with id $userId is already added to the project")
    }

    return ProjectMemberCreatedEvent(projectId = this.getId(), userId = userId)
}

fun ProjectAggregateState.removeMember(userId: UUID): ProjectMemberRemovedEvent {
    if (!participants.containsKey(userId)) {
        throw IllegalArgumentException("User with id $userId is not a member of the project")
    }

    return ProjectMemberRemovedEvent(projectId = this.getId(), userId = userId)
}

fun ProjectAggregateState.addTask(name: String): TaskCreatedEvent {
    if (name.isBlank() || name.isEmpty()) {
        throw IllegalArgumentException("Task name must contain symbols")
    }

    return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name)
}

fun ProjectAggregateState.updateTask(taskId: UUID, taskName: String, taskDescription: String): TaskUpdatedEvent {
    if (taskName.isBlank() || taskName.isEmpty()) {
        throw IllegalArgumentException("Task name must contain symbols")
    }

    if (taskDescription.isBlank() || taskDescription.isEmpty()) {
        throw IllegalArgumentException("Task description must contain symbols")
    }

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

fun ProjectAggregateState.createTag(name: String, color: String, creatorId: UUID): TagCreatedEvent {
    if (name.isBlank() || name.isEmpty()) {
        throw IllegalArgumentException("Tag name must contain symbols")
    }

    if (projectTags.values.any { it.name == name }) {
        throw IllegalArgumentException("Tag already exists: $name")
    }

    return TagCreatedEvent(tagId = UUID.randomUUID(), projectId = this.getId(), tagName = name, tagColor = color, creatorId = creatorId)
}

fun ProjectAggregateState.removeTag(tagId: UUID): TagDeletedEvent {
    if (tasks.values.any { it.tagsAssigned.contains(tagId) }) {
        throw IllegalArgumentException("Tag to delete cannot be assigned to project task")
    }

    return TagDeletedEvent(tagId = tagId, projectID = this.getId())
}

fun ProjectAggregateState.assignTagToTask(taskId: UUID, tagId: UUID): TagAssignedToTaskEvent {
    if (!projectTags.containsKey(tagId)) {
        throw IllegalArgumentException("Tag doesn't exists: $tagId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return TagAssignedToTaskEvent(projectId = this.getId(), taskId = taskId, tagId = tagId)
}