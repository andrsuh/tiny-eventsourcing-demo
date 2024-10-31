package ru.quipy.controller.auth

import org.springframework.web.bind.annotation.*
import ru.quipy.api.auth.UserAggregate
import ru.quipy.api.auth.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.auth.UserAggregateState
import ru.quipy.logic.auth.UserAggregateState.Companion.usersAggregateId
import ru.quipy.logic.auth.UserEntity
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<String, UserAggregate, UserAggregateState>,
) {
    @PostMapping("/create")
    fun createUser(
        @RequestBody dto: CreateUserDto,
    ): UserCreatedEvent {
        if (userEsService.getState(usersAggregateId) == null) {
            userEsService.create { it.create(usersAggregateId) }
        }

        return userEsService.update(usersAggregateId) {
            it.createUser(UUID.randomUUID(), dto.nickName, dto.name, dto.password)
        }
    }

    @GetMapping
    fun getAllUsers() : UserAggregateState? {
        return userEsService.getState(usersAggregateId)
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID) : UserEntity? {
        return userEsService.getState(usersAggregateId)?.users?.get(userId)
    }
}

data class CreateUserDto(
    val nickName: String,
    val name: String,
    val password: String
)
