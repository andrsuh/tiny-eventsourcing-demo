package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val USER_CREATED_EVENT = "USER_CREATED_EVENT"
const val USER_NAME_CHANGED_EVENT = "USER_NAME_CHANGED_EVENT"

@DomainEvent(name = USER_CREATED_EVENT)
class UserCreatedEvent(
    val userId: UUID,
    val password: String,
    val nickname: String,
    val realName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<UserAggregate>(
    name = USER_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(USER_NAME_CHANGED_EVENT)
class UserNameChangedEvent(
    val userId: UUID,
    val nickname: String,
    createdAt: Long = System.currentTimeMillis()
) : Event<UserAggregate>(
    name = USER_NAME_CHANGED_EVENT,
    createdAt = createdAt
)