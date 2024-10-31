package ru.quipy.logic.task

import ru.quipy.api.task.*
import ru.quipy.api.task.TaskAggregate
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class TaskAggregateState : AggregateState<UUID, TaskAggregate> {
    private lateinit var taskId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var title: String
    lateinit var projectId: UUID
    lateinit var status: String
    var executors = mutableSetOf<String>()

    override fun getId() = taskId

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        taskId = event.taskId
        title = event.title
        projectId = event.projectId
        createdAt = event.createdAt
        updatedAt = createdAt
        status = "CREATED"
    }

    @StateTransitionFunc
    fun changeTasksTitleApply(event: ChangeTasksTitleEvent) {
        title = event.title
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun addTaskExecutorApply(event: AddTaskExecutorEvent) {
        executors.add(event.executor)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun delTaskExecutorApply(event: DelTaskExecutorEvent) {
        executors.remove(event.executor)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun changeTaskStatusApply(event: ChangeTaskStatusEvent) {
        status = event.status
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun deleteTaskApply(event: DeleteTaskEvent) {
        status = "DELETED"
        updatedAt = event.createdAt
    }

}
