package ru.quipy.logic.auth

import ru.quipy.api.auth.UserAggregate
import ru.quipy.api.auth.UserCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    lateinit var nickname: String
    lateinit var name: String
    lateinit var passwordHash: ByteArray

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = userId

    fun create(id: UUID, nickName: String, name: String, password: String): UserCreatedEvent {
        return UserCreatedEvent(
            id,
            nickName,
            name,
            password,
        )
    }

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        userId = event.userId
        nickname = event.nickName
        name = event.personName
        passwordHash = event.password.toByteArray()
        updatedAt = createdAt
    }
}
