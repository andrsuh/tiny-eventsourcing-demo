package ru.quipy.logic.state

import javassist.NotFoundException
import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.entity.StatusEntity
import ru.quipy.entity.TaskEntity
import java.util.UUID

class TaskAndStatusAggregateState : AggregateState<UUID, TaskAndStatusAggregate> {
    val MIN_POSITION: Int = 1
    private lateinit var taskId: UUID
    private var projectStatuses = mutableMapOf<UUID, StatusEntity>()
    private var tasks = mutableMapOf<UUID, TaskEntity>()

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = taskId

    fun getTasks() = tasks.values.toList()

    fun getStatuses() = projectStatuses.values.toList()

    fun getStatusesByName(statusName: String) = projectStatuses.values.filter { s -> s.name == statusName }

    fun getTasksByName(taskName: String) = projectStatuses.values.filter { t -> t.name == taskName }

    fun getTaskById(id: UUID) = tasks[id]

    fun getStatusById(id: UUID) = projectStatuses[id]

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(
                id = event.taskId,
                name = event.taskName,
                description = event.description,
                projectId = event.projectId,
                statusId = event.statusId,
                executors = event.executors,
        )
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskUpdatedApply(event: TaskUpdatedEvent) {
        if (!tasks.containsKey(event.taskId))
            throw NotFoundException("Task ${event.taskId} does not exist.")

        tasks[event.taskId]?.name = event.taskName
        tasks[event.taskId]?.description = event.description
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun executorAddedApply(event: ExecutorAddedEvent) {
        val task = tasks[event.taskId] ?: throw NotFoundException("Task ${event.taskId} does not exist")

        tasks[event.taskId]!!.executors.add(event.userId)
        updatedAt = createdAt
    }


    @StateTransitionFunc
    fun statusChangedApply(event: TaskStatusChangedEvent) {
        if (!projectStatuses.containsKey(event.statusId))
            throw NotFoundException("Status ${event.statusId} does not exist.")

        if (!tasks.containsKey(event.taskId))
            throw NotFoundException("Task ${event.taskId} does not exist.")

        tasks[event.taskId]?.statusId = event.statusId

        updatedAt = event.createdAt
    }


    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        taskId = event.projectId
        projectStatuses[event.statusId] = StatusEntity(
                id = event.statusId,
                name = event.statusName,
                color = event.color,
                projectId = event.projectId,
                position = projectStatuses.size + 1
        )
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        val status = projectStatuses[event.statusId]
                ?: throw NotFoundException("Status ${event.statusId} does not exist.")

        projectStatuses.remove(event.statusId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusPositionChangedApply(event: StatusPositionChangedEvent) {
        val status = projectStatuses[event.statusId]
                ?: throw NotFoundException("Status with id ${event.statusId} does not exist.")

        val prevPosition = status.position

        if (event.position < MIN_POSITION || event.position > projectStatuses.size)
            throw IllegalArgumentException("Position ${event.position} out of bound.")

        if (event.position >= prevPosition) {
            projectStatuses.entries.forEach {
                if (it.value.position <= event.position && it.value.position > prevPosition) {
                    val tmp = it.value
                    tmp.position -= 1
                    projectStatuses[it.key] = tmp
                }
            }
        } else {
            projectStatuses.entries.forEach {
                if (it.value.position >= event.position && it.value.position < prevPosition) {
                    val tmp = it.value
                    tmp.position += 1
                    projectStatuses[it.key] = tmp
                }
            }
        }

        status.position = event.position
        projectStatuses[event.statusId] = status

        updatedAt = event.createdAt
    }

}
