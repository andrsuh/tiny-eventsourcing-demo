package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*


const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val STATUS_CHANGED_EVENT = "STATUS_CHANGED_EVENT"
const val TASK_EXECUTOR_ADDED_EVENT = "TASK_EXECUTOR_ADDED_EVENT"


@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val taskId: UUID,
    val projectId: UUID,
    val title: String,
    val executors : MutableList<UUID>,
    val creatorId : UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
): Event<TaskAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt
)


@DomainEvent(name = STATUS_CHANGED_EVENT)
class StatusChangedEvent(
    val taskId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
): Event<TaskAggregate>(
    name = STATUS_CHANGED_EVENT,
    createdAt = createdAt
)


@DomainEvent(name = TASK_EXECUTOR_ADDED_EVENT)
class TaskExecutorAddedEvent(
    val taskId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis(),
): Event<TaskAggregate>(
    name = TASK_EXECUTOR_ADDED_EVENT,
    createdAt = createdAt
)