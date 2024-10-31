package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class TaskAggregateState : AggregateState<UUID, TaskAggregate> {
    private lateinit var taskId: UUID
    private var projectId: UUID? = null
    private var title: String = ""
    private var executorsIds: MutableSet<UUID> = mutableSetOf()
    private var statusId: UUID? = null
    override fun getId() = taskId

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        taskId = event.taskId
        projectId = event.projectId
        title = event.taskTitle
        executorsIds.add(event.executorId)
        statusId = event.statusId
    }

    @StateTransitionFunc
    fun executorsAddedApply(event: ExecutorsAddedEvent) {
        taskId = event.taskId
        executorsIds.addAll(event.executorsIds)
    }

    @StateTransitionFunc
    fun taskEditedApply(event: TaskEditedEvent) {
        taskId = event.taskId
        title = event.title
    }

    @StateTransitionFunc
    fun taskStatusAssignedApply(event: TaskStatusAssignedEvent) {
        taskId = event.taskId
        statusId = event.statusId
    }
}