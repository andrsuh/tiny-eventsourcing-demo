package ru.quipy.aggregate.user

import ru.quipy.api.user.UserChangedNameEvent
import ru.quipy.api.user.UserCreatedEvent
import java.util.*

fun UserAggregateState.create(id: UUID, name: String, nickname: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
        userId = id,
        firstname = name,
        nickname = nickname,
        password = password
    )
}

fun UserAggregateState.changeName(newName: String): UserChangedNameEvent {
    return UserChangedNameEvent(
        userId = getId(),
        newName
    )
}