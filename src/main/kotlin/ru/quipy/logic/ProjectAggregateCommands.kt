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

fun ProjectAggregateState.addTask(userId: UUID,name: String): TaskCreatedEvent {
    if (!membersList.containsKey(userId)) {
        throw IllegalArgumentException("This User doesn't exists in project: $userId")
    }
    return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name)
}

fun ProjectAggregateState.createTag(userId: UUID, name: String, color: String): TagCreatedEvent {
    if (!membersList.containsKey(userId)) {
        throw IllegalArgumentException("This User doesn't exists in project: $userId")
    }
    if (projectTags.values.any { it.name == name }) {
        throw IllegalArgumentException("Tag already exists: $name")
    }

    val regex = Regex("^[0-9a-fA-F]{6}\$")
    if (regex.matches(color)) {
        throw IllegalArgumentException("color is not hex formatted: $color")
    }

    return TagCreatedEvent(projectId = this.getId(), tagId = UUID.randomUUID(), tagName = name, color = color)
}

fun ProjectAggregateState.assignTagToTask(userId: UUID,tagId: UUID, taskId: UUID): TagAssignedToTaskEvent {
    if (!membersList.containsKey(userId)) {
        throw IllegalArgumentException("This User doesn't exists in project: $userId")
    }
    if (!projectTags.containsKey(tagId)) {
        throw IllegalArgumentException("Tag doesn't exists: $tagId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    if (tasks[taskId]?.tag == tagId) {
        throw IllegalArgumentException("This tag is already assigned: $tagId")
    }

    return TagAssignedToTaskEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
}

fun ProjectAggregateState.addUserToProject(userId: UUID, userNewId: UUID): UserAddedToProjectEvent {
    if (!membersList.containsKey(userId)) {
        throw IllegalArgumentException("This User doesn't exists in project: $userId")
    }
    if (membersList.containsKey(userNewId)) {
        throw IllegalArgumentException("This User exists in project: $userNewId")
    }
    return UserAddedToProjectEvent(projectId = this.getId(), userId = userNewId)
}

fun ProjectAggregateState.changeProjectName(userId: UUID, title: String): ProjectNameChangedEvent {
    if (!membersList.containsKey(userId)) {
        throw IllegalArgumentException("This User doesn't exists in project: $userId")
    }
    if (projectTitle == title) {
        throw IllegalArgumentException("This title is already applied: $title")
    }
    return ProjectNameChangedEvent(projectId = this.getId(), title = title)
}

fun ProjectAggregateState.deleteTag(userId: UUID,tagId: UUID): TagDeletedEvent {
    if (!membersList.containsKey(userId)) {
        throw IllegalArgumentException("This User doesn't exists in project: $userId")
    }
    if (!projectTags.containsKey(tagId)) {
        throw IllegalArgumentException("Tag doesn't exists: $tagId")
    }

    if (!tasks.values.none { userEntity ->
                tagId == userEntity.tag
            }) {
        throw IllegalArgumentException("Tag is associated with at least 1 task it cannot be deleted: $tagId")
    }
    return TagDeletedEvent(projectId = this.getId(), tagId = tagId)
}

fun ProjectAggregateState.assignUserToTask(userId: UUID, taskId: UUID): UserAssignedToTaskEvent {
    if (!membersList.containsKey(userId)) {
        throw IllegalArgumentException("User doesn't exists in project: $userId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    if (tasks[taskId]?.userId == userId) {
        throw IllegalArgumentException("This user is already assigned: $userId")
    }

    return UserAssignedToTaskEvent(projectId = this.getId(), userId = userId, taskId = taskId)
}


fun ProjectAggregateState.changeTaskName(userId: UUID,taskId: UUID, title: String): TaskNameChangedEvent {
    if (!membersList.containsKey(userId)) {
        throw IllegalArgumentException("User doesn't exists in project: $userId")
    }
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }
    if (tasks[taskId]?.name == title) {
        throw IllegalArgumentException("This title is already applied: $title")
    }
    return TaskNameChangedEvent(projectId = this.getId(), taskId = taskId, title = title)
}