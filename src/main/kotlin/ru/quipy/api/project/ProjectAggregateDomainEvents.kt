package ru.quipy.api.project

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val ADD_MEMBER_EVENT = "ADD_MEMBER_EVENT"
const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_DELETED_EVENT = "TASK_DELETED_EVENT"
const val STATUS_CREATED_EVENT = "STATUS_CREATED_EVENT"
const val STATUS_DELETED_EVENT = "STATUS_DELETED_EVENT"

// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val title: String,
    val creator: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = ADD_MEMBER_EVENT)
class MemberAddedEvent(
    val projectId: UUID,
    val newMember: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = ADD_MEMBER_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val projectId: UUID,
    val taskId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TASK_DELETED_EVENT)
class TaskDeletedEvent(
    val projectId: UUID,
    val taskId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_DELETED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = STATUS_CREATED_EVENT)
class StatusAddedEvent(
    val projectId: UUID,
    val addedStatus: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = STATUS_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = STATUS_DELETED_EVENT)
class StatusDeletedEvent(
    val projectId: UUID,
    val deletedStatus: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = STATUS_DELETED_EVENT,
    createdAt = createdAt
)