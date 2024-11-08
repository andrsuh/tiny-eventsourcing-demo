package ru.quipy.api.task

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val TASK_RENAMED_EVENT = "TASK_RENAMED_EVENT"
const val USER_ASSIGNED_TO_TASK_EVENT = "USER_ASSIGNED_TO_TASK_EVENT"
const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val STATUS_ASSIGNED_TO_TASK_EVENT = "STATUS_ASSIGNED_TO_TASK_EVENT"

@DomainEvent(name = TASK_RENAMED_EVENT)
class TaskRenamedEvent(
        val taskId: UUID,
        val title: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
        name = TASK_RENAMED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = USER_ASSIGNED_TO_TASK_EVENT)
class UserAssignedToTaskEvent(
        val taskId: UUID,
        val userId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
        name = USER_ASSIGNED_TO_TASK_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
        val projectId: UUID,
        val taskId: UUID,
        val statusId: UUID,
        val taskTitle: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = STATUS_ASSIGNED_TO_TASK_EVENT)
class StatusAssignedToTaskEvent(
        val taskId: UUID,
        val statusId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = STATUS_ASSIGNED_TO_TASK_EVENT,
    createdAt = createdAt
)