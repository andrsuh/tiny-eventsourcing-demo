package ru.quipy.logic.task

import ru.quipy.api.task.*
import ru.quipy.projections.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.UUID

// Service's business logic
class TaskAggregateState: AggregateState<UUID, TaskAggregate> {
    private lateinit var taskId: UUID
    lateinit var taskName: String
    lateinit var statusId: UUID
    lateinit var projectId: UUID

    var executors = mutableSetOf<UUID>()
    override fun getId() = taskId

    @StateTransitionFunc
    fun createTask(event: TaskCreatedEvent){
        taskId = event.taskId
        projectId = event.projectId
        taskName = event.taskName
    }

    @StateTransitionFunc
    fun changeTaskTitle(event: TaskNameChangeEvent){
        taskName = event.newTaskName
    }

    @StateTransitionFunc
    fun changeTaskStatus(event: TaskStatusChangeEvent){
        statusId = event.newStatusId
    }

    @StateTransitionFunc
    fun addExecutorToTask(event: TaskExecutorAssignedEvent){
        executors.add(event.userId)
    }
}