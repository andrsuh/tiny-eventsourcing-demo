package ru.quipy.controller

import org.springframework.web.bind.annotation.* // ktlint-disable no-wildcard-imports
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
        @RequestBody dto: createUserDto,
    ): UserCreatedEvent {
        return userEsService.create {
            it.create(UUID.randomUUID(), dto.nickName, dto.name, dto.password)
        }
    }
}

data class createUserDto(
    val nickName: String,
    val name: String,
    val password: String,
)
