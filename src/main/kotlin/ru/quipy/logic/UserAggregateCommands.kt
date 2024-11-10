package ru.quipy.logic

import ru.quipy.api.UserCreatedEvent
import java.util.*

fun UserAggregateState.create(name: String, nickname: String, password: String): UserCreatedEvent {
    val userId = UUID.nameUUIDFromBytes(nickname.toByteArray())

    return UserCreatedEvent(
        userId = userId,
        nickname = nickname,
        userName = name,
        password = password
    )
}