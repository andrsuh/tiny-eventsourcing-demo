package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.createUser
import java.util.*

@RestController
@RequestMapping("/users")
class UserController (
        val userService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {
    @PostMapping("")
    fun createUser(@RequestParam username: String, @RequestParam password: String) : UserCreatedEvent {
        return userService.create {
            it.createUser(UUID.randomUUID(), username, password)
        }
    }

}