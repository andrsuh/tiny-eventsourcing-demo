package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.awt.Color
import java.util.*

class TaskAndStatusAggregateStatec : AggregateState<UUID, TaskAndStatusAggregate> {
    private lateinit var taskId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var description: String
    lateinit var projectId: UUID
    lateinit var name: String
    var statusId: UUID? = null
    lateinit var color: Color
    val executors = mutableListOf<UUID>()

    override fun getId() = taskId

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        taskId = event.taskId
        projectId = event.projectId
        name = event.taskName
        description = event.description
        color = if (Color.getColor(event.color) != null) Color.getColor(event.color) else throw IllegalArgumentException("Incorrect color type.")
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
    fun statusAssignedApply(event: StatusAssignedToTaskEvent) {
        statusId = event.statusId
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusChangedApply(event: TaskStatusChangedEvent) {
        statusId = event.statusId
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusRemovedApply(event: StatusRemovedFromTaskEvent) {
        statusId = null
        updatedAt = event.createdAt
    }
}
