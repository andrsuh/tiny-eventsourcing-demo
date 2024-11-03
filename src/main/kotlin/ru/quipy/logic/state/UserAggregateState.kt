package ru.quipy.logic.state

import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    private lateinit var nickname: String
    private lateinit var password: String
    private lateinit var uname: String

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = userId

    fun getNickname() = nickname
    fun getPassword() = password
    fun getName() = uname

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        userId = event.userId
        uname = event.uname
        nickname = event.nickname
        password = event.password
        updatedAt = createdAt
    }
}