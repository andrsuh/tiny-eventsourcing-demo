package ru.quipy.logic.project

import ru.quipy.api.project.*
import ru.quipy.logic.user.updateUserProfile
import java.util.*
import ru.quipy.projections.*


fun ProjectAggregateState.createProject(id: UUID, title: String, creatorId: UUID): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        creatorId = creatorId,
    )
}

fun ProjectAggregateState.addParticipantToProject(userId: UUID, userProjectionRepository: UserProjectionRepo): AddParticipantToProjectEvent {
    if (participants.contains(userId)) {
        throw IllegalArgumentException("User already exists: $userId")
    }

    if (!userProjectionRepository.existsById(userId)) {
        throw IllegalArgumentException("User is not exists: $userId")
    }

    return AddParticipantToProjectEvent(
        projectId = this.getId(),
        participantId = userId
    )
}

fun ProjectAggregateState.changeProjectTitle(id: UUID, newTitle: String): ProjectTitleChangedEvent {
    if (this.projectTitle == newTitle ) {
        throw IllegalArgumentException("New project title is the same as old title")
    }
    return ProjectTitleChangedEvent(
        projectId = id,
        title = newTitle,
    )
}

fun ProjectAggregateState.createStatus(name: String, color: String): StatusCreatedEvent {
    if (statuses.values.any { it.statusName == name }) {
        throw IllegalArgumentException("Status already exists: $name")
    }

    return StatusCreatedEvent(
        projectId = this.getId(),
        statusId = UUID.randomUUID(),
        statusName = name,
        color = color,
        taskQuantity = 0
    )
}

fun ProjectAggregateState.changeStatusName(statusId: UUID, newStatusName: String): StatusChangedEvent{
    if (statuses[statusId]?.statusName == newStatusName) {
        throw IllegalArgumentException("New status name is the same as old status name")
    }

    if (statuses.values.any { it.statusName == newStatusName }) {
        throw IllegalArgumentException("Status name already exists: $newStatusName")
    }

    return StatusChangedEvent(
        projectId = this.getId(),
        statusId = statusId,
        newStatusName = newStatusName
    )
}

fun ProjectAggregateState.addTasktoProject(taskId: UUID): TaskAddedInProjectEvent {
    return TaskAddedInProjectEvent(
        projectId = this.getId(),
        taskId = taskId,
    )
}

