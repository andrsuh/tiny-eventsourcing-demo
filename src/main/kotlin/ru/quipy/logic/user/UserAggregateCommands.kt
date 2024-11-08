package ru.quipy.logic.user

import ru.quipy.api.user.UserCreatedEvent
import java.util.*

fun UserAggregateState.create(id: UUID, nickname: String, name: String, password: String) : UserCreatedEvent {
    return UserCreatedEvent(
        userId = id,
        nickname = nickname,
        firstName = name,
        password = password,
    )
}