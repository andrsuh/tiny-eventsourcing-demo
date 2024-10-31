package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.createUser
import java.util.*


@RestController
@RequestMapping("/users")
class UserController (val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>){

    @PostMapping("/newUser")
    fun createProject(@RequestParam nickname: String, @RequestParam userName: String,  @RequestParam password: String) : UserCreatedEvent {
        return userEsService.create { it.createUser(nickname, userName, password)}
    }
}
