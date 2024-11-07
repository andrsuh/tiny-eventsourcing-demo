package ru.quipy.logic

import ru.quipy.api.ProjectAddedEvent
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.UserCreatedEvent
import java.util.*

fun UserAggregateState.create(userId: UUID, login: String, password: String, username: String) : UserCreatedEvent {
    return UserCreatedEvent(
        userId = userId,
        login = login,
        password = password,
        username = username
    )
}

fun UserAggregateState.addProject(projectId: UUID): ProjectAddedEvent {
    return ProjectAddedEvent(userId = this.getId(), projectId = projectId)
}