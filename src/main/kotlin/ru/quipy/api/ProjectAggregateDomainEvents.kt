package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.logic.TaskStatusEntity
import ru.quipy.logic.ParticipantEntity
import java.util.*
import kotlin.collections.mutableMapOf

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val TASK_STATUS_CREATED_EVENT = "TASK_STATUS_CREATED_EVENT"
const val TASK_STATUS_ASSIGNED_TO_TASK_EVENT = "TASK_STATUS_ASSIGNED_TO_TASK_EVENT"
const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val PARTICIPANT_ADDED_EVENT = "PARTICIPANT_ADDED_EVENT"
const val PERFORMER_ADDED_TO_TASK_EVENT = "PERFORMER_ADDED_TO_TASK_EVENT"

// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val projectName: String,
    val description: String,
    val authorUsername: String,
    val authorFullName: String,
    val taskStatuses: MutableMap<UUID, TaskStatusEntity>,
    val participants: MutableMap<UUID, ParticipantEntity>,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_STATUS_CREATED_EVENT)
class TaskStatusCreatedEvent(
    val projectId: UUID,
    val taskStatusId: UUID,
    val taskStatusName: String,
    val taskStatusColour: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_STATUS_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TASK_STATUS_ASSIGNED_TO_TASK_EVENT)
class TaskStatusAssignedToTaskEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskStatusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_STATUS_ASSIGNED_TO_TASK_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = PARTICIPANT_ADDED_EVENT)
class ParticipantAddedEvent(
    val projectId: UUID,
    val participantId: UUID,
    val participantUsername: String,
    val participantFullName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PARTICIPANT_ADDED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = PERFORMER_ADDED_TO_TASK_EVENT)
class PerformerAddedToTaskEvent(
    val projectId: UUID,
    val taskId: UUID,
    val participantId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PERFORMER_ADDED_TO_TASK_EVENT,
    createdAt = createdAt
)