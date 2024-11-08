package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.user.UserAggregate
import ru.quipy.api.user.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.user.UserAggregateState
import ru.quipy.logic.user.create
import java.util.*

@RestController
@RequestMapping("/users")
class UserController (
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
){
    @PostMapping("/create")
    fun createUser(
        @RequestParam nickname: String,
        @RequestParam name: String,
        @RequestParam password: String
    ) : UserCreatedEvent {
        return userEsService.create {
            it.create(
                id = UUID.randomUUID(),
                nickname = nickname,
                name = name,
                password = password,
            )
        }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID) : UserAggregateState? {
        return userEsService.getState(userId)
    }
}