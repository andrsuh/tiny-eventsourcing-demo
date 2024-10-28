package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val BOARD_MANAGER_CREATED_EVENT = "BOARD_MANAGER_CREATED_EVENT"
const val STATUS_CREATED_EVENT = "STATUS_CREATED_EVENT"
const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_ASSIGNEE_ADDED_EVENT = "TASK_ASSIGNEE_ADDED_EVENT"

@DomainEvent(name = BOARD_MANAGER_CREATED_EVENT)
class BoardManagerCreatedEvent(
    val projectId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskServiceAggregate>(
    name = BOARD_MANAGER_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = STATUS_CREATED_EVENT)
class StatusCreatedEvent(
    val projectId: UUID,
    val statusName: String,
    val color: Color,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskServiceAggregate>(
    name = STATUS_CREATED_EVENT,
    createdAt = createdAt
)

enum class Color {
    RED,
    GREEN,
    BLUE,
    PURPLE,
    YELLOW,
    GREY
}

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val taskId: UUID,
    val taskName: String,
    val description: String,
    val projectId: UUID,
    val statusName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskServiceAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TASK_ASSIGNEE_ADDED_EVENT)
class TaskAssigneeAddedEvent(
    val taskId: UUID,
    val participantId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskServiceAggregate>(
    name = TASK_ASSIGNEE_ADDED_EVENT,
    createdAt = createdAt
)