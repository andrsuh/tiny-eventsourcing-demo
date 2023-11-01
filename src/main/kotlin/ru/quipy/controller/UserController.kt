package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.create
import java.util.*

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {
    @PostMapping("/create-user")
    fun createUser(
        @RequestParam surname: String,
        @RequestParam username: String,
        @RequestParam password: String,
    ): UserCreatedEvent {
        return userEsService.create { it.create(UUID.randomUUID(), surname, username, password) }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID): UserAggregateState? {
        return userEsService.getState(userId)
    }
}