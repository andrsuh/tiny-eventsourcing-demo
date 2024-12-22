package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.projections.Project
import ru.quipy.projections.User
import ru.quipy.projections.UserAndProjectService
import java.util.*

@RestController
@RequestMapping("/usersAndProjects")
class UserAndProjectsProjectionController(
        val userAndProjectService: UserAndProjectService
) {
    @GetMapping("/project/{userId}")
    fun getProjectsByUserId(@PathVariable userId: UUID): List<Project> {
        return userAndProjectService.getProjectsByUserId(userId)
    }

    @GetMapping("/user/{projectId}")
    fun getUsersByProjectId(@PathVariable projectId: UUID): List<User> {
        return userAndProjectService.getUsersByProjectId(projectId)
    }
}