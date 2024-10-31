package ru.quipy.logic

import org.springframework.stereotype.Component
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState.Companion.defaultStatus
import java.lang.RuntimeException
import java.util.*

@Component
class TaskAggregateStateCommands (val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>) {
    fun createTask(projectId: UUID, title: String, executors : MutableList<UUID>, creatorId : UUID) : TaskCreatedEvent {
        return TaskCreatedEvent(
            taskId = UUID.randomUUID(),
            projectId = projectId,
            title = title,
            executors = executors,
            creatorId = creatorId,
            statusId = defaultStatus.id
        )
    }

    fun changeStatus(taskId: UUID, statusId: UUID, projectID: UUID) : StatusChangedEvent {
        if (!projectEsService.getState(projectID)!!.statuses.containsKey(statusId))
            throw RuntimeException("There isn't this status in project $projectID")
        return StatusChangedEvent(
            taskId = taskId,
            statusId = statusId
        )
    }

    fun addTaskExecutor(taskId: UUID, userId: UUID) : TaskExecutorAddedEvent {
        return TaskExecutorAddedEvent(
            taskId = taskId,
            userId = userId
        )
    }
}