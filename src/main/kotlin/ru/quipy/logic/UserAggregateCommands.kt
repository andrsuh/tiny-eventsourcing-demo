package ru.quipy.logic

import ru.quipy.api.UserCreatedEvent
import java.util.UUID

fun UserAggregateState.createUser(userId: UUID, username: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
        userId = userId,
        username = username,
        password = password
    )
}