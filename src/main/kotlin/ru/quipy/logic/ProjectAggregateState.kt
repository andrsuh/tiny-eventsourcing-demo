package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.awt.Color
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    lateinit var projectTitle: String
    lateinit var creatorId: UUID
    var projectMembers = mutableSetOf<UUID>()
    var statuses = mutableMapOf<UUID, Status>()
    var tasks = mutableMapOf<UUID, Task>()
    lateinit var defaultStatus: UUID

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
        projectMembers.add(event.creatorId)
        val statusUuid = UUID.randomUUID()
        defaultStatus = statusUuid
        statuses[statusUuid] = Status(defaultStatus, "CREATED", Color.WHITE)
    }

    @StateTransitionFunc
    fun userAddedInProjectApply(event: UserAddedInProjectEvent) {
        projectMembers.add(event.userId)
    }

    @StateTransitionFunc
    fun addStatusApply(event: StatusAddedEvent) {
        statuses[event.statusId] = Status(event.statusId, event.statusName, event.color)
    }
    @StateTransitionFunc
    fun removeTaskStatusApply(event: StatusRemovedEvent) {
        statuses.remove(event.statusId)
    }

    @StateTransitionFunc
    fun createTaskApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = Task(event.taskId, event.taskName, defaultStatus, projectId)
    }

    @StateTransitionFunc
    fun changeTaskStatusApply(event: TaskStatusChangedEvent) {
        tasks[event.taskId]?.status = event.newStatusId
    }

    @StateTransitionFunc
    fun renameTaskApply(event: TaskChangedEvent) {
        tasks[event.taskId]?.taskName = event.newName
    }

    @StateTransitionFunc
    fun assigneeAddedApply(event: AssigneeAddedEvent) {
        tasks[event.taskId]?.taskAssignees?.add(event.assigneeId)
    }
}


data class Status (
        var id: UUID,
        var name: String,
        var color: Color
)

data class Task (
        var id: UUID,
        var taskName: String,
        var status: UUID,
        var projectID: UUID,
        var taskAssignees: MutableSet<UUID> = mutableSetOf()
)
/**
 * Demonstrates that the transition functions might be representer by "extension" functions, not only class members functions
 */
//@StateTransitionFunc
//fun ProjectAggregateState.tagAssignedApply(event: TagAssignedToTaskEvent) {
//    tasks[event.taskId]?.tagsAssigned?.add(event.tagId)
//        ?: throw IllegalArgumentException("No such task: ${event.taskId}")
//    updatedAt = createdAt
//}
