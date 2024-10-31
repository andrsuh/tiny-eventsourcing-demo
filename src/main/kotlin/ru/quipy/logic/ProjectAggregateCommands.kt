package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


fun ProjectAggregateState.create(id: UUID, title: String, creatorId: UUID): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        creatorId = creatorId,
    )
}

fun ProjectAggregateState.addParticipants(participantId: UUID): ParticipantAddedEvent {
    return ParticipantAddedEvent(
        projectId = this.getId(),
        participantId = participantId
    )
}

fun ProjectAggregateState.editProjectTitle(title: String): EditProjectTitleEvent {
    return EditProjectTitleEvent(
        projectId = this.getId(),
        title = title
    )
}

fun ProjectAggregateState.createStatus(
    statusId: UUID,
    statusName: String,
    color: String
): StatusCreatedEvent {
    return StatusCreatedEvent(
        projectId = this.getId(),
        statusId = statusId,
        statusName = statusName,
        color = color
    )
}

fun ProjectAggregateState.deleteStatus(statusId: UUID): StatusDeletedEvent {
    return StatusDeletedEvent(
        projectId = this.getId(),
        statusId = statusId
    )
}