package ru.quipy.logic

import ru.quipy.api.NameUserChangedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    lateinit var name: String
    lateinit var username: String
    lateinit var password: String
    override fun getId() = userId

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        userId = event.userId
        username = event.username
        password = event.password
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun nameUserChangedApply(event: NameUserChangedEvent) {
        name = event.nameUser
        updatedAt = createdAt
    }

}
