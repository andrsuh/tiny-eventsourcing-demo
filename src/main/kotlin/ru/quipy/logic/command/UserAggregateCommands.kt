package ru.quipy.logic.command

import ru.quipy.api.UserCreatedEvent
import ru.quipy.logic.state.UserAggregateState
import java.util.UUID

fun UserAggregateState.createUser(id: UUID, nickname: String, password: String, uname: String): UserCreatedEvent {
    return UserCreatedEvent(
            userId = id,
            nickname = nickname,
            password = password,
            uname = uname
    )
}