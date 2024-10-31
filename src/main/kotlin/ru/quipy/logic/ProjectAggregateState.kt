package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.lang.Integer.max
import java.util.*
import kotlin.math.min

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

    fun addParticipant(participantId: UUID): ParticipantAddedToProjectEvent {
        if (participants.contains(participantId)) {
            throw IllegalStateException("The user $participantId is already a member of the project $projectId")
        }

        return ParticipantAddedToProjectEvent(participantId)
    }

    fun createStatus(name: String, color: String, participantId: UUID): StatusCreatedEvent {
        checkIfProjectParticipant(participantId)

        if (orderState.values.find { x -> x.id.name.lowercase() == name.lowercase() } != null) {
            throw IllegalArgumentException("The status with name $name already exists")
        }

        return StatusCreatedEvent(
            statusName = name,
            color = Color.valueOf(color.uppercase())
        )
    }

    fun createTask(id: UUID, name: String, description: String, participantId: UUID): TaskCreatedEvent {
        checkIfProjectParticipant(participantId)
        val defaultStatus = orderState.values.find { x -> x.id == StatusId(projectId, defaultStatusName) }
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

    fun changeTaskName(taskId: UUID, newName: String, participantId: UUID): TaskNameChangedEvent {
        checkIfProjectParticipant(participantId)
        checkIfTaskExists(taskId)

        return TaskNameChangedEvent(taskId, newName)
    }

    fun changeTaskStatus(taskId: UUID, statusName: String, participantId: UUID): TaskStatusChangedEvent {
        checkIfProjectParticipant(participantId)
        checkIfTaskExists(taskId)
        checkIfStatusExists(statusName)

        return TaskStatusChangedEvent(taskId, statusName)
    }

    fun deleteTask(taskId: UUID, participantId: UUID): TaskDeletedEvent {
        checkIfProjectParticipant(participantId)
        checkIfTaskExists(taskId)

        return TaskDeletedEvent(taskId)
    }

    fun changeStatusOrder(statusName: String, newOrder: Int, participantId: UUID): StatusOrderChangedEvent {
        checkIfProjectParticipant(participantId)
        checkIfStatusesAreOrdered(statusName, newOrder)

        return StatusOrderChangedEvent(statusName, newOrder)
    }

    fun changeStatusColor(statusName: String, newColor: String, participantId: UUID): StatusColorChangedEvent {
        checkIfProjectParticipant(participantId)
        checkIfStatusExists(statusName)

        return StatusColorChangedEvent(statusName, Color.valueOf(newColor.uppercase()))
    }

    fun deleteStatus(statusName: String, participantId: UUID): StatusDeletedEvent {
        checkIfProjectParticipant(participantId)

        if (statusName.lowercase() == defaultStatusName.lowercase()) throw IllegalArgumentException("Cannot delete default status")
        checkIfStatusExists(statusName)

        if (tasks.values.any{ x -> x.status.name.lowercase() == statusName.lowercase() })
            throw IllegalStateException("Cannot delete status with tasks")

        return StatusDeletedEvent(statusName)
    }

    private fun checkIfStatusesAreOrdered(statusName: String, newOrder: Int) {
        val maxKey = orderState.keys.maxOf { x -> x }
        val minKey = orderState.keys.minOf { x -> x }

        if (newOrder > maxKey || newOrder < minKey)
            throw IllegalStateException("New order of status $statusName cannot be larger than $maxKey or smaller than $minKey")
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


    private fun checkIfStatusExists(statusName: String) {
        if (!orderState.values.any { x -> x.id.name.lowercase() == statusName.lowercase() }) {
            throw IllegalArgumentException("No such status: $statusName")
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

    @StateTransitionFunc
    fun taskNameChangedApply(event: TaskNameChangedEvent) {
        tasks[event.taskId]?.name = event.newName
    }

    @StateTransitionFunc
    fun taskStatusChangedApply(event: TaskStatusChangedEvent) {
        val status = orderState.values.first { x -> x.id.name.lowercase() == event.newStatusName.lowercase() }
        tasks[event.taskId]?.status = status.id
    }

    @StateTransitionFunc
    fun taskDeletedApply(event: TaskDeletedEvent) {
        tasks.remove(event.taskId)
    }

    @StateTransitionFunc
    fun statusOrderChangedApply(event: StatusOrderChangedEvent) {
        val oldOrder =
            orderState.filter { x -> x.value.id.name.lowercase() == event.statusName.lowercase() }.keys.first()

        if (oldOrder == event.newOrder) return

        val replacedValue = orderState.remove(oldOrder)!!
        val shiftRegion =
            orderState.keys.filter { x -> (x <= max(oldOrder, event.newOrder) && min(oldOrder, event.newOrder) <= x) }
                .sorted()

        if (oldOrder > event.newOrder) {
            for (i in shiftRegion.lastIndex downTo 0) {
                val value = orderState.remove(shiftRegion[i])!!
                orderState[shiftRegion[i] + 1] = value
            }
        } else {
            for (i in shiftRegion) {
                val value = orderState.remove(i)!!
                orderState[i - 1] = value
            }
        }

        orderState[event.newOrder] = replacedValue
    }

    @StateTransitionFunc
    fun statusColorChangedApply(event: StatusColorChangedEvent) {
        val status = orderState.values.find { x -> x.id.name.lowercase() == event.statusName.lowercase() }!!

        status.color = event.newColor
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        val key = orderState.filter { x -> x.value.id.name.lowercase() == event.statusName.lowercase() }.keys.first()
        orderState.remove(key)
    }
}


data class StatusId(
    val projectId: UUID,
    var name: String
)

data class StatusEntity(
    val id: StatusId,
    var color: Color
)

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    val description: String,
    var status: StatusId,
    val participantIds: MutableList<UUID> = mutableListOf()
)