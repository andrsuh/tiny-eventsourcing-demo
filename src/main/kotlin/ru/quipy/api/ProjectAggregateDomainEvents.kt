package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val TAG_CREATED_EVENT = "TAG_CREATED_EVENT"
const val TAG_ASSIGNED_TO_TASK_EVENT = "TAG_ASSIGNED_TO_TASK_EVENT"
const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val USER_ADDED_TO_PROJECT_EVENT = "USER_ADDED_TO_PROJECT_EVENT"
const val PROJECT_NAME_CHANGED_EVENT = "PROJECT_NAME_CHANGED_EVENT"
const val TAG_CHANGED_EVENT = "TAG_CHANGED_EVENT"
const val USER_ASSIGNED_TO_TASK_EVENT = "USER_ASSIGNED_TO_TASK_EVENT"
const val TASK_NAME_CHANGED_EVENT = "TASK_NAME_CHANGED_EVENT"

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
        val color: String,
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

@DomainEvent(name = USER_ADDED_TO_PROJECT_EVENT)
class UserAddedToProjectEvent(
        val projectId: UUID,
        val userId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = USER_ADDED_TO_PROJECT_EVENT,
        createdAt = createdAt
)

@DomainEvent(name = PROJECT_NAME_CHANGED_EVENT)
class ProjectNameChangedEvent(
        val projectId: UUID,
        val title: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = PROJECT_NAME_CHANGED_EVENT,
        createdAt = createdAt
)

@DomainEvent(name = TAG_CHANGED_EVENT)
class TagDeletedEvent(
        val projectId: UUID,
        val tagId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = TAG_CHANGED_EVENT,
        createdAt = createdAt
)


@DomainEvent(name = USER_ASSIGNED_TO_TASK_EVENT)
class UserAssignedToTaskEvent(
        val projectId: UUID,
        val userId: UUID,
        val taskId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = USER_ASSIGNED_TO_TASK_EVENT,
        createdAt = createdAt
)

@DomainEvent(name = TASK_NAME_CHANGED_EVENT)
class TaskNameChangedEvent(
        val projectId: UUID,
        val taskId: UUID,
        val title: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = TASK_NAME_CHANGED_EVENT,
        createdAt = createdAt
)

