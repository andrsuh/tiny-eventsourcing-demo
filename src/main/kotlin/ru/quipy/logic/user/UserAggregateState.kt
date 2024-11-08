package ru.quipy.logic.user

import ru.quipy.api.user.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var id: UUID
    lateinit var username: String
    lateinit var login: String
    private lateinit var password: String

    override fun getId() = id

    @StateTransitionFunc
    fun onUserCreated(event: UserCreatedEvent) {
        id = event.userId
        username = event.username
        login = event.login
        password = event.password
    }
}