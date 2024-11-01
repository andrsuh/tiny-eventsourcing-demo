package ru.quipy.logic

import ru.quipy.api.UserRegisteredEvent
import java.util.*


fun UserAggregateState.register(
    userId: UUID,
    username: String,
    fullName: String,
    password: String): UserRegisteredEvent {
        return UserRegisteredEvent(
            userId = userId,
            username = username,
            fullName = fullName,
            password = password,
        )
}
