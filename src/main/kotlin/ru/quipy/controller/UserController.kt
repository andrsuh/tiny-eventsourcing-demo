package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.user.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.user.*
import java.lang.IllegalArgumentException
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
) {
    @PostMapping("/{userName}")
    fun createUser(@PathVariable userName: String, @RequestParam password: String, @RequestParam displayName: String) : UserCreatedEvent {
        return userEsService.create { it.createUser(UUID.randomUUID(), userName, password, displayName) }
    }

    @PostMapping("/changeUserProfile/{userId}")
    fun updateUserProfile(@PathVariable userId: UUID, @RequestParam newNickName: String) : UserUpdateEvent{
        return userEsService.update(userId) {
            it.updateUserProfile(userId, newNickName)
        }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID) : UserAggregateState? {
        return userEsService.getState(userId)
    }


}