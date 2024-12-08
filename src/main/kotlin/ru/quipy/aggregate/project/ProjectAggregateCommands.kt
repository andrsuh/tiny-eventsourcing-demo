package ru.quipy.aggregate.project

import ru.quipy.api.project.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(id: UUID, title: String, creatorId: UUID): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        creatorId = creatorId,
    )
}

fun ProjectAggregateState.addUser(projectId: UUID, userId: UUID): AddUserToProjectEvent {
    var userAlreadyExist = false;
    this.projectMemberIds.forEach { element ->
        if (element == userId){
            userAlreadyExist = true;
        }
    }

    if (userAlreadyExist){
        throw IllegalArgumentException("User already exist in this project: $userId")
    }

    return AddUserToProjectEvent(projectId = projectId, userId = userId);
}

fun ProjectAggregateState.addTask(name: String): TaskCreatedEvent {
    return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name)
}

fun ProjectAggregateState.addStatus(name: String, color: String): StatusCreatedEvent {
    return StatusCreatedEvent(projectId = this.getId(), statusId = UUID.randomUUID(), statusName = name, color = color)
}

fun ProjectAggregateState.changeTitle(projectId: UUID, title: String): ProjectTitleChangedEvent {
    return ProjectTitleChangedEvent(projectId = projectId, title = title);
}
fun ProjectAggregateState.changeTaskTitle(taskId: UUID, title: String): TaskTitleChangedEvent {
    return  TaskTitleChangedEvent(taskId = taskId, title = title)
}
fun ProjectAggregateState.removeStatus(statusId: UUID, projectId: UUID): StatusDeletedEvent {
    if (!projectStatus.containsKey(statusId)){
        throw IllegalArgumentException("Status doesn't exists: $statusId")
    }

    var statusIsUsed = false;
    this.tasks.forEach { element ->
        if (element.value.status == statusId){
            statusIsUsed = true;
        }
    }

    if (statusIsUsed){
        throw IllegalArgumentException("Status is used: $statusId")
    }

    return StatusDeletedEvent(statusId, projectId)
}

fun ProjectAggregateState.changeTaskStatus(taskId: UUID, statusId: UUID): TaskStatusChangedEvent {
    if (!projectStatus.containsKey(statusId)){
        throw IllegalArgumentException("Status doesn't exists: $statusId")
    }

    return TaskStatusChangedEvent(taskId = taskId, statusId = statusId)
}

fun ProjectAggregateState.memberAssignedToTask(userId: UUID, taskId: UUID): MemberAssignedToTaskEvent{
    var userNotExist = true;
    var taskNotExist = true;

    this.projectMemberIds.forEach { element ->
        if (element == userId){
            userNotExist = false;
        }
    }

    this.tasks.forEach { element ->
        if (element.value.id == taskId){
            taskNotExist = true;
        }
    }

    if (userNotExist){
        throw IllegalArgumentException("User not exist: $userId")
    }

    if (taskNotExist){
        throw IllegalArgumentException("Task not exist: $taskId")
    }

    return MemberAssignedToTaskEvent(taskId = taskId, userId = userId)
}