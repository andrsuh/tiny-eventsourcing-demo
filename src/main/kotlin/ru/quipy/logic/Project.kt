package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class Project : AggregateState<UUID, ProjectAggregate> {

    private var defaultStatus: Status = Status(UUID.randomUUID(), "CREATED", "green", 0)

    private lateinit var id: UUID
    private lateinit var title: String
    private lateinit var creatorId: UUID

    private var participants: MutableSet<UUID> = mutableSetOf()

    private var tasks: MutableMap<UUID, Task> = mutableMapOf()
    private var statuses: MutableMap<UUID, Status> = mutableMapOf(Pair(defaultStatus.id, defaultStatus))

    override fun getId() = id

    fun createProject(title: String, creatorId: UUID): ProjectCreatedEvent {
        return ProjectCreatedEvent(
            projectId = UUID.randomUUID(),
            title = title,
            creatorId = creatorId
        )
    }

    @StateTransitionFunc
    fun createProject(event: ProjectCreatedEvent) {
        id = event.projectId
        title = event.title
        creatorId = event.creatorId
    }

    fun changeProjectTitle(newTitle: String): ProjectTitleChangedEvent {
        if (title == newTitle) {
            throw IllegalArgumentException("New project title is the same as the old title")
        }

        return ProjectTitleChangedEvent(
            projectId = id,
            newTitle = newTitle
        )
    }

    @StateTransitionFunc
    fun changeProjectTitle(event: ProjectTitleChangedEvent) {
        title = event.newTitle
    }

    fun addParticipantToProject(userId: UUID): AddParticipantToProjectEvent {
        if (participants.contains(userId)) {
            throw IllegalArgumentException("User $userId is already a participant in project $id")
        }

        return AddParticipantToProjectEvent(
            projectId = id,
            participantId = userId
        )
    }

    @StateTransitionFunc
    fun addParticipantToProject(event: AddParticipantToProjectEvent) {
        participants.add(event.participantId)
    }

    fun createStatus(title: String, color: String): StatusCreatedEvent {
        if (statuses.values.any { it.title == title }) {
            throw IllegalArgumentException("Status $title already exists")
        }

        return StatusCreatedEvent(
            projectId = id,
            statusId = UUID.randomUUID(),
            title = title,
            color = color
        )
    }

    @StateTransitionFunc
    fun createStatus(event: StatusCreatedEvent) {
        statuses[event.statusId] = Status(event.statusId, event.title, event.color, 0)
    }

    fun deleteStatus(statusId: UUID): StatusDeletedEvent {
        if (statuses.values.none { it.id == statusId }) {
            throw IllegalArgumentException("Status $statusId doesn't exists")
        }
        if (statuses[statusId]?.taskQuantity != 0) {
            throw IllegalArgumentException("Cannot delete status $statusId, still in use")
        }

        return StatusDeletedEvent(
            projectId = id,
            statusId = UUID.randomUUID()
        )
    }

    @StateTransitionFunc
    fun deleteStatus(event: StatusDeletedEvent) {
        statuses.remove(event.statusId)
    }

    fun changeStatusTitle(statusId: UUID, newTitle: String): StatusTitleChangedEvent {
        if (statuses[statusId]?.title == newTitle) {
            throw IllegalArgumentException("New status title is the same as old status title")
        }
        if (statuses.values.any { it.title == newTitle }) {
            throw IllegalArgumentException("Status with title $newTitle already exists")
        }

        return StatusTitleChangedEvent(
            projectId = id,
            statusId = statusId,
            newTitle = newTitle
        )
    }

    @StateTransitionFunc
    fun changeStatusTitle(event: StatusTitleChangedEvent) {
        statuses[event.statusId]?.title = event.newTitle
    }

    fun createTask(title: String): TaskCreatedEvent {
        return TaskCreatedEvent(
            projectId = id,
            taskId = UUID.randomUUID(),
            title = title
        )
    }

    @StateTransitionFunc
    fun createTask(event: TaskCreatedEvent) {
        tasks[event.taskId] = Task(event.taskId, event.title, defaultStatus, mutableSetOf())
    }

    fun changeTaskTitle(taskId: UUID, newTitle: String): TaskNameChangeEvent {
        if (title == newTitle) {
            throw IllegalArgumentException("New task title is the same as the old task title")
        }

        return TaskNameChangeEvent(
            projectId = id,
            taskId = taskId,
            newTitle = newTitle
        )
    }

    @StateTransitionFunc
    fun changeTaskTitle(event: TaskNameChangeEvent) {
        tasks[event.taskId]?.title = event.newTitle
    }

    fun changeTaskStatus(taskId: UUID, newStatusId: UUID): TaskStatusChangedEvent {
        if (statuses.values.none { it.id == newStatusId }) {
            throw IllegalArgumentException("Status $newStatusId doesn't exist in project $id")
        }
        if (tasks[taskId]?.status?.id == newStatusId) {
            throw IllegalArgumentException("New status is the same as old status")
        }

        return TaskStatusChangedEvent(
            projectId = id,
            taskId = taskId,
            newStatusId = newStatusId
        )
    }

    @StateTransitionFunc
    fun changeTaskStatus(event: TaskStatusChangedEvent) {
        tasks[event.taskId]?.status?.taskQuantity = tasks[event.taskId]?.status?.taskQuantity!! - 1
        tasks[event.taskId]?.status = statuses[event.newStatusId]!!
        tasks[event.taskId]?.status?.taskQuantity = tasks[event.taskId]?.status?.taskQuantity!! + 1
    }

    fun addExecutorToTask(taskId: UUID, userId: UUID): TaskExecutorAssignedEvent {
        if (tasks[taskId]?.executors?.contains(userId) == true) {
            throw IllegalArgumentException("Executor was added already!")
        }

        return TaskExecutorAssignedEvent(
            projectId = id,
            taskId = taskId,
            userId = userId
        )
    }

    @StateTransitionFunc
    fun addExecutorToTask(event: TaskExecutorAssignedEvent) {
        tasks[event.taskId]?.executors!!.add(event.userId)
    }

    data class Status(
        val id: UUID,
        var title: String,
        val color: String,
        var taskQuantity: Int
    )

    data class Task(
        val id: UUID,
        var title: String,
        var status: Status,
        val executors: MutableSet<UUID>
    )
}
