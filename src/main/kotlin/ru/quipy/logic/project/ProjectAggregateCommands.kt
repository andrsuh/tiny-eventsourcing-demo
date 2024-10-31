package ru.quipy.logic.project

import ru.quipy.api.project.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(id: UUID, title: String, creator: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        creator = creator,
    )
}

fun ProjectAggregateState.addMember(newMember: String): MemberAddedEvent {
    return MemberAddedEvent(
        projectId = this.getId(),
        newMember = newMember,
    )
}

fun ProjectAggregateState.addTask(taskId: UUID): TaskCreatedEvent {
    return TaskCreatedEvent(
        projectId = this.getId(),
        taskId = taskId,
    )
}

fun ProjectAggregateState.delTask(taskId: UUID): TaskDeletedEvent {
    return TaskDeletedEvent(
        projectId = this.getId(),
        taskId = taskId,
    )
}

fun ProjectAggregateState.addStatus(status: String): StatusAddedEvent {
    return StatusAddedEvent(
        projectId = this.getId(),
        addedStatus = status,
    )
}

fun ProjectAggregateState.delStatus(status: String): StatusDeletedEvent {
    return StatusDeletedEvent(
        projectId = this.getId(),
        deletedStatus = status,
    )
}
