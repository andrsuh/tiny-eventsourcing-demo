package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*
import javax.xml.bind.annotation.XmlType.DEFAULT

//TODO: разобраться с updatedAt
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID

    lateinit var projectTitle: String
    lateinit var ownerId: UUID
    var description: String = ""

    var participants = mutableSetOf<UUID>()
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var taskStatuses = mutableMapOf<UUID, TaskStatusEntity>()

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = projectId

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        ownerId = event.creatorId
        participants.add(event.creatorId)
        taskStatuses[UUID.randomUUID()] = TaskStatusEntity.DEFAULT
    }

    @StateTransitionFunc
    fun participantAddedApply(event: ProjectParticipantAddedEvent) {
        participants.add(event.userId)
        updatedAt = System.currentTimeMillis()
    }

    @StateTransitionFunc
    fun projectNameEditApply(event: ProjectNameEditedEvent) {
        projectTitle = event.newProjectName
        updatedAt = System.currentTimeMillis()
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(
            id = event.taskId,
            name = event.taskName,
            taskStatus = TaskStatusEntity.DEFAULT
        )
        updatedAt = System.currentTimeMillis()

    }

    @StateTransitionFunc
    fun taskNameEditApply(event: TaskNameEditedEvent) {
        tasks[event.taskId]?.name = event.newTaskName
        updatedAt = System.currentTimeMillis()
    }

    @StateTransitionFunc
    fun taskPerformerSet(event: TaskPerfomerSetEvent) {
        tasks[event.taskId]?.performers?.add(event.performer)
        updatedAt = System.currentTimeMillis()
    }

    @StateTransitionFunc
    fun taskPerformerDeleted(event: TaskPerfomerDeletedEvent) {
        tasks[event.taskId]?.performers?.remove(event.performer)
        updatedAt = System.currentTimeMillis()
    }

    @StateTransitionFunc
    fun taskDeleted(event: TaskDeletedEvent) {
        tasks.remove(event.taskId)
        updatedAt = System.currentTimeMillis()
    }

    @StateTransitionFunc
    fun taskStatusCreatedApply(event: TaskStatusCreatedEvent) {
        val taskId = UUID.randomUUID()
        taskStatuses[taskId] = TaskStatusEntity(taskId, event.statusName, event.statusColor)
        updatedAt = System.currentTimeMillis()
    }

    @StateTransitionFunc
    fun taskStatusSet(event: TaskStatusSetEvent) {
        val taskStatus = taskStatuses.values.first { it.name == event.statusName }
        tasks[event.taskId]?.taskStatus = taskStatus
        updatedAt = System.currentTimeMillis()
    }

    @StateTransitionFunc
    fun taskStatusDeleted(event: TaskStatusDeletedEvent) {
        val taskStatus = taskStatuses.values.first { it.name == event.statusName }
        taskStatuses.remove(taskStatus.id)
        updatedAt = System.currentTimeMillis()
    }
}

class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    var performers: MutableList<UUID> = mutableListOf(),
    var name: String,
    var taskStatus: TaskStatusEntity
)

class TaskStatusEntity(
    val id: UUID,
    val name: String,
    val color: String
) {
    companion object {

        const val DEFAULT_TASK_STATUS_NAME = "CREATED"
        const val DEFAULT_TASK_STATUS_COLOR = "GREEN"
        val DEFAULT = TaskStatusEntity(
            id = UUID.randomUUID(),
            name = DEFAULT_TASK_STATUS_NAME,
            color = DEFAULT_TASK_STATUS_COLOR,
        )
    }
}

