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

fun UserAggregateState.addProject(project: ProjectAggregateState?): ProjectAddedEvent {
    if (project == null) throw IllegalArgumentException("Project doesn't exists")
    if (this.projects.contains(project.getId())) throw IllegalArgumentException("Project already added")
    return ProjectAddedEvent(userId = this.getId(), projectId = project.getId())
}