package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*


const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val PROJECT_UPDATED_EVENT = "PROJECT_UPDATED_EVENT"
const val PARTICIPANT_ADDED_EVENT = "PARTICIPANT_ADDED_EVENT"
const val LEAVE_PROJECT_EVENT = "LEAVE_PROJECT_EVENT"
const val STATUS_CREATED_EVENT = "STATUS_CREATED_EVENT"
const val STATUS_DELETED_EVENT = "STATUS_DELETED_EVENT"


// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
        val projectId: UUID,
        val projectName: String,
        val creatorId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = PROJECT_CREATED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = PROJECT_UPDATED_EVENT)
class ProjectUpdatedEvent(
        val projectId: UUID,
        val projectName: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = PROJECT_UPDATED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = PARTICIPANT_ADDED_EVENT)
class ParticipantAddedEvent(
        val projectId: UUID,
        val userId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = PARTICIPANT_ADDED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = LEAVE_PROJECT_EVENT)
class LeaveProjectEvent(
        val projectId: UUID,
        val userId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = LEAVE_PROJECT_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = STATUS_CREATED_EVENT)
class StatusCreatedEvent(
        val projectId: UUID,
        val statusId: UUID,
        val statusName: String,
        val color: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = STATUS_CREATED_EVENT,
        createdAt = createdAt,
)


//color = if (Color.getColor(event.color) != null) Color.getColor(event.color) else throw IllegalArgumentException("Incorrect color type.")

@DomainEvent(name = STATUS_DELETED_EVENT)
class StatusDeletedEvent(
        val projectId: UUID,
        val statusId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = STATUS_DELETED_EVENT,
        createdAt = createdAt
)