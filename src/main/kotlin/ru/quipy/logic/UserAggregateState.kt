package ru.quipy.logic

import java.util.*
import ru.quipy.domain.AggregateState
import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    private  lateinit var password: String
    lateinit var userName: String
    lateinit var nickname: String

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = userId

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        userId = event.userId
        password = event.password
        userName = event.userName
        nickname = event.nickname
        updatedAt = createdAt
    }

}