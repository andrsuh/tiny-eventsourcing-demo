package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PARTICIPANT_CREATED_EVENT = "PARTICIPANT_CREATED_EVENT"
const val PARTICIPANT_ADDED_TO_PROJECT_EVENT = "PARTICIPANT_ADDED_TO_PROJECT_EVENT"

@DomainEvent(name = PARTICIPANT_CREATED_EVENT)
class ParticipantCreatedEvent(
    val participantId: UUID,
    val projectId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ParticipantAggregate>(
    name = PARTICIPANT_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = PARTICIPANT_ADDED_TO_PROJECT_EVENT)
class ParticipantAddedToProjectEvent(
    val projectId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ParticipantAggregate>(
    name = PARTICIPANT_ADDED_TO_PROJECT_EVENT,
    createdAt = createdAt,
)