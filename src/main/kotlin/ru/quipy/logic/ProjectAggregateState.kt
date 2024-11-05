package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectTitle: String
    lateinit var creatorId: UUID
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var statuses = mutableMapOf<UUID, StatusEntity>()
    var members = mutableMapOf<UUID, ProjectMemberEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
        members[event.creatorMemberId] = ProjectMemberEntity(event.creatorMemberId, event.creatorId)
        statuses[event.defaultStatusId] = StatusEntity(event.defaultStatusId, "CREATED", "White")
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, event.statusId, mutableMapOf())
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun userAssignedApply(event: UserAssignedEvent) {
        tasks[event.taskId]!!.executors[event.memberExecutorId] = ExecutorEntity(event.memberExecutorId, event.memberId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusChangedApply(event: TaskStatusChangedEvent) {
        tasks[event.taskId]!!.status = event.statusId
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusCreatedApply(event: TaskStatusCreatedEvent) {
        statuses[event.statusId] = StatusEntity(event.statusId, event.statusText, event.statusColor)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun userAddedToProjectApply(event: UserAddedToProjectEvent) {
        members[event.userMemberId] = ProjectMemberEntity(event.userMemberId, event.userId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskTittleChangedApply(event: TaskTittleChangedEvent) {
        tasks[event.taskId]!!.name = event.newTittle
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusDeletedApply(event: TaskStatusDeletedEvent) {
        statuses.remove(event.statusId)
        updatedAt = event.createdAt
    }
}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var status: UUID,
    val executors: MutableMap<UUID, ExecutorEntity>,
)

data class StatusEntity(
    val id: UUID = UUID.randomUUID(),
    val statusText: String,
    val statusColor: String,
)

data class ProjectMemberEntity(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
)

data class ExecutorEntity(
    val id: UUID = UUID.randomUUID(),
    val projectMemberId: UUID,
)
