package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


fun ProjectAggregateState.create(title: String, creatorId: UUID): ProjectCreatedEvent {
    val projectId = UUID.nameUUIDFromBytes(title.toByteArray())

    return ProjectCreatedEvent(
        projectId = projectId,
        title = title,
        creatorId = creatorId,
    )
}


fun ProjectAggregateState.addParticipant(userId: UUID): ProjectParticipantAddedEvent {
    require(!participants.contains(userId)) { "Пользователь не может быть добавлен, если он уже участник проекта" }

    return ProjectParticipantAddedEvent(
        userId = userId,
    )
}

fun ProjectAggregateState.nameEdited(newProjectName: String, userId: UUID): ProjectNameEditedEvent {
    require(ownerId == userId) { "Пользователь должен быть владельцем проекта" }

    return ProjectNameEditedEvent(
        newProjectName = newProjectName,
    )
}


fun ProjectAggregateState.taskStatusCreatedEvent(
    statusColor: String,
    statusName: String,
    userId: UUID
): TaskStatusCreatedEvent {
    require(participants.contains(userId)) { "Пользователь должен быть участником проекта" }
    require(!taskStatuses.values.any { it.name == statusName }) { "Статус $statusName уже существует в проекте" }

    return TaskStatusCreatedEvent(
        statusColor = statusColor,
        statusName = statusName
    )
}


fun ProjectAggregateState.taskStatusSetEvent(
    taskId: UUID,
    statusName: String,
    userId: UUID
): TaskStatusSetEvent {
    require(participants.contains(userId)) { "Пользователь должен быть участником проекта" }
    require(taskStatuses.values.any { it.name == statusName }) { "Статус $statusName должен существовать в проекте" }

    return TaskStatusSetEvent(
        taskId = taskId,
        statusName = statusName
    )
}

fun ProjectAggregateState.taskStatusDeletedEvent(
    statusName: String,
    userId: UUID
): TaskStatusDeletedEvent {
    require(participants.contains(userId)) { "Пользователь должен быть участником проекта" }
    require(tasks.none { it.value.taskStatus.name == statusName }) { "Задач с данным статусом не должно быть" }
    require(statusName != TaskStatusEntity.DEFAULT_TASK_STATUS_NAME) { "Статус ${TaskStatusEntity.DEFAULT_TASK_STATUS_NAME} не может быть удален" }

    return TaskStatusDeletedEvent(
        statusName = statusName
    )
}

fun ProjectAggregateState.taskNameEditedEvent(taskId: UUID, newTaskName: String, userId: UUID): TaskNameEditedEvent {
    require(participants.contains(userId)) { "Пользователь должен быть участником проекта" }

    return TaskNameEditedEvent(
        taskId = taskId,
        newTaskName = newTaskName,
    )
}

fun ProjectAggregateState.taskPerformerSetEvent(taskId: UUID, performerId: UUID, userId: UUID): TaskPerfomerSetEvent {
    require(participants.contains(userId)) { "Пользователь должен быть участником проекта" }
    require(participants.contains(performerId)) { "Назначаемый исполнитель должен быть участником проекта" }

    return TaskPerfomerSetEvent(
        taskId = taskId,
        performer = performerId
    )
}

fun ProjectAggregateState.taskPerformerDeletedEvent(
    taskId: UUID,
    performerId: UUID,
    userId: UUID
): TaskPerfomerDeletedEvent {
    require(tasks.contains(taskId)) { "Задача должна принадлежать проекту" }
    require(tasks[taskId]?.performers?.contains(performerId) == true) { "Пользователь должен быть исполнителем задачи" }
    require(participants.contains(userId)) { "Пользователь должен быть участником проекта" }

    return TaskPerfomerDeletedEvent(
        taskId = taskId,
        performer = performerId
    )
}

fun ProjectAggregateState.taskDeletedEvent(taskId: UUID, userId: UUID): TaskDeletedEvent {
    require(tasks.contains(taskId)) { "Задача должна принадлежать проекту" }
    require(participants.contains(userId)) { "Пользователь должен быть участником проекта" }

    return TaskDeletedEvent(
        taskId = taskId
    )
}

fun ProjectAggregateState.addTask(name: String, userId: UUID): TaskCreatedEvent {
    require(participants.contains(userId)) { "Пользователь должен быть участником проекта" }

    return TaskCreatedEvent(
        taskId = UUID.nameUUIDFromBytes(name.toByteArray()),
        taskName = name
    )
}