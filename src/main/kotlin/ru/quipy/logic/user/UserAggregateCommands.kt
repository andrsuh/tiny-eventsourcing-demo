package ru.quipy.logic.user

import ru.quipy.api.user.UserCreatedEvent
import java.util.*

fun UserAggregateState.createUser(id: UUID, username: String, login: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
            userId = id,
            username = username,
            login = login,
            password = password
    )
}
