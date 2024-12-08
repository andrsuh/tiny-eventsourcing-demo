package ru.quipy.aggregate.project

import ru.quipy.api.project.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    private var defaultStatus = StatusEntity(UUID.randomUUID(), projectId, null, null)
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    var projectMemberIds = mutableListOf<UUID>()
    var projectStatus = mutableMapOf<UUID, StatusEntity>()
    var tasks = mutableMapOf<UUID, TaskEntity>()
    lateinit var projectTitle: String
    lateinit var creatorId: UUID

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
        updatedAt = createdAt
        projectMemberIds.add(creatorId)
    }

    @StateTransitionFunc
    fun addUserToProjectApply(event: AddUserToProjectEvent) {
        projectMemberIds.add(event.userId)
    }

    @StateTransitionFunc
    fun projectTitleChangedApply(event: ProjectTitleChangedEvent) {
        projectTitle = event.title
    }

    @StateTransitionFunc
    fun taskTitleChangedApply(event: TaskTitleChangedEvent) {
        tasks[event.taskId]?.title = event.title
    }

    @StateTransitionFunc
    fun statusDeletedEvent(event: StatusDeletedEvent) {
        projectStatus.remove(event.statusId)
    }

    @StateTransitionFunc
    fun memberAssignedToTaskApply(event: MemberAssignedToTaskEvent) {
        tasks[event.taskId]?.executorId = event.userId
    }

    @StateTransitionFunc
    fun taskStatusChangedApply(event: TaskStatusChangedEvent) {
        tasks[event.taskId]?.status = event.statusId
    }

    @StateTransitionFunc
    fun statusCreatedEvent(event: StatusCreatedEvent) {
        projectStatus[event.statusId] = StatusEntity(event.statusId, projectId, event.name, event.color)
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(event.taskId, projectId, event.taskName, defaultStatus.id, null)
    }
}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    val projectId: UUID,
    var title: String,
    var status: UUID,
    var executorId: UUID?
)

data class StatusEntity(
    val id: UUID = UUID.randomUUID(),
    val projectId: UUID,
    val name: String?,
    val color: String?
)

