package ru.quipy.services

import ru.quipy.projections.Project
import ru.quipy.projections.Task
import ru.quipy.projections.UserWithName
import java.util.UUID

interface ProjectProjectionsService {
    fun getAllByUserId(userId: UUID) : List<Project>
    fun getAllUsersByProjectId(projectId: UUID) : List<UserWithName>
}

interface TaskProjectionsService {
    fun getById(taskId: UUID) : Task
    fun getAllByProjectId(projectId: UUID) : List<Task>
    fun getByName(name: String) : Task
}