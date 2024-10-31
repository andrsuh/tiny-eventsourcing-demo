package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TAG_STATUS_CHANGED_EVENT = "TASK_STATUS_CHANGED_EVENT"
const val USER_ADDED_TO_TASK_EVENT = "USER_ADDED_TO_TASK_EVENT"
const val TASK_NAME_CHANGED_EVENT = "TASK_NAME_CHANGED_EVENT"

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val projectId: UUID,
    val creatorId: UUID,
    val description: String,
    val taskId: UUID,
    val taskName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TAG_STATUS_CHANGED_EVENT)
class TaskStatusChangedEvent(
    val taskId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = TAG_STATUS_CHANGED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = USER_ADDED_TO_TASK_EVENT)
class UserAddedToTaskEvent(
    val taskId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<TaskAggregate>(
    name = USER_ADDED_TO_TASK_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TASK_NAME_CHANGED_EVENT)
class TaskNameChangedEvent(
    val taskId: UUID,
    val taskName: String,
    createdAt: Long = System.currentTimeMillis()
) : Event<TaskAggregate>(
    name = TASK_NAME_CHANGED_EVENT,
    createdAt = createdAt
)