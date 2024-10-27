package ru.quipy.logic.auth

import liquibase.repackaged.org.apache.commons.lang3.mutable.Mutable
import ru.quipy.api.auth.UserAggregate
import ru.quipy.api.auth.UserCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private val users: MutableMap<UUID, UserEntity> = mutableMapOf()

    private lateinit var aggregateId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = aggregateId

    fun create(id: UUID, nickName: String, name: String, password: String): UserCreatedEvent {
        if (users.values.any { x -> x.nickname == nickName })
            throw Exception("User with this nickname already exists")

        return UserCreatedEvent(
            id,
            nickName,
            name,
            password,
        )
    }

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        users[event.userId] = UserEntity(
            userId = event.userId,
            nickname = event.nickName,
            name = event.personName,
            passwordHash = event.password.toByteArray()
        )
    }
}

data class UserEntity(
    val userId: UUID,
    val nickname: String,
    val name: String,
    val passwordHash: ByteArray
);