package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(projectId: UUID, title: String, creatorId: UUID): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = projectId,
        title = title,
        creatorId = creatorId,
        creatorMemberId = UUID.randomUUID(),
        defaultStatusId = UUID.randomUUID()
    )
}

fun ProjectAggregateState.createTask(taskId: UUID, taskName: String, statusId: UUID) : TaskCreatedEvent {
    if (!statuses.containsKey(statusId)) {
        throw IllegalArgumentException("Status doesn't exists: $statusId")
    }

    return TaskCreatedEvent(
        projectId = this.getId(),
        taskId = taskId,
        taskName = taskName,
        statusId = statusId,
    )
}

fun ProjectAggregateState.assignUser(memberExecutorId: UUID, taskId:UUID, memberId: UUID) : UserAssignedEvent {
    if (!members.containsKey(memberId)) {
        throw IllegalArgumentException("Member doesn't exists: $memberId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return UserAssignedEvent(
        projectId = getId(),
        memberId = memberId,
        memberExecutorId = memberExecutorId,
        taskId = taskId
    )
}

fun ProjectAggregateState.changeTaskStatus(taskId: UUID, statusId: UUID) : TaskStatusChangedEvent {
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    if (!statuses.containsKey(statusId)) {
        throw IllegalArgumentException("Status doesn't exists: $statusId")
    }

    return TaskStatusChangedEvent(
        projectId = getId(),
        taskId = taskId,
        statusId = statusId,
    )
}

fun ProjectAggregateState.createStatus(statusId: UUID, statusText: String, statusColor: String) : TaskStatusCreatedEvent {
    return TaskStatusCreatedEvent(
        projectId = getId(),
        statusId = statusId,
        statusText = statusText,
        statusColor = statusColor
    )
}

fun ProjectAggregateState.addUser(userId: UUID, userMemberId: UUID) : UserAddedToProjectEvent {
    if (members.values.any { it.userId.toString().equals(userId.toString()) }) {
        throw IllegalArgumentException("Member already added: $userId")
    }

    return UserAddedToProjectEvent(
        projectId = getId(),
        userId = userId,
        userMemberId = userMemberId,
    )
}

fun ProjectAggregateState.changeTaskTitle(taskId: UUID, newTittle: String) : TaskTittleChangedEvent {
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }
    return TaskTittleChangedEvent(
        projectId = getId(),
        taskId = taskId,
        newTittle = newTittle
    )
}

fun ProjectAggregateState.deleteStatus(statusId: UUID) : TaskStatusDeletedEvent {
    if (tasks.values.any {it.status == statusId}) {
        throw IllegalArgumentException("Status $statusId still have assigned tasks")
    }
    return TaskStatusDeletedEvent(
        projectId = getId(),
        statusId = statusId
    )
}