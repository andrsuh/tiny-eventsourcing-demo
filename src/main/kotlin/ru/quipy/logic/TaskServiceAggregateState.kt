package ru.quipy.logic

import org.springframework.beans.factory.annotation.Autowired
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class TaskServiceAggregateState : AggregateState<UUID, TaskServiceAggregate> {
    @Autowired
    private lateinit var participantEsService: EventSourcingService<UUID, ParticipantAggregate, ParticipantAggregateState>

    private val defaultStatusName = "CREATED"
    private val defaultStatusColor = Color.GREEN

    private lateinit var projectId: UUID // boardManagerId == projectId
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    // todo: maybe it will be better to merge this two fields into one collection mutableMapOf<Int, StatusEntity>()
    var orderState = mutableMapOf<Int, StatusId>()
    val statuses = mutableMapOf<StatusId, StatusEntity>()
    var tasks = mutableMapOf<UUID, TaskEntity>()

    override fun getId() = projectId

    fun create(projectId: UUID, participantId: UUID): BoardManagerCreatedEvent {
        checkIfProjectParticipant(projectId, participantId)

        val boardManagerCreatedEvent = BoardManagerCreatedEvent(
            projectId = projectId
        )
        createStatus(defaultStatusName, defaultStatusColor.name, projectId, participantId)

        return boardManagerCreatedEvent
    }

    fun createStatus(name: String, color: String, projectId: UUID, participantId: UUID): StatusCreatedEvent {
        checkIfProjectParticipant(projectId, participantId)

        return StatusCreatedEvent(
            projectId = projectId,
            statusName = name,
            color = Color.valueOf(color)
        )
    }

    fun createTask(id: UUID, name: String, description: String, projectId: UUID, participantId: UUID): TaskCreatedEvent {
        checkIfProjectParticipant(projectId, participantId)
        val defaultStatus = statuses[StatusId(projectId, defaultStatusName)]
            ?: throw IllegalStateException(
                "Ну и зачем вы удалили дефолтный статус?? " +
                        "Теперь задачи нельзя создать, потому что мы так спроектировали нашу систему)"
            )

        val taskCreatedEvent = TaskCreatedEvent(
            taskId = id,
            taskName = name,
            description = description,
            projectId = defaultStatus.id.projectId,
            statusName = defaultStatus.id.name
        )
        addTaskAssignee(id, participantId)

        return taskCreatedEvent
    }

    fun addTaskAssignee(taskId: UUID, participantId: UUID): TaskAssigneeAddedEvent {
        checkIfProjectParticipant(projectId, participantId)
        checkIfTaskExists(taskId)

        return TaskAssigneeAddedEvent(taskId, participantId)
    }

    private fun checkIfProjectParticipant(projectId: UUID, participantId: UUID) {
        val participant = participantEsService.getState(participantId)
            ?: throw IllegalArgumentException("No such participant: $participantId")

        if (!participant.projects.contains(projectId)) {
            throw IllegalAccessException("User $participantId is not participant of the project $projectId")
        }
    }

    private fun checkIfTaskExists(taskId: UUID) {
        if (!tasks.containsKey(taskId)) {
            throw IllegalArgumentException("No such task: $taskId")
        }
    }

    @StateTransitionFunc
    fun boardManagerCreatedApply(event: BoardManagerCreatedEvent) {
        projectId = event.projectId
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        val statusId = StatusId(event.projectId, event.statusName)
        orderState[orderState.keys.stream().max(Int::compareTo).get() + 1] = statusId
        statuses[statusId] = StatusEntity(statusId, event.color)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(
            event.taskId,
            event.taskName,
            event.description,
            StatusId(event.projectId, event.statusName)
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