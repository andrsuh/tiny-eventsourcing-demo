package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val PROJECT_UPDATED_EVENT = "PROJECT_UPDATED_EVENT"
const val PROJECT_USER_ADDED_EVENT = "PROJECT_USER_ADDED_EVENT"
const val PROJECT_USER_REMOVED_EVENT = "PROJECT_USER_REMOVED_EVENT"

const val TAG_CREATED_EVENT = "TAG_CREATED_EVENT"
const val TAG_ASSIGNED_TO_TASK_EVENT = "TAG_ASSIGNED_TO_TASK_EVENT"

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_EXECUTOR_ADDED_EVENT = "TASK_EXECUTOR_ADDED_EVENT"
const val TASK_UPDATED_EVENT = "TASK_UPDATED_EVENT"
const val TASK_DELETED_EVENT = "TASK_DELETED_EVENT"

// API
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

@DomainEvent(name = PROJECT_UPDATED_EVENT)
class ProjectUpdatedEvent(
    val projectId: UUID,
    val title: String?,
    val description: String?,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate> (
    name = PROJECT_UPDATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = PROJECT_USER_ADDED_EVENT)
class ProjectUserAddedEvent(
    val projectId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_USER_ADDED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = PROJECT_USER_REMOVED_EVENT)
class ProjectUserRemovedEvent(
    val projectId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_USER_REMOVED_EVENT,
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


@DomainEvent(name = TASK_UPDATED_EVENT)
class TaskUpdatedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskName: String,
    val taskDescription: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_UPDATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_EXECUTOR_ADDED_EVENT)
class TaskExecutorAddedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_EXECUTOR_ADDED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_DELETED_EVENT)
class TaskDeletedEvent(
    val projectId: UUID,
    val taskId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_DELETED_EVENT,
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