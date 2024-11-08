package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.UserUpdateEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.User
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(val userEsService: EventSourcingService<UUID, UserAggregate, User>) {

    @PostMapping
    fun createUser(
        @RequestParam login: String,
        @RequestParam password: String,
        @RequestParam displayName: String
    ): UserCreatedEvent {
        return userEsService.create {
            it.createUser(login, password, displayName)
        }
    }

    @PostMapping("/{userId}")
    fun updateDisplayName(
        @PathVariable userId: UUID,
        @RequestParam newDisplayName: String
    ): UserUpdateEvent {
        return userEsService.update(userId) {
            it.updateDisplayName(userId, newDisplayName)
        }
    }

    @GetMapping("/{userId}")
    fun getUser(
        @PathVariable userId: UUID
    ): User? {
        return userEsService.getState(userId)
    }
}
