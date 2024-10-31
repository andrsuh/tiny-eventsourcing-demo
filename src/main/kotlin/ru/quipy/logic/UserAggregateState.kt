package ru.quipy.logic

import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.UserNameChangedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    lateinit var nickName: String
    lateinit var realName: String
    lateinit var password: String

    override fun getId(): UUID = userId

    @StateTransitionFunc
    fun changeName(event: UserNameChangedEvent) {
        nickName = event.nickname
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun createUser(event: UserCreatedEvent) {
        userId = event.userId
        nickName = event.nickname
        realName = event.realName
        password = event.password
        updatedAt = createdAt
    }
}