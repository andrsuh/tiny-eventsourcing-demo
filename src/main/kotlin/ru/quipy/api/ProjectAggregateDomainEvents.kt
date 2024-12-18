package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val TAG_CREATED_EVENT = "TAG_CREATED_EVENT"
const val TAG_ASSIGNED_TO_TASK_EVENT = "TAG_ASSIGNED_TO_TASK_EVENT"
const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val MEMBER_ADDED_EVENT = "MEMBER_ADDED_EVENT"
const val EXECUTOR_ASSIGNED_TO_TASK_EVENT = "EXECUTOR_ASSIGNED_TO_TASK_EVENT"
const val TASK_STATUS_CHANGED_EVENT = "TASK_STATUS_CHANGED_EVENT"

// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val title: String,
    val creatorId: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TAG_CREATED_EVENT)
class TagCreatedEvent(
    val projectId: UUID,
    val tagId: UUID,
    val tagName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TAG_CREATED_EVENT,
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

@DomainEvent(name = TAG_ASSIGNED_TO_TASK_EVENT)
class TagAssignedToTaskEvent(
    val projectId: UUID,
    val taskId: UUID,
    val tagId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TAG_ASSIGNED_TO_TASK_EVENT,
    createdAt = createdAt
)
@DomainEvent(name = MEMBER_ADDED_EVENT)
class MemberAddedEvent(
        val projectId: UUID,
        val userId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = MEMBER_ADDED_EVENT,
        createdAt = createdAt
)

@DomainEvent(name = EXECUTOR_ASSIGNED_TO_TASK_EVENT)
class ExecutorAssignedToTaskEvent(
        val projectId: UUID,
        val taskId: UUID,
        val executorId: UUID,
        createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
        name = EXECUTOR_ASSIGNED_TO_TASK_EVENT,
        createdAt = createdAt
)

@DomainEvent(name = TASK_STATUS_CHANGED_EVENT)
class TaskStatusChangedEvent(
        val projectId: UUID,
        val taskId: UUID,
        val newStatus: Int,
        createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
        name = TASK_STATUS_CHANGED_EVENT,
        createdAt = createdAt
)