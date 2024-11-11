package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val PROJECT_USER_ADDED_EVENT = "PROJECT_USER_ADDED_EVENT"
const val PROJECT_USER_REMOVED_EVENT = "PROJECT_USER_REMOVED_EVENT"
const val PROJECT_TASK_CREATED_EVENT = "PROJECT_TASK_CREATED_EVENT"
const val PROJECT_TASK_MODIFIED_EVENT = "PROJECT_TASK_MODIFIED_EVENT"
const val PROJECT_STATUS_CREATED_EVENT = "PROJECT_STATUS_CREATED_EVENT"
const val PROJECT_STATUS_DEFAULT_MODIFIED_EVENT = "PROJECT_STATUS_DEFAULT_MODIFIED_EVENT"
const val PROJECT_STATUS_REMOVED_EVENT = "PROJECT_STATUS_REMOVED_EVENT"

//// API

@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val projectName: String,
    val ownerId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = PROJECT_USER_ADDED_EVENT)
class ProjectUserAddedEvent(
    val projectId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_USER_ADDED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = PROJECT_USER_REMOVED_EVENT)
class ProjectUserRemovedEvent(
    val projectId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_USER_ADDED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = PROJECT_TASK_CREATED_EVENT)
class ProjectTaskCreatedEvent(
    val projectId: UUID,
    val userId: UUID,
    val taskName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_TASK_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = PROJECT_TASK_MODIFIED_EVENT)
class ProjectTaskModifiedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val statusId: UUID?,
    val executors: MutableSet<UUID>?,
    val taskName: String?,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_TASK_MODIFIED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = PROJECT_STATUS_CREATED_EVENT)
class ProjectStatusCreatedEvent(
    val projectId: UUID,
    val statusName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_STATUS_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = PROJECT_STATUS_DEFAULT_MODIFIED_EVENT)
class ProjectStatusDefaultModifiedEvent(
    val projectId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_STATUS_DEFAULT_MODIFIED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = PROJECT_STATUS_REMOVED_EVENT)
class ProjectStatusRemovedEvent(
    val projectId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_STATUS_REMOVED_EVENT,
    createdAt = createdAt
)
