package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.createUser
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {
    @PostMapping("/")
    fun createProject(@RequestParam username: String, @RequestParam realname: String, @RequestParam password: String) : UserCreatedEvent {
        return userEsService.create { it.createUser(UUID.randomUUID(),username, realname, password) }
    }
}