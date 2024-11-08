package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import ru.quipy.logic.command.createUser
import ru.quipy.logic.state.UserAggregateState
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(
        val usersEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    @PostMapping("")
    fun createUser(@RequestParam nickname: String, @RequestParam password: String, @RequestParam uname: String): UserCreatedEvent {
        return usersEsService.create { it.createUser(UUID.randomUUID(), nickname, password, uname) }
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: UUID) : UserAggregateState? {
        return usersEsService.getState(id)
    }
}
//u2
// f44ec693-4074-41d5-a6ac-51e2c884c96a
//u3
//2c2e779f-621b-4526-9a51-cb55a18249ac