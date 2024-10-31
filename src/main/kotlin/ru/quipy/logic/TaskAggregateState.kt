package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class TaskAggregateState : AggregateState<UUID, TaskAggregate> {
    private lateinit var taskId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    lateinit var creatorId: UUID
    lateinit var projectId: UUID
    lateinit var description: String
    lateinit var taskName: String
    lateinit var statusId: UUID
    var users = mutableSetOf<UUID>()

    override fun getId(): UUID = taskId

    @StateTransitionFunc
    fun stateAssignedApply(event: TaskStatusChangedEvent) {
        statusId = event.statusId
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun assignUser(event: UserAddedToTaskEvent) {
        users.add(event.userId)
        updatedAt = createdAt
    }


    @StateTransitionFunc
    fun crateTask(event: TaskCreatedEvent) {
        taskId = event.taskId
        taskName = event.taskName
        description = event.description
        creatorId = event.creatorId
        projectId = event.projectId
    }

    @StateTransitionFunc
    fun changeName(event: TaskNameChangedEvent) {
        taskName = event.taskName
    }
}