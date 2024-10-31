package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private val defaultStatusName = "CREATED"
    private val defaultStatusColor = Color.GREEN

    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectTitle: String
    lateinit var creatorId: UUID

    var participants = mutableListOf<UUID>()

    var orderState = mutableMapOf<Int, StatusEntity>()
    var tasks = mutableMapOf<UUID, TaskEntity>()

    override fun getId() = projectId

    fun createProject(id: UUID, title: String, creatorId: UUID): ProjectCreatedEvent {
        return ProjectCreatedEvent(
            projectId = id,
            title = title,
            creatorId = creatorId,
        )
    }

    fun addParticipant(participantId: UUID) : ParticipantAddedToProjectEvent {
        if (participants.contains(participantId)) {
            throw IllegalStateException("The user $participantId is already a member of the project $projectId")
        }

        return ParticipantAddedToProjectEvent(participantId)
    }

    fun createStatus(name: String, color: String, participantId: UUID): StatusCreatedEvent {
        checkIfProjectParticipant(participantId)

        if (orderState.values.find { x -> x.id.name == name } != null) {
            throw IllegalArgumentException("The status with name $name already exists")
        }

        return StatusCreatedEvent(
            statusName = name,
            color = Color.valueOf(color.uppercase(Locale.getDefault()))
        )
    }

    fun createTask(id: UUID, name: String, description: String, participantId: UUID): TaskCreatedEvent {
        checkIfProjectParticipant(participantId)
        val defaultStatus = orderState.values.find{ x -> x.id == StatusId(projectId, defaultStatusName)}
            ?: throw IllegalStateException("Default status $defaultStatusName not found")

        return TaskCreatedEvent(
            taskId = id,
            taskName = name,
            description = description,
            statusName = defaultStatus.id.name
        )
    }

    fun addTaskAssignee(taskId: UUID, participantId: UUID): TaskAssigneeAddedEvent {
        checkIfProjectParticipant(participantId)
        checkIfTaskExists(taskId)

        if (tasks[taskId]?.participantIds?.contains(participantId) == true) {
            throw IllegalStateException("The user $participantId is already the executor of the task $taskId")
        }

        return TaskAssigneeAddedEvent(taskId, participantId)
    }

    private fun checkIfProjectParticipant(participantId: UUID) {
        if (!participants.contains(participantId)) {
            throw IllegalAccessException("User $participantId is not participant of the project $projectId")
        }
    }

    private fun checkIfTaskExists(taskId: UUID) {
        if (!tasks.containsKey(taskId)) {
            throw IllegalArgumentException("No such task: $taskId")
        }
    }

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
        participants.add(event.creatorId)
        orderState[1] = StatusEntity(StatusId(event.projectId, defaultStatusName), defaultStatusColor)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun participantAddedToProjectApply(event: ParticipantAddedToProjectEvent) {
        participants.add(event.participantId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        val statusId = StatusId(projectId, event.statusName)
        orderState[orderState.keys.stream().max(Int::compareTo).get() + 1] = StatusEntity(statusId, event.color)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(
            event.taskId,
            event.taskName,
            event.description,
            StatusId(projectId, event.statusName)
        )
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskAssigneeAddedApply(event: TaskAssigneeAddedEvent) {
        checkIfTaskExists(event.taskId)
        tasks[event.taskId]?.participantIds?.add(event.participantId)
        updatedAt = createdAt
    }
}


data class StatusId(
    val projectId: UUID,
    val name: String
)

data class StatusEntity(
    val id: StatusId,
    val color: Color
)

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    val status: StatusId,
    val participantIds: MutableList<UUID> = mutableListOf()
)