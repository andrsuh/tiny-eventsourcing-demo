package ru.quipy.logic.user

import ru.quipy.api.user.*
import java.util.UUID

fun UserAggregateState.createUser(userId: UUID,
                                  nickName: String,
                                  password: String,
                                  displayName: String): UserCreatedEvent{
    return  UserCreatedEvent(
        userId = userId,
        nickName = nickName,
        password = password,
        displayName = displayName
    )
}

fun UserAggregateState.updateUserProfile(userId: UUID, newNickName: String) : UserUpdateEvent {
    if (this.nickName == newNickName ) {
        throw IllegalArgumentException("New nick name is the same as old name")
    }

    return UserUpdateEvent(
        userId = userId,
        nickName = newNickName
    )
}