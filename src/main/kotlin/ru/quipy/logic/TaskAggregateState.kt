package ru.quipy.logic


import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.logic.ProjectAggregateState.Companion.defaultStatus
import java.lang.RuntimeException
import java.util.*

class TaskAggregateState : AggregateState<UUID, TaskAggregate> {
    private lateinit var taskId: UUID
    lateinit var projectID: UUID
    lateinit var taskName: String
    lateinit var statusId: UUID
    lateinit var oldStatusId: UUID
    lateinit var creatorId: UUID
    lateinit var executors: MutableList<UUID>

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = taskId

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent){
        taskId = event.taskId
        projectID = event.projectId
        taskName = event.title
        executors = event.executors
        creatorId = event.creatorId
        statusId = event.statusId
        oldStatusId = defaultStatus.id
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusChangedApply(event: StatusChangedEvent){
        oldStatusId = statusId
        statusId = event.statusId
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskExecutorAddedApply(event: TaskExecutorAddedEvent){
        executors.add(event.userId)
    }


}