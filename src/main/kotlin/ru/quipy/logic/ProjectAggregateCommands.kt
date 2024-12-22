package ru.quipy.logic

import ru.quipy.api.*
import java.awt.Color
import java.util.*
import kotlin.streams.toList


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(id: UUID, title: String, creatorId: UUID, defaultStatus: Status): ProjectCreatedEvent {
    return ProjectCreatedEvent(
            projectId = id,
            title = title,
            creatorId = creatorId,
            defaultStatus = defaultStatus,
    )
}

fun ProjectAggregateState.addUserInProject(userId: UUID, projectId: UUID, adderId: UUID,): UserAddedInProjectEvent {
    if (projectMembers.contains(userId))
        throw IllegalArgumentException("User already in project")

    if (!projectMembers.contains(adderId))
        throw IllegalArgumentException("Adder must be in project")

    return UserAddedInProjectEvent(
            userId = userId,
            projectId = projectId,
            adderId = adderId
    )
}

fun ProjectAggregateState.addStatus(statusName: String, color: Color, statusId: UUID, projectId: UUID, adderId: UUID): StatusAddedEvent {
    if (!projectMembers.contains(adderId))
        throw IllegalArgumentException("Adder must be in project")

    return StatusAddedEvent(
            statusName = statusName,
            color = color,
            statusId = statusId,
            projectId = projectId,
            adderId = adderId
    )
}

fun ProjectAggregateState.removeStatus(statusId: UUID, removerId: UUID): StatusRemovedEvent {
    if (!projectMembers.contains(removerId))
        throw IllegalArgumentException("Remover must be in project")
    if (defaultStatus == statusId)
        throw IllegalArgumentException("Could not remove default status")
    if (tasks.values.stream().filter{x -> x.status == statusId}.toList().isNotEmpty())
        throw IllegalArgumentException("Could not remove not empty status")

    return StatusRemovedEvent(
            statusId = statusId,
            removerId = removerId,
    )
}

fun ProjectAggregateState.changeTaskStatus(taskId: UUID, newStatusId: UUID, changerId: UUID): TaskStatusChangedEvent {
    if (!projectMembers.contains(changerId))
        throw IllegalArgumentException("Changer must be in project")
    if (statuses[newStatusId] == null)
        throw IllegalArgumentException("Status not found")

    return TaskStatusChangedEvent(
            taskId = taskId,
            newStatusId = newStatusId,
            changerId = changerId,
    )
}

fun ProjectAggregateState.createTask(taskId: UUID, taskName: String, projectID: UUID, creatorId: UUID): TaskCreatedEvent {
    if (!projectMembers.contains(creatorId))
        throw IllegalArgumentException("Creator must be in project")

    return TaskCreatedEvent(
            taskId = taskId,
            taskName = taskName,
            status = defaultStatus,
            projectId = projectID,
            creatorId = creatorId
    )
}

fun ProjectAggregateState.changeTask(taskId: UUID, newName: String, changerId: UUID): TaskChangedEvent {
    if (!projectMembers.contains(changerId))
        throw IllegalArgumentException("Changer must be in project")

    return TaskChangedEvent(
            taskId = taskId,
            newName = newName,
            changerId = changerId,
    )
}

fun ProjectAggregateState.addAssignee(taskId: UUID, assigneeId: UUID, adderId: UUID): AssigneeAddedEvent {
    if (!projectMembers.contains(adderId))
        throw IllegalArgumentException("Adder must be in project")

    val assignees = tasks[taskId]?.taskAssignees?:throw IllegalArgumentException("Task not found")
    if (assignees.contains(assigneeId)) {
        throw IllegalArgumentException("This user is assignee already")
    }

    return AssigneeAddedEvent(
            taskId = taskId,
            assigneeId = assigneeId,
            adderId = adderId,
    )
}