package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserRegisteredEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.register
import ru.quipy.projections.TaskProjection
import ru.quipy.service.ProjectionService
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
    val projectionService: ProjectionService,
) {
    @PostMapping("/{username}")
    fun createProject(@PathVariable username: String, @RequestParam password: String): UserRegisteredEvent {
        return userEsService.create { it.register(UUID.randomUUID(), username, password) }
    }

    @GetMapping("/relatedTasks/{executorId}")
    fun findAllTasks(@PathVariable executorId: UUID): List<TaskProjection> {
        return projectionService.findAllTasksByExecutorIdContains(executorId)
    }
}