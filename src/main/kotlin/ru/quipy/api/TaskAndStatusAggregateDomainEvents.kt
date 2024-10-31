package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_UPDATED_EVENT = "TASK_UPDATED_EVENT"
const val EXECUTOR_ADDED_EVENT = "EXECUTOR_ADDED_EVENT"
const val STATUS_ASSIGNED_TO_TASK_EVENT = "STATUS_ASSIGNED_TO_TASK_EVENT"
const val STATUS_REMOVED_FROM_TASK_EVENT = "STATUS_REMOVED_FROM_TASK_EVENT"
const val TASK_STATUS_CHANGED_EVENT = "TASK_STATUS_CHANGED_EVENT"
// API

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
        val taskId: UUID,
        val projectId: UUID,
        val taskName: String,
        val description: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAndStatusAggregate>(
        name = TASK_CREATED_EVENT,
        createdAt = createdAt
)

@DomainEvent(name = TASK_UPDATED_EVENT)
class TaskUpdatedEvent(
        val taskId: UUID,
        val taskName: String,
        val description: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAndStatusAggregate>(
        name = TASK_UPDATED_EVENT,
        createdAt = createdAt
)
@DomainEvent(name = EXECUTOR_ADDED_EVENT)
class ExecutorAddedEvent(
        val taskId: UUID,
        val userId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAndStatusAggregate>(
        name = EXECUTOR_ADDED_EVENT,
        createdAt = createdAt
)
@DomainEvent(name = STATUS_ASSIGNED_TO_TASK_EVENT)
class StatusAssignedToTaskEvent(
        val taskId: UUID,
        val statusId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAndStatusAggregate>(
        name = STATUS_ASSIGNED_TO_TASK_EVENT,
        createdAt = createdAt
)

@DomainEvent(name = TASK_STATUS_CHANGED_EVENT)
class TaskStatusChangedEvent(
        val taskId: UUID,
        val statusId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAndStatusAggregate>(
        name = TASK_STATUS_CHANGED_EVENT,
        createdAt = createdAt
)
@DomainEvent(name = STATUS_REMOVED_FROM_TASK_EVENT)
class StatusRemovedFromTaskEvent(
        val taskId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAndStatusAggregate>(
        name = STATUS_REMOVED_FROM_TASK_EVENT,
        createdAt = createdAt
)