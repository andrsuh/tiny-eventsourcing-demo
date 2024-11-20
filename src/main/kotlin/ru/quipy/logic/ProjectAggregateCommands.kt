package ru.quipy.logic

import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.TaskStatusAssignedToTaskEvent
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.ParticipantAddedEvent
import ru.quipy.api.PerformerAddedToTaskEvent
import ru.quipy.api.TaskCreatedEvent
import java.util.*


fun ProjectAggregateState.create(id: UUID, projectName: String, authorUsername: String, authorFullName: String, description: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        projectName = projectName,
        authorUsername = authorUsername,
        authorFullName = authorFullName,
        taskStatuses = withDefaultTaskStatus(),
        participants = withAuthorParticipant(authorUsername, authorFullName),
        description = description,
    )
}

fun ProjectAggregateState.addTask(name: String): TaskCreatedEvent {
    return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name)
}

fun ProjectAggregateState.createTaskStatus(name: String, colour: String): TaskStatusCreatedEvent {
    if (taskStatuses.values.any { it.name == name }) {
        throw IllegalArgumentException("Task status already exists: $name")
    }
    return TaskStatusCreatedEvent(
        projectId = this.getId(),
        taskStatusId = UUID.randomUUID(),
        taskStatusName = name,
        taskStatusColour = colour)
}

fun ProjectAggregateState.addParticipant(participantUsername: String, participantFullName: String): ParticipantAddedEvent {
    if (participants.values.any { it.username == participantUsername }) {
        throw IllegalArgumentException("Participant already added: $participantUsername")
    }
    return ParticipantAddedEvent(
        projectId = this.getId(),
        participantId = UUID.randomUUID(),
        participantUsername = participantUsername,
        participantFullName = participantFullName)
}

fun ProjectAggregateState.addPerformerToTask(taskId: UUID, participantId: UUID): PerformerAddedToTaskEvent {
    if (!participants.values.any { it.id == participantId }) {
        throw IllegalArgumentException("No such participant: ${participantId}")
    }
    return PerformerAddedToTaskEvent(
        projectId = this.getId(),
        taskId = taskId,
        participantId = participantId)
}

fun ProjectAggregateState.assignTaskStatusToTask(taskStatusId: UUID, taskId: UUID): TaskStatusAssignedToTaskEvent {
    if (!taskStatuses.containsKey(taskStatusId)) {
        throw IllegalArgumentException("Task status doesn't exists: $taskStatusId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return TaskStatusAssignedToTaskEvent(projectId = this.getId(), taskStatusId = taskStatusId, taskId = taskId)
}