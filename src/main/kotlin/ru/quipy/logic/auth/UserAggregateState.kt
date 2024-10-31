package ru.quipy.logic.auth

import ru.quipy.api.auth.UserAggregate
import ru.quipy.api.auth.UserCreatedEvent
import ru.quipy.api.auth.UsersAggregateCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class UserAggregateState : AggregateState<String, UserAggregate> {
    companion object {
        val usersAggregateId = "UsersAggregateId"
    }

    private lateinit var aggregateId: String
    var users = mutableMapOf<UUID, UserEntity>()

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = aggregateId

    fun create(id: String): UsersAggregateCreatedEvent {
        return UsersAggregateCreatedEvent()
    }

    fun createUser(id: UUID, nickName: String, name: String, password: String): UserCreatedEvent {
        if (users.values.any { x -> x.nickname == nickName }) {
            throw Exception("User with this nickname already exists")
        }

        return UserCreatedEvent(
            id,
            nickName,
            name,
            password,
        )
    }

    @StateTransitionFunc
    fun usersAggregateCreatedApply(event: UsersAggregateCreatedEvent) {
        aggregateId = usersAggregateId
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        users[event.userId] = UserEntity(
            event.userId,
            event.nickName,
            event.personName,
            event.password.toByteArray()
        )
        updatedAt = createdAt
    }
}

data class UserEntity(
    var id: UUID,
    var nickname: String,
    var name: String,
    var passwordHash: ByteArray
)