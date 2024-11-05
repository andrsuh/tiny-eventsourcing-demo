package ru.quipy.logic

import ru.quipy.api.UserCreatedEvent
import java.util.*

fun UserAggregateState.createUser(id: UUID, username: String, realname: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
        userId = id,
        username = username,
        realname = realname,
        password = password
    )
}
