package ru.quipy.logic.state

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.awt.Color
import java.util.*

class TaskAndStatusAggregateState : AggregateState<UUID, TaskAndStatusAggregate> {
    private lateinit var taskId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var description: String
    lateinit var projectId: UUID
    lateinit var name: String
    var statusId: UUID? = null
    val executors = mutableListOf<UUID>()

    override fun getId() = taskId

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        taskId = event.taskId
        projectId = event.projectId
        name = event.taskName
        description = event.description
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskUpdatedApply(event: TaskUpdatedEvent) {
        name = event.taskName
        description = event.description
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun executorAddedApply(event: ExecutorAddedEvent) {
        executors.add(event.userId)
        updatedAt = event.createdAt
    }


    @StateTransitionFunc
    fun statusChangedApply(event: TaskStatusChangedEvent) {
        statusId = event.statusId
        updatedAt = event.createdAt
    }


    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        projectStatuses[event.statusId] = StatusEntity(id=event.statusId, name = event.statusName, color = event.color)
        updatedAt = event.createdAt
    }
    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        projectStatuses.remove(event.statusId)
        updatedAt = event.createdAt
    }
}