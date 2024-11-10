package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val PROJECT_PARTICIPANT_ADDED_EVENT = "PROJECT_PARTICIPANT_ADDED_EVENT"
const val PROJECT_NAME_EDITED_EVENT = "PROJECT_NAME_EDITED_EVENT"
const val TASK_STATUS_CREATED_EVENT = "TASK_STATUS_CREATED_EVENT"
const val TASK_STATUS_DELETED_EVENT = "TASK_STATUS_DELETED_EVENT"
const val TASK_STATUS_SET_EVENT = "TASK_STATUS_SET_EVENT"
const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_NAME_EDITED_EVENT = "TASK_NAME_EDITED_EVENT"
const val TASK_PERFORMER_SET_EVENT = "TASK_PERFORMER_SET_EVENT"
const val TASK_PERFORMER_DELETED_EVENT = "TASK_PERFORMER_DELETED_EVENT"
const val TASK_DELETED_EVENT = "TASK_DELETED_EVENT"


@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val title: String,
    val creatorId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = PROJECT_PARTICIPANT_ADDED_EVENT)
class ProjectParticipantAddedEvent(
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_PARTICIPANT_ADDED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = PROJECT_NAME_EDITED_EVENT)
class ProjectNameEditedEvent(
    val newProjectName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_NAME_EDITED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_STATUS_CREATED_EVENT)
class TaskStatusCreatedEvent(
    val statusColor: String,
    val statusName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_STATUS_CREATED_EVENT,
    createdAt = createdAt,
)


@DomainEvent(name = TASK_STATUS_SET_EVENT)
class TaskStatusSetEvent(
    val taskId: UUID,
    val statusName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_STATUS_SET_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_STATUS_DELETED_EVENT)
class TaskStatusDeletedEvent(
    val statusName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_STATUS_DELETED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val taskId: UUID,
    val taskName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_NAME_EDITED_EVENT)
class TaskNameEditedEvent(
    val taskId: UUID,
    val newTaskName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_NAME_EDITED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_PERFORMER_SET_EVENT)
class TaskPerfomerSetEvent(
    val taskId: UUID,
    val performer: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_PERFORMER_SET_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_PERFORMER_DELETED_EVENT)
class TaskPerfomerDeletedEvent(
    val taskId: UUID,
    val performer: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_PERFORMER_DELETED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_DELETED_EVENT)
class TaskDeletedEvent(
    val taskId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_DELETED_EVENT,
    createdAt = createdAt,
)