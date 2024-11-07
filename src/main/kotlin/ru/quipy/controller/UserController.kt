package ru.quipy.controller

import liquibase.pro.packaged.it
import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    @PostMapping("/register")
    fun createUser(@RequestParam login: String, @RequestParam password: String, @RequestParam username: String) : UserCreatedEvent {
        return userEsService.create { it.create(UUID.randomUUID(), login, password, username) }
    }

    @PostMapping("/{userId}/addProject")
    fun addProject(@PathVariable userId: UUID, @RequestParam projectId: UUID) : ProjectAddedEvent {
        return userEsService.update(userId) { it.addProject(projectId) }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID) : UserAggregateState? {
        return userEsService.getState(userId)
    }
}