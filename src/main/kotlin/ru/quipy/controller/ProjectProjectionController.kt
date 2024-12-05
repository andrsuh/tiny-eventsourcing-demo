package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.projections.Project
import ru.quipy.projections.Task
import ru.quipy.projections.UserWithName
import ru.quipy.services.ProjectProjectionsService
import ru.quipy.services.TaskProjectionsService
import java.util.UUID

@RestController
@RequestMapping("/projects")
class ProjectProjectionController(
    private val projectsService: ProjectProjectionsService,
    private val taskService: TaskProjectionsService
) {
    @GetMapping("/user/{userId}")
    fun getProjectsByUserId(@PathVariable userId: UUID): List<Project> {
        return projectsService.getAllByUserId(userId)
    }

    @GetMapping("/{projectId}/users")
    fun getUsersByProjectId(@PathVariable projectId: UUID): List<UserWithName> {
        return projectsService.getAllUsersByProjectId(projectId)
    }

    @GetMapping("/{projectId}/tasks")
    fun getTasksByProjectId(@PathVariable projectId: UUID): List<Task> {
        return taskService.getAllByProjectId(projectId)
    }
}