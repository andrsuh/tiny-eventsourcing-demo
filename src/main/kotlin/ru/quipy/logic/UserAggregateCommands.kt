package ru.quipy.logic

import ru.quipy.api.UserCreatedEvent
import java.util.*

fun UserAggregateStatec.createUser(id: UUID, userName: String, nickname: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
            userId = id,
            uname = uname,
            nickname = nickname,
            password = password
    )
}