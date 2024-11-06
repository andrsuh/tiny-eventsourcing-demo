package ru.quipy.api.user

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val USER_CREATED_EVENT = "USER_CREATED_EVENT"
const val USER_UPDATE_EVENT= "USER_UPDATE_EVENT"


@DomainEvent(name = USER_CREATED_EVENT)
class UserCreatedEvent(
    val userId: UUID,
    val nickName: String,
    val password: String,
    val displayName: String
) : Event<UserAggregate>(
    name = USER_CREATED_EVENT
)

@DomainEvent(name = USER_UPDATE_EVENT)
class UserUpdateEvent(
    val userId: UUID,
    val nickName: String,
) : Event<UserAggregate>(
    name = USER_UPDATE_EVENT
)