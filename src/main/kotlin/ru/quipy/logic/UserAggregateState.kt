package ru.quipy.logic

import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.UserName
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.UUID

class UserAggregateState(): AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    private lateinit var username: UserName
    private lateinit var login: String
    private lateinit var password: String


    override fun getId() = userId

    @StateTransitionFunc
    fun userCreateApply(event: UserCreatedEvent) {
        userId = event.id
        username = event.username
        login = event.login
        password = event.password
        createdAt = event.createdAt
        updatedAt = createdAt
    }
}