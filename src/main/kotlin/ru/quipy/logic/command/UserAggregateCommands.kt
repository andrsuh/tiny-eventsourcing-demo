package ru.quipy.logic.command

import ru.quipy.api.UserCreatedEvent
import ru.quipy.logic.state.UserAggregateState
import java.util.*

fun UserAggregateState.createUser(id: UUID, userName: String, nickname: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
            userId = id,
            uname = uname,
            nickname = nickname,
            password = password
    )
}