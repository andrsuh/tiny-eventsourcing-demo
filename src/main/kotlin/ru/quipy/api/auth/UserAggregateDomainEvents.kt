package ru.quipy.api.auth

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val USERS_AGGREGATE_CREATED_EVENT = "USERS_AGGREGATE_CREATED_EVENT"
const val USER_CREATED_EVENT = "USER_CREATED_EVENT"

@DomainEvent(name = USERS_AGGREGATE_CREATED_EVENT)
class UsersAggregateCreatedEvent(
    createdAt: Long = System.currentTimeMillis(),
) : Event<UserAggregate>(
    name = USERS_AGGREGATE_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = USER_CREATED_EVENT)
class UserCreatedEvent(
    val userId: UUID,
    val nickName: String,
    val personName: String,
    val password: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<UserAggregate>(
    name = USER_CREATED_EVENT,
    createdAt = createdAt,
)