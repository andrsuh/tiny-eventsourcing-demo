package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.enum.ColorEnum
import java.util.UUID

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_UPDATED_EVENT = "TASK_UPDATED_EVENT"
const val EXECUTOR_ADDED_EVENT = "EXECUTOR_ADDED_EVENT"
const val STATUS_CREATED_EVENT = "STATUS_CREATED_EVENT"
const val STATUS_DELETED_EVENT = "STATUS_DELETED_EVENT"
const val TASK_STATUS_CHANGED_EVENT = "TASK_STATUS_CHANGED_EVENT"
const val TASK_STATUS_POSITION_CHANGED_EVENT = "TASK_STATUS_POSITION_CHANGED_EVENT"
// API

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
        val taskId: UUID,
        val projectId: UUID,
        val taskName: String,
        val description: String,
        val statusId: UUID,
        val executors: MutableList<UUID> = mutableListOf(),
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

@DomainEvent(name = TASK_STATUS_CHANGED_EVENT)
class TaskStatusChangedEvent(
        val taskId: UUID,
        val statusId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAndStatusAggregate>(
        name = TASK_STATUS_CHANGED_EVENT,
        createdAt = createdAt
)

@DomainEvent(name = STATUS_CREATED_EVENT)
class StatusCreatedEvent(
        val statusId: UUID,
        val statusName: String,
        val projectId: UUID,
        val color: ColorEnum,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAndStatusAggregate>(
        name = STATUS_CREATED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = STATUS_DELETED_EVENT)
class StatusDeletedEvent(
        val statusId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAndStatusAggregate>(
        name = STATUS_DELETED_EVENT,
        createdAt = createdAt
)

@DomainEvent(name = TASK_STATUS_POSITION_CHANGED_EVENT)
class StatusPositionChangedEvent(
        val statusId: UUID,
        val position: Int,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAndStatusAggregate>(
        name = TASK_STATUS_POSITION_CHANGED_EVENT,
        createdAt = createdAt,
)