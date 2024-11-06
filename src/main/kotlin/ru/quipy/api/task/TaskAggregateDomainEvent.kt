package ru.quipy.api.task

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_TITLE_CHANGED_EVENT = "TASK_TITLE_CHANGED_EVENT"
const val TASK_STATUS_CHANGED_EVENT = "TASK_STATUS_CHANGED_EVENT"
const val TASK_EXECUTOR_ASSIGNED_EVENT = "TASK_EXECUTOR_ASSIGNED_EVENT"

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskName: String
) : Event<TaskAggregate>(
    name = TASK_CREATED_EVENT
)

@DomainEvent(name = TASK_TITLE_CHANGED_EVENT)
class TaskNameChangeEvent(
    val taskId: UUID,
    val oldTaskName: String,
    val newTaskName: String,
) : Event<TaskAggregate>(
    name = TASK_TITLE_CHANGED_EVENT
)

@DomainEvent(name = TASK_STATUS_CHANGED_EVENT)
class TaskStatusChangeEvent(
    val taskId: UUID,
    val newStatusId: UUID,
    val oldStatusId: UUID,
) : Event<TaskAggregate>(
    name = TASK_STATUS_CHANGED_EVENT
)

@DomainEvent(name = TASK_EXECUTOR_ASSIGNED_EVENT)
class TaskExecutorAssignedEvent(
    val taskId: UUID,
    val userId: UUID,
) : Event<TaskAggregate>(
    name = TASK_EXECUTOR_ASSIGNED_EVENT
)
