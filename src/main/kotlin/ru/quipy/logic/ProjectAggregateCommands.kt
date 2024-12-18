package ru.quipy.logic

import ru.quipy.api.*
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

fun ProjectAggregateState.addTask(name: String): TaskCreatedEvent {
    return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name)
}

fun ProjectAggregateState.createTag(name: String): TagCreatedEvent {
    if (projectTags.values.any { it.name == name }) {
        throw IllegalArgumentException("Tag already exists: $name")
    }

    return TagCreatedEvent(projectId = this.getId(), tagId = UUID.randomUUID(), tagName = name)
}

fun ProjectAggregateState.assignTagToTask(tagId: UUID, taskId: UUID): TagAssignedToTaskEvent {
    if (!projectTags.containsKey(tagId)) {
        throw IllegalArgumentException("Tag doesn't exists: $tagId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return TagAssignedToTaskEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
}

fun ProjectAggregateState.addMember(userId: UUID): MemberAddedEvent{
    if (members.contains(userId)) {
        throw IllegalArgumentException("Project already contains member: $userId")
    }

    return MemberAddedEvent(projectId = this.getId(), userId = userId)
}

fun ProjectAggregateState.changeStatus(taskId: UUID, newStatus: Int): TaskStatusChangedEvent{
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    if (StatusEnum.values().size < newStatus) {
        throw IllegalArgumentException("Status out of range: $newStatus")
    }

    return TaskStatusChangedEvent(projectId = this.getId(), taskId = taskId, newStatus = newStatus)
}

fun ProjectAggregateState.addExecutor(taskId: UUID, executorId: UUID): ExecutorAssignedToTaskEvent{
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    if(!members.contains(executorId)){
        throw IllegalArgumentException("No such member: $executorId")
    }

    return ExecutorAssignedToTaskEvent(projectId = this.getId(), taskId = taskId, executorId = executorId)
}