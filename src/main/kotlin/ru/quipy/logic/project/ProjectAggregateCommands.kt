package ru.quipy.logic.project

import ru.quipy.api.project.ParticipantAddedEvent
import ru.quipy.api.project.ProjectCreatedEvent
import ru.quipy.api.task.TaskCreatedEvent
import java.util.*

fun ProjectAggregateState.createProject(id: UUID, title: String, creatorId: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
            projectId = id,
            title = title,
            creatorId = creatorId,
    )
}

fun ProjectAggregateState.addParticipant(
        projectId: UUID,
        userId: UUID,
        userName: String
): ParticipantAddedEvent {
    return ParticipantAddedEvent(
            projectId = projectId,
            userId = userId,
            userName = userName
    )
}
