package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val USER_CREATED_EVENT = "USER_CREATED_EVENT"
// API

@DomainEvent(name = USER_CREATED_EVENT)
class UserCreatedEvent(
        val userId: UUID,
        val nickname: String,
        val uname: String,
        val password: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<UserAggregate>(
        name = USER_CREATED_EVENT,
        createdAt = createdAt
)