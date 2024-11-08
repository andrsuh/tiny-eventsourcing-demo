package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.user.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.user.*
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
        val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {
    @PostMapping("/create")
    fun addUser(
            @RequestParam username: String,
            @RequestParam login: String,
            @RequestParam password: String
    ): UserCreatedEvent {
        // создаем пользователя и возвращаем событие о создании
        return userEsService.create {
            it.createUser (UUID.randomUUID(), username, login, password)
        }
    }
}
