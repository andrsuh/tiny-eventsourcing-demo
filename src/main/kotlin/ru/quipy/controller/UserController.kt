package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.Username
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.create
import ru.quipy.models.user.CreateUserRequest
import ru.quipy.models.user.UsernameDto
import ru.quipy.projections.AnnotationBasedUserEventsSubscriber
import ru.quipy.projections.view.UserInfoViewDomain
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
    val userService: AnnotationBasedUserEventsSubscriber
) {

    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest) : UserCreatedEvent {
        return userEsService.create { it.create( UUID.randomUUID(), request.username.map(), request.login, request.password) }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID): UserInfoViewDomain.UserDtoData {
        return userService.getUser(userId)
    }
    @GetMapping("/all_users")
    fun getAllUsers(): List<UserInfoViewDomain.UserDtoData>{
        return userService.getAllUsers()
    }
}

fun UsernameDto.map(): Username {
    return Username(
        firstName = this.firstName,
        lastName = this.lastName,
        middleName = this.middleName
    )
}
