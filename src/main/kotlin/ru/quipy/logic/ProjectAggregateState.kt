package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var baseStatus: StatusEntity = StatusEntity()
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectTitle: String
    lateinit var projectDescription: String
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var projectStatuses = mutableMapOf<UUID, StatusEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        projectDescription = event.description
        updatedAt = createdAt
        projectStatuses[baseStatus.id] = baseStatus
    }

    @StateTransitionFunc
    fun projectUpdatedApply(event: ProjectUpdatedEvent) {
        projectTitle = event.title
        projectDescription = event.description
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        projectStatuses[event.statusId] = StatusEntity(event.statusId, event.statusName, event.order)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        projectStatuses.remove(event.statusId) ?: throw IllegalArgumentException("No such status: ${event.statusId}")
        updatedAt = createdAt
    }
    
    @StateTransitionFunc
    fun statusOrderChangedApply(event: StatusOrderChangedEvent) {
        event.order.forEach {
            entry ->
            if (projectStatuses[entry.key] == null) throw IllegalArgumentException("No such status: ${entry.key}")
            projectStatuses[entry.key]!!.order = entry.value
        }
        updatedAt = createdAt
    }


    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, event.taskDescription, event.statusId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskUpdatedApply(event: TaskUpdatedEvent) {
        if (tasks[event.taskId] == null) throw IllegalArgumentException("No such task: ${event.taskId}")
        tasks[event.taskId]!!.name = event.taskName
        tasks[event.taskId]!!.description = event.taskDescription
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskPerformerAssignedApply(event: PerformerAssignedToTaskEvent) {
        if (tasks[event.taskId] == null) throw IllegalArgumentException("No such task: ${event.taskId}")
        tasks[event.taskId]!!.performers.add(event.userId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusAssignedApply(event: StatusAssignedToTaskEvent) {
        if (tasks[event.taskId] == null) throw IllegalArgumentException("No such task: ${event.taskId}")
        tasks[event.taskId]!!.statusAssigned = event.statusId
        updatedAt = createdAt
    }
}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var description: String,
    var statusAssigned: UUID,
    val performers: ArrayList<UUID> = ArrayList<UUID>()
)

data class StatusEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String = "CREATED",
    var order: Int = 0
)

/**
 * Demonstrates that the transition functions might be representer by "extension" functions, not only class members functions
 */

