package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*
import kotlin.collections.mutableMapOf

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectName: String
    lateinit var authorUsername: String
    lateinit var authorFullName: String
    lateinit var description: String
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var taskStatuses = mutableMapOf<UUID, TaskStatusEntity>()
    var participants = mutableMapOf<UUID, ParticipantEntity>()

    override fun getId() = projectId

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectName = event.projectName
        description = event.description
        authorUsername = event.authorUsername
        authorFullName = event.authorFullName
        taskStatuses = event.taskStatuses
        participants = event.participants
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskStatusCreatedApply(event: TaskStatusCreatedEvent) {
        taskStatuses[event.taskStatusId] = TaskStatusEntity(event.taskStatusId, event.taskStatusName, event.taskStatusColour)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(
            id = event.taskId,
            name = event.taskName,
            taskStatusesAssigned = withDefaultTaskStatusUuid(),
            performersAssigned = mutableSetOf<UUID>())
        updatedAt = createdAt
    }


    @StateTransitionFunc
    fun taskStatusAssignedApply(event: TaskStatusAssignedToTaskEvent) {
        tasks[event.taskId]?.taskStatusesAssigned?.add(event.taskStatusId)
            ?: throw IllegalArgumentException("No such task: ${event.taskId}")
        updatedAt = createdAt
    }
    
    @StateTransitionFunc
    fun participantAddedApply(event: ParticipantAddedEvent) {
        if (participants.values.any { it.username == event.participantUsername }) {
            throw IllegalArgumentException("Participant already added: ${event.participantUsername}")
        }
        participants[event.participantId] = ParticipantEntity(event.participantId, event.participantUsername, event.participantFullName)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun performerAddedToTaskApply(event: PerformerAddedToTaskEvent) {
        if (!participants.values.any { it.id == event.participantId }) {
            throw IllegalArgumentException("No such participant: ${event.participantId}")
        }
        tasks[event.taskId]?.performersAssigned?.add(event.participantId)
            ?: throw IllegalArgumentException("No such task: ${event.taskId}")
        updatedAt = createdAt
    }


    fun withDefaultTaskStatus() : MutableMap<UUID, TaskStatusEntity> {
        val id = UUID.randomUUID()
        val defaultTaskStatus = TaskStatusEntity(id = id, name = "Created", colour = "Blue")

        return mutableMapOf<UUID, TaskStatusEntity>(id to defaultTaskStatus)
    }

    fun withDefaultTaskStatusUuid() : MutableSet<UUID> {
        if (taskStatuses.values.any { it.name == "Created" }) {
            val defaultTaskStatus = taskStatuses.values.first { it.name == "Created" }
            return mutableSetOf<UUID>(defaultTaskStatus.id)
        }
        else {
            return mutableSetOf<UUID>()
        }
    }

    fun withAuthorParticipant(authorUsername: String, authorFullName: String) : MutableMap<UUID, ParticipantEntity> {
        val participant = ParticipantEntity(username = authorUsername, fullName = authorFullName)

        return mutableMapOf<UUID, ParticipantEntity>(participant.id to participant)
    }
}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val taskStatusesAssigned: MutableSet<UUID>,
    val performersAssigned: MutableSet<UUID>
)

data class TaskStatusEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val colour: String,
)

data class ParticipantEntity(
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val fullName: String,
)
