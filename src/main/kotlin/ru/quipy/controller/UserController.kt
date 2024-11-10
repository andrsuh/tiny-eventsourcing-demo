package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.create
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {
    @PostMapping("/{nickname}")
    fun createProject(
        @PathVariable nickname: String,
        @RequestBody request: CreateUserRequest
    ): UserCreatedEvent {
        return userEsService.create { it.create(request.name, nickname, request.secret) }
    }

}