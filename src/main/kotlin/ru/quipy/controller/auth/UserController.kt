package ru.quipy.controller.auth

import org.springframework.web.bind.annotation.*
import ru.quipy.api.auth.UserAggregate
import ru.quipy.api.auth.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.auth.UserAggregateState
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
) {
    @PostMapping("/create")
    fun createUser(
        @RequestBody dto: CreateUserDto,
    ): UserCreatedEvent {
        return userEsService.create {
            it.create(UUID.randomUUID(), dto.nickName, dto.name, dto.password)
        }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID) : UserAggregateState? {
        return userEsService.getState(userId)
    }
}

data class CreateUserDto(
    val nickName: String,
    val name: String,
    val password: String,
)
