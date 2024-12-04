package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(id: UUID, name: String, ownerId: UUID): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        default_status_id = DEFAULT_STATUS.statusId,
        projectName = name,
        ownerId = ownerId
    )
}

fun ProjectAggregateState.addUser(userId: UUID): ProjectUserAddedEvent {
    if (members.any { it == userId } || ownerId == userId) {
        throw IllegalArgumentException("User already in project");
    }
    return ProjectUserAddedEvent(
        projectId = this.getId(),
        userId = userId
    )
}

fun ProjectAggregateState.removeUser(userId: UUID): ProjectUserRemovedEvent {
    if (ownerId == userId) {
        throw IllegalArgumentException("Cannot remove owner");
    }
    if (!members.any { it == userId }) {
        throw IllegalArgumentException("User not found in project");
    }
    return ProjectUserRemovedEvent(
        projectId = this.getId(),
        userId = userId
    )
}

fun ProjectAggregateState.createTask(userId: UUID, taskId: UUID, name: String): ProjectTaskCreatedEvent {
    return ProjectTaskCreatedEvent(
        projectId = this.getId(),
        userId = userId,
        taskId = taskId,
        taskName = name
    )
}

fun ProjectAggregateState.modifyTask(
    taskId: UUID,
    statusId: UUID? = null,
    executors: MutableSet<UUID>? = null,
    name: String? = null
): ProjectTaskModifiedEvent {
//    println(tasks);
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task not found");
    }
    if (statusId != null && !statuses.containsKey(statusId)) {
        throw IllegalArgumentException("Status not found");
    }
    return ProjectTaskModifiedEvent(
        projectId = this.getId(),
        taskId = taskId,
        statusId = statusId,
        executors = executors,
        taskName = name,
    )
}

fun ProjectAggregateState.createStatus(name: String, statusId: UUID): ProjectStatusCreatedEvent {
    if (statuses.values.any { it.name == name } || name.isEmpty()) {
        throw IllegalArgumentException("Invalid status name");
    }
    return ProjectStatusCreatedEvent(
        projectId = this.getId(),
        statusId = statusId,
        statusName = name
    )
}


fun ProjectAggregateState.removeStatus(statusId: UUID): ProjectStatusRemovedEvent {
    if (!statuses.containsKey(statusId)) {
        throw IllegalArgumentException("Status not found");
    }
    if (!tasks.values.any { it.statusId == statusId }) {
        throw IllegalArgumentException("Status is still used");
    }
    return ProjectStatusRemovedEvent(
        projectId = this.getId(),
        statusId = statusId
    )
}