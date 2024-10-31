package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
        val usersEsService: EventSourcingService<UUID, UserAggregate, UserAggregateStatec>
) {

    @PostMapping("")
    fun createUser(@RequestBody body: CreateUserRequest): UserCreatedEvent {
        return usersEsService.create { it.createUser(UUID.randomUUID(), body.uname, body.nickname, body.password) }
    }
}

data class CreateUserRequest (
        val uname: String,
        val nickname: String,
        val password: String
)