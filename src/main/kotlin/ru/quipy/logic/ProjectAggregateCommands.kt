package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(id: UUID, title: String, description: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        description = description
    )
}

fun ProjectAggregateState.update(title: String, description: String): ProjectUpdatedEvent {
    return ProjectUpdatedEvent(
        projectId = this.getId(),
        title = title,
        description = description
    )
}

fun ProjectAggregateState.addTask(name: String, description: String): TaskCreatedEvent {
    return TaskCreatedEvent(
        projectId = this.getId(),
        taskId = UUID.randomUUID(),
        taskName = name,
        taskDescription = description,
        statusId = this.baseStatus.id
    )
}

fun ProjectAggregateState.updateTask(taskId: UUID, name: String, description: String): TaskUpdatedEvent {
    if (!tasks.containsKey(taskId)) throw IllegalArgumentException("Task doesn't exists")
    return TaskUpdatedEvent(projectId = this.getId(), taskId = taskId, taskName = name, taskDescription = description)
}

fun ProjectAggregateState.assignTaskPerformer(taskId: UUID, user: UserAggregateState?): PerformerAssignedToTaskEvent {
    if (user == null) throw IllegalArgumentException("User doesn't exists")
    if (!tasks.containsKey(taskId)) throw IllegalArgumentException("Task doesn't exists")
    if (tasks[taskId]!!.performers.contains(user.getId())) throw IllegalArgumentException("Performer already added")
    return PerformerAssignedToTaskEvent(projectId = this.getId(), taskId = taskId, userId = user.getId())
}

fun ProjectAggregateState.createStatus(name: String): StatusCreatedEvent {
    if (projectStatuses.values.any { it.name == name }) {
        throw IllegalArgumentException("Status already exists: $name")
    }
    val newOrder = projectStatuses.values.maxByOrNull { it.order }!!.order + 1
    return StatusCreatedEvent(
        projectId = this.getId(),
        statusId = UUID.randomUUID(),
        statusName = name,
        order = newOrder
    )
}

fun ProjectAggregateState.deleteStatus(statusId: UUID): StatusDeletedEvent {
    if (!projectStatuses.containsKey(statusId)) {
        throw IllegalArgumentException("Status doesn't exists: $statusId")
    }

    if (tasks.values.any { it.statusAssigned == statusId }) {
        throw IllegalArgumentException("Status $statusId have assigned tasks")
    }

    return StatusDeletedEvent(projectId = this.getId(), statusId = statusId)
}

fun ProjectAggregateState.changeStatusOrder(statusId: UUID, newOrder: Int): StatusOrderChangedEvent {
    if (!projectStatuses.containsKey(statusId)) {
        throw IllegalArgumentException("Status doesn't exists: $statusId")
    }

    val newStatusOrder = mutableMapOf<UUID, Int>()
    newStatusOrder[statusId] = newOrder
    var lastOrder = newOrder

    projectStatuses.values.sortedBy { it.order }.forEach {
        if (it.id != statusId && it.order >= lastOrder) {
            newStatusOrder[it.id] = lastOrder + 1
            lastOrder += 1
        }
    }

    return StatusOrderChangedEvent(projectId = this.getId(), order = newStatusOrder)
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