package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val EXECUTORS_ADDED_EVENT = "EXECUTORS_ADDED_EVENT"
const val TASK_EDITED_EVENT = "TASK_EDITED_EVENT"
const val TASK_STATUS_ASSIGNED_EVENT = "TASK_STATUS_ASSIGNED_EVENT"


@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskTitle: String,
    val executorId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<TaskAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = EXECUTORS_ADDED_EVENT)
class ExecutorsAddedEvent(
    val taskId: UUID,
    val executorsIds: Set<UUID>,
    createdAt: Long = System.currentTimeMillis()
) : Event<TaskAggregate>(
    name = EXECUTORS_ADDED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TASK_EDITED_EVENT)
class TaskEditedEvent(
    val taskId: UUID,
    val title: String,
    createdAt: Long = System.currentTimeMillis()
) : Event<TaskAggregate>(
    name = TASK_EDITED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TASK_STATUS_ASSIGNED_EVENT)
class TaskStatusAssignedEvent(
    val taskId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = TASK_STATUS_ASSIGNED_EVENT,
    createdAt = createdAt
)
