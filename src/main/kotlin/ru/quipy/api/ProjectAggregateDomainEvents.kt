package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.awt.Color
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val USER_ADDED_IN_PROJECT_EVENT = "USER_ADDED_IN_PROJECT_EVENT"
const val STATUS_ADDED_EVENT = "STATUS_ADDED_EVENT"
const val STATUS_REMOVED_EVENT = "STATUS_REMOVED_EVENT"
const val TASK_STATUS_CHANGED_EVENT = "TASK_STATUS_CHANGED_EVENT"
const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_CHANGED_EVENT = "TASK_CHANGED_EVENT"
const val ASSIGNEE_ADDED_EVENT = "ASSIGNEE_ADDED_EVENT"

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

@DomainEvent(name = USER_ADDED_IN_PROJECT_EVENT)
class UserAddedInProjectEvent(
    val userId: UUID,
    val projectId: UUID,
    val adderId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = USER_ADDED_IN_PROJECT_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = STATUS_ADDED_EVENT)
class StatusAddedEvent(
        val statusName: String,
        val color: Color,
        val statusId: UUID,
        val projectId: UUID,
        val adderId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = STATUS_ADDED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = STATUS_REMOVED_EVENT)
class StatusRemovedEvent(
        val statusId: UUID,
        val removerId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = STATUS_REMOVED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = TASK_STATUS_CHANGED_EVENT)
class TaskStatusChangedEvent(
        val taskId: UUID,
        val newStatusId: UUID,
        val changerId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = TASK_STATUS_CHANGED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
        val taskId: UUID,
        val taskName: String,
        val projectId: UUID,
        val creatorId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = TASK_CREATED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = TASK_CHANGED_EVENT)
class TaskChangedEvent(
        val taskId: UUID,
        val newName: String,
        val changerId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = TASK_CHANGED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = ASSIGNEE_ADDED_EVENT)
class AssigneeAddedEvent(
        val taskId: UUID,
        val assigneeId: UUID,
        val adderId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = ASSIGNEE_ADDED_EVENT,
        createdAt = createdAt,
)
