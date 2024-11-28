package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import ru.quipy.projections.entity.ProjectProjection
import ru.quipy.projections.repository.TaskInfoRepository
import ru.quipy.projections.entity.UserProjection
import ru.quipy.projections.repository.UserProjectionRepository
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
        val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
        val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
        private val userProjectionRepository: UserProjectionRepository,
        private val projectProjectionRepository: TaskInfoRepository
) {

    @PostMapping("/register")
    fun createUser(@RequestParam login: String, @RequestParam password: String, @RequestParam username: String) : UserCreatedEvent {
        return userEsService.create { it.create(UUID.randomUUID(), login, password, username) }
    }

    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: UUID): UserProjection? {
        return userProjectionRepository.findById(userId).orElse(null)
    }

    @GetMapping("/{userId}/projects")
    fun getUserProjects(@PathVariable userId: UUID): Iterable<ProjectProjection> {
        val user = userProjectionRepository.findById(userId).orElse(null)
        return if (user != null) {
            projectProjectionRepository.findAllById(user.projects)
        } else {
            emptyList()
        }
    }

    @PostMapping("/{userId}/addProject")
    fun addProject(@PathVariable userId: UUID, @RequestParam projectId: UUID) : ProjectAddedEvent {
        val project = projectEsService.getState(projectId)
        return userEsService.update(userId) { it.addProject(project) }
    }
}