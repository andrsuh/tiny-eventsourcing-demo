package ru.quipy.logic

import ru.quipy.api.UserCreatedEvent
import java.util.*

fun UserAggregateState.createUser(nickname: String, userName: String, password: String) : UserCreatedEvent{
    return UserCreatedEvent(
        userId = UUID.randomUUID(),
        nickname = nickname,
        userName = userName,
        password = password
    )
}