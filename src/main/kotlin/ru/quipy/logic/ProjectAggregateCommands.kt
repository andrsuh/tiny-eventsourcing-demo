package ru.quipy.logic

import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.StatusAssignedToTaskEvent
import ru.quipy.api.StatusCreatedEvent
import ru.quipy.api.TaskCreatedEvent
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(id: UUID, title: String, creatorId: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        creatorId = creatorId,
    )
}

fun ProjectAggregateState.addTask(name: String, description: String): TaskCreatedEvent {
    return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name, taskDescription = description)
}

fun ProjectAggregateState.createStatus(name: String, color: String): StatusCreatedEvent {
    if (projectStatuses.values.any { it.name == name }) {
        throw IllegalArgumentException("Status already exists: $name")
    }
    return StatusCreatedEvent(projectId = this.getId(), statusId = UUID.randomUUID(), statusName = name, statusColor = color)
}

fun ProjectAggregateState.assignStatusToTask(statusId: UUID, taskId: UUID): StatusAssignedToTaskEvent {
    if (!projectStatuses.containsKey(statusId)) {
        throw IllegalArgumentException("Status doesn't exists: $statusId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return StatusAssignedToTaskEvent(projectId = this.getId(), statusId = statusId, taskId = taskId)
}