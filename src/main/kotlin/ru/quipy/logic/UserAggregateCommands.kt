package ru.quipy.logic

import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.UserNameChangedEvent
import java.util.*

fun UserAggregateState.createUser(realName: String, nickname: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(userId = UUID.randomUUID(), realName = realName, nickname = nickname, password = password)
}

fun UserAggregateState.changeUsername(userId: UUID, nickname: String): UserNameChangedEvent {
    if (nickname == this.nickName){
        error("This nickname is already used")
    }
    return UserNameChangedEvent(userId = userId, nickname = nickname)
}