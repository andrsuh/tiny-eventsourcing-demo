package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.UserNameChangedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.changeUsername
import ru.quipy.logic.createUser
import java.util.*


@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    @PostMapping("/{nickName}")
    fun createUser(
        @PathVariable nickName: String,
        @RequestParam realName: String,
        @RequestParam password: String
    ): UserCreatedEvent {
        return userEsService.create { it.createUser(nickname = nickName, realName = realName, password = password) }
    }

    @PutMapping("/{userId}")
    fun changeName(@PathVariable userId: String, @RequestParam nickname: String): UserNameChangedEvent {
        return userEsService.update(UUID.fromString(userId)) {
            it.changeUsername(UUID.fromString(userId), nickname)
        }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID): UserAggregateState? {
        return userEsService.getState(userId)
    }
}