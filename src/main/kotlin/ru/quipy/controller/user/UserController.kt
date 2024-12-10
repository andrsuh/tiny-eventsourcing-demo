package ru.quipy.controller.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.core.EventSourcingService
import ru.quipy.aggregate.user.UserAggregate
import ru.quipy.aggregate.user.UserAggregateState
import ru.quipy.aggregate.user.changeName
import ru.quipy.aggregate.user.create
import ru.quipy.api.user.UserChangedNameEvent
import ru.quipy.api.user.UserCreatedEvent
import ru.quipy.projections.UserViewDomain
import ru.quipy.projections.UserViewService
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
    val userViewService: UserViewService,
) {

    @PostMapping("/")
    fun addUser(@RequestParam name: String,
                @RequestParam nickname: String,
                @RequestParam password: String) : UserCreatedEvent {
        return userEsService.create { it.create(UUID.randomUUID(), name, nickname, password) }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID) : UserAggregateState? {
        return userEsService.getState(userId)
    }

    @GetMapping("/{nickname}")
    fun checkIfNicknameExists(@PathVariable nickname: String) : Boolean {
        return userViewService.findByNickName(nickname) != null
    }

    @GetMapping("/findByNicknameSubstr/{nicknameSubstr}")
    fun findByNicknameSubstr(@PathVariable nicknameSubstr: String) : List<UserViewDomain.User> {
        return userViewService.findByNameSubs(nicknameSubstr)
    }

    @PostMapping("/{userId}")
    fun changeUserName(@PathVariable userId: UUID, @RequestParam newName: String) : UserChangedNameEvent {
        return userEsService.update(userId) {
            it.changeName(newName)
        }
    }
}