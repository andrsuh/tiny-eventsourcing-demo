package ru.quipy.logic.task

import ru.quipy.api.task.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.time.LocalDateTime
import java.util.*

// класс состояния агрегата задачи, отслеживающий атрибуты задачи и ее изменения
class TaskAggregateState : AggregateState<UUID, TaskAggregate> {
    private lateinit var id: UUID
    lateinit var linkedProjectId: UUID
    lateinit var title: String
    lateinit var description: String
    var deadlineTimestamp: LocalDateTime? = null
    lateinit var currentStatusId: UUID
    var taskExecutors = mutableMapOf<UUID, TaskExecutor>()

    override fun getId() = id

    @StateTransitionFunc
    fun onTaskCreated(event: TaskCreatedEvent) {
        id = event.taskId
        linkedProjectId = event.projectId
        title = event.title
        description = event.description
        deadlineTimestamp = event.deadlineTimestamp
        currentStatusId = event.statusId
        taskExecutors.putAll(event.executors)
    }

    @StateTransitionFunc
    fun onTaskTitleUpdated(event: TaskTitleUpdatedEvent) {
        title = event.newTitle
    }

    @StateTransitionFunc
    fun onTaskDescriptionUpdated(event: TaskDescriptionUpdatedEvent) {
        description = event.newDescription
    }

    @StateTransitionFunc
    fun onExecutorAdded(event: TaskExecutorAddedEvent) {
        taskExecutors[event.executorId] = TaskExecutor(event.executorId)
    }

    @StateTransitionFunc
    fun onStatusUpdated(event: TaskStatusUpdatedEvent) {
        currentStatusId = event.newStatusId
    }

    @StateTransitionFunc
    fun onTaskDeleted(event: TaskDeletedEvent) {
        taskExecutors.clear()  // убираем всех исполнителей при удалении задачи
    }
}

// класс для представления сущности назначенного исполнителя задачи
data class TaskExecutor(
        val userId: UUID

)
