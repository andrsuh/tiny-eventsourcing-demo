package ru.quipy.logic

import ru.quipy.api.UserRegisteredEvent
import java.util.*

fun UserAggregateState.register(id: UUID, username: String, password: String): UserRegisteredEvent {
    return UserRegisteredEvent(
            userId = id,
            username = username,
            password = password,
    )
}