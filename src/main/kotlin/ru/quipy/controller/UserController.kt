package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.createUser
import java.util.UUID

@RestController
@RequestMapping("/user")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    // @GetMapping("/{userId}")
    // fun getUser(@PathVariable userId: UUID) : UserAggregateState? {
    //     return userEsService.getState(userId)
    // }

    @PostMapping("")
    fun createUser(@RequestParam username: String, @RequestParam password: String) : UserCreatedEvent {
        return userEsService.create {
            it.createUser(UUID.randomUUID(), username, password)
        }
    }

}