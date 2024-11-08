package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val USER_CREATED = "USER_CREATED_EVENT"
const val USER_UPDATE = "USER_UPDATE_EVENT"

@DomainEvent(name = USER_CREATED)
class UserCreatedEvent(
    val userId: UUID,
    val login: String,
    val password: String,
    val displayName: String
) : Event<UserAggregate>(
    name = USER_CREATED
)

@DomainEvent(name = USER_UPDATE)
class UserUpdateEvent(
    val userId: UUID,
    val newDisplayName: String,
) : Event<UserAggregate>(
    name = USER_UPDATE
)
