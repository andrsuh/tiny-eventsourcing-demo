package ru.quipy.api.task

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val CHANGE_TASKS_TITLE_EVENT = "CHANGE_TASKS_TITLE_EVENT"
const val ADD_TASKS_EXECUTOR_EVENT = "ADD_TASKS_EXECUTOR_EVENT"
const val DEL_TASK_EXECUTOR_EVENT = "DEL_TASK_EXECUTOR_EVENT"
const val CHANGE_TASK_STATUS_EVENT = "CHANGE_TASK_STATUS_EVENT"
const val DELETE_TASK_EVENT = "DELETE_TASK_EVENT"

// API

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val taskId: UUID,
    val title: String,
    val projectId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = CHANGE_TASKS_TITLE_EVENT)
class ChangeTasksTitleEvent(
    val taskId: UUID,
    val title: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = CHANGE_TASKS_TITLE_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = ADD_TASKS_EXECUTOR_EVENT)
class AddTaskExecutorEvent(
    val taskId: UUID,
    val executor: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = ADD_TASKS_EXECUTOR_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = DEL_TASK_EXECUTOR_EVENT)
class DelTaskExecutorEvent(
    val taskId: UUID,
    val executor: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = DEL_TASK_EXECUTOR_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = CHANGE_TASK_STATUS_EVENT)
class ChangeTaskStatusEvent(
    val taskId: UUID,
    val status: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = CHANGE_TASK_STATUS_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = DELETE_TASK_EVENT)
class DeleteTaskEvent(
    val taskId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = DELETE_TASK_EVENT,
    createdAt = createdAt
)
