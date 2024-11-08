package ru.quipy.api.task

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.logic.task.TaskExecutor
import java.time.LocalDateTime
import java.util.*

// константы для идентификации различных типов событий, связанных с задачей
const val EVENT_TASK_CREATED = "EVENT_TASK_CREATED"
const val EVENT_TASK_STATUS_UPDATED = "EVENT_TASK_STATUS_UPDATED"
const val EVENT_TASK_TITLE_UPDATED = "EVENT_TASK_TITLE_UPDATED"
const val EVENT_TASK_DESCRIPTION_UPDATED = "EVENT_TASK_DESCRIPTION_UPDATED"
const val EVENT_TASK_EXECUTOR_ADDED = "EVENT_TASK_EXECUTOR_ADDED"
const val EVENT_TASK_REMOVED = "EVENT_TASK_REMOVED"

// определение событий для задач
@DomainEvent(name = EVENT_TASK_CREATED)
class TaskCreatedEvent(
        val taskId: UUID,
        val projectId: UUID, // id проекта, к которому относится задача
        val title: String,
        val description: String,
        val deadlineTimestamp: LocalDateTime,
        val statusId: UUID,  // начальный статус задачи
        val executors: Map<UUID, TaskExecutor> = emptyMap(), // назначенные исполнители задачи
        createdAt: Long = System.currentTimeMillis() // временная метка создания
) : Event<TaskAggregate>(
        name = EVENT_TASK_CREATED,
        createdAt = createdAt
)

@DomainEvent(name = EVENT_TASK_STATUS_UPDATED)
class TaskStatusUpdatedEvent(
        val taskId: UUID,
        val newStatusId: UUID, // новый статус задачи
        updatedAt: Long = System.currentTimeMillis() // временная метка обновления
) : Event<TaskAggregate>(
        name = EVENT_TASK_STATUS_UPDATED,
        createdAt = updatedAt
)

@DomainEvent(name = EVENT_TASK_TITLE_UPDATED)
class TaskTitleUpdatedEvent(
        val taskId: UUID,
        val newTitle: String,
        updatedAt: Long = System.currentTimeMillis()
) : Event<TaskAggregate>(
        name = EVENT_TASK_TITLE_UPDATED,
        createdAt = updatedAt
)

@DomainEvent(name = EVENT_TASK_DESCRIPTION_UPDATED)
class TaskDescriptionUpdatedEvent(
        val taskId: UUID,
        val newDescription: String, // новое описание задачи
        updatedAt: Long = System.currentTimeMillis()
) : Event<TaskAggregate>(
        name = EVENT_TASK_DESCRIPTION_UPDATED,
        createdAt = updatedAt
)

@DomainEvent(name = EVENT_TASK_EXECUTOR_ADDED)
class TaskExecutorAddedEvent(
        val taskId: UUID,
        val executorId: UUID, // идентификатор назначенного пользователя
        assignedAt: Long = System.currentTimeMillis()
) : Event<TaskAggregate>(
        name = EVENT_TASK_EXECUTOR_ADDED,
        createdAt = assignedAt
)

@DomainEvent(name = EVENT_TASK_REMOVED)
class TaskDeletedEvent(
        val taskId: UUID,
        val projectId: UUID, // id проекта, к которому привязана задача
        removedAt: Long = System.currentTimeMillis() // временная метка удаления
) : Event<TaskAggregate>(
        name = EVENT_TASK_REMOVED,
        createdAt = removedAt
)
