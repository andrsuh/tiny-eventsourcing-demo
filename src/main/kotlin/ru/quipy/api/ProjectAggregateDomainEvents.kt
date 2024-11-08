package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_CREATED = "PROJECT_CREATED_EVENT"
const val PROJECT_MEMBER_ADDED = "PROJECT_MEMBER_ADDED_EVENT"
const val PROJECT_TITLE_CHANGED = "PROJECT_TITLE_CHANGED_EVENT"
const val STATUS_CREATED = "STATUS_CREATED_EVENT"
const val STATUS_CHANGED = "STATUS_CHANGED_EVENT"
const val TASK_ADDED_IN_PROJECT = "TASK_ADDED_IN_PROJECT_EVENT"
const val TASK_CREATED = "TASK_CREATED_EVENT"
const val STATUS_DELETED ="STATUS_DELETED_EVENT"
const val TASK_TITLE_CHANGED = "TASK_TITLE_CHANGED_EVENT"
const val TASK_STATUS_CHANGED = "TASK_STATUS_CHANGED_EVENT"
const val TASK_EXECUTOR_ASSIGNED = "TASK_EXECUTOR_ASSIGNED_EVENT"

@DomainEvent(name = PROJECT_CREATED)
class ProjectCreatedEvent(
    val projectId: UUID,
    val title: String,
    val creatorId: UUID
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED
)

@DomainEvent(name = PROJECT_TITLE_CHANGED)
class ProjectTitleChangedEvent(
    val projectId: UUID,
    val newTitle: String
) : Event<ProjectAggregate>(
    name = PROJECT_TITLE_CHANGED
)

@DomainEvent(name = PROJECT_MEMBER_ADDED)
class AddParticipantToProjectEvent(
    val projectId: UUID,
    val participantId: UUID
) : Event<ProjectAggregate>(
    name = PROJECT_MEMBER_ADDED
)

@DomainEvent(name = STATUS_CREATED)
class StatusCreatedEvent(
    val projectId: UUID,
    val statusId: UUID,
    val title: String,
    val color: String
) : Event<ProjectAggregate>(
    name = STATUS_CREATED
)

@DomainEvent(name = STATUS_DELETED)
class StatusDeletedEvent(
    val projectId: UUID,
    val statusId: UUID
) : Event<ProjectAggregate>(
    name = STATUS_DELETED
)

@DomainEvent(name = STATUS_CHANGED)
class StatusTitleChangedEvent(
    val projectId: UUID,
    val statusId: UUID,
    val newTitle: String
) : Event<ProjectAggregate>(
    name = STATUS_CHANGED
)

@DomainEvent(name = TASK_CREATED)
class TaskCreatedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val title: String
) : Event<ProjectAggregate>(
    name = TASK_CREATED
)

@DomainEvent(name = TASK_TITLE_CHANGED)
class TaskNameChangeEvent(
    val projectId: UUID,
    val taskId: UUID,
    val newTitle: String
) : Event<ProjectAggregate>(
    name = TASK_TITLE_CHANGED
)

@DomainEvent(name = TASK_STATUS_CHANGED)
class TaskStatusChangedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val newStatusId: UUID
) : Event<ProjectAggregate>(
    name = TASK_STATUS_CHANGED
)

@DomainEvent(name = TASK_EXECUTOR_ASSIGNED)
class TaskExecutorAssignedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val userId: UUID
) : Event<ProjectAggregate>(
    name = TASK_EXECUTOR_ASSIGNED
)