package ru.quipy.logic

import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.UserName
import java.util.UUID


fun UserAggregateState.create(id: UUID, username: UserName, login: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
        userId = id,
        username = username,
        login = login,
        password = password
    )
}