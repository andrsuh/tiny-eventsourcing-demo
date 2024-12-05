package ru.quipy.services

import org.springframework.stereotype.Service
import ru.quipy.projections.Project
import ru.quipy.projections.ProjectsRepository
import ru.quipy.projections.Task
import ru.quipy.projections.TasksRepository
import ru.quipy.projections.UserWithName
import ru.quipy.projections.UsersNamesRepository
import java.util.UUID

@Service
class ProjectionsService (
    private val projectsRepository: ProjectsRepository,
    private val userRepository: UsersNamesRepository,
    private val tasksRepository: TasksRepository
) : ProjectProjectionsService, TaskProjectionsService{
    override fun getAllByUserId(userId: UUID): List<Project> {
        return projectsRepository.findAllByParticipantsContains(userId)
    }

    override fun getAllUsersByProjectId(projectId: UUID): List<UserWithName> {
        return userRepository.findAllByUserIdIn(projectsRepository.findByProjectId(projectId).participants)
    }

    override fun getById(taskId: UUID): Task {
        return tasksRepository.findByTaskId(taskId)
    }

    override fun getAllByProjectId(projectId: UUID): List<Task> {
        return tasksRepository.findAllByProjectId(projectId)
    }

    override fun getByName(name: String): Task {
        return tasksRepository.findByName(name)
    }
}