package ru.quipy.logic.task

import ru.quipy.api.task.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun TaskAggregateState.create(title: String, projectId: UUID): TaskCreatedEvent {
    return TaskCreatedEvent(
        taskId = UUID.randomUUID(),
        title = title,
        projectId = projectId,
    )
}

fun TaskAggregateState.changeTitle(title: String): ChangeTasksTitleEvent {
    return ChangeTasksTitleEvent(taskId = this.getId(), title = title)
}

fun TaskAggregateState.addExecutor(executor: String): AddTaskExecutorEvent {
    return AddTaskExecutorEvent(taskId = this.getId(), executor = executor)
}

fun TaskAggregateState.delExecutor(executor: String): DelTaskExecutorEvent {
    return DelTaskExecutorEvent(taskId = this.getId(), executor = executor)
}

fun TaskAggregateState.changeStatus(status: String): ChangeTaskStatusEvent {
    return ChangeTaskStatusEvent(taskId = this.getId(), status = status)
}

fun TaskAggregateState.deleteTask(): DeleteTaskEvent {
    return DeleteTaskEvent(taskId = this.getId())
}