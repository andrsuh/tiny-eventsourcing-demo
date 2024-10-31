package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


fun ProjectAggregateState.createProject(id: UUID, title: String, creatorId: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        creatorId = creatorId,
    )
}

fun ProjectAggregateState.createStatus(name: String, color: String, userInitiatorId: UUID): StatusCreatedEvent {
    if (projectStatuses.values.any { it.name == name }) {
        throw IllegalArgumentException("Tag already exists: $name")
    }
    if (!users.contains(userInitiatorId)) {
        error("You have no rights to create statuses in this project")
    }
    return StatusCreatedEvent(
        projectId = this.getId(),
        statusId = UUID.randomUUID(),
        statusColor = color,
        statusName = name
    )
}
fun ProjectAggregateState.changeProjectName(name: String, userInitiatorId: UUID): ProjectNameChangedEvent {
    if (this.name == name) {
        error("This name already set")
    }
    if (!users.contains(userInitiatorId)){
        error("You have no rights to change project name")
    }
    return ProjectNameChangedEvent(projectId = this.getId(), projectName = name)
}
fun ProjectAggregateState.addUserToProject(userInitiatorId: UUID, userId: UUID): UserAddedToProjectEvent {
    if (!this.users.contains(userInitiatorId)) {
        error("You have no rights to add users to this project")
    }
    if (this.users.contains(userId)) {
        error("This user is already added to project")
    }
    return UserAddedToProjectEvent(projectId = this.getId(), userId = userId)
}

fun ProjectAggregateState.addTaskToProject(taskId: UUID): TaskAddedEvent {
    if (this.tasks.contains(taskId)) {
        error("Task was already added to project")
    }
    return TaskAddedEvent(projectId = this.getId(), taskId = taskId)
}

fun ProjectAggregateState.deleteStatus(statusId: UUID, userInitiatorId: UUID): StatusDeletedEvent {
    if (this.projectStatuses.containsKey(statusId)) {
        error("Project doesn't contains this status")
    }
    if (users.contains(userInitiatorId)){
        error("You have no rights to delete status from this project")
    }
    return StatusDeletedEvent(projectId = this.getId(), statusId = statusId)
}