package ru.quipy.logic

import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.UserUpdateEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class User : AggregateState<UUID, UserAggregate> {

    private lateinit var userId: UUID
    private lateinit var login: String
    private lateinit var password: String
    private lateinit var displayName: String

    override fun getId(): UUID = userId

    fun createUser(login: String, password: String, displayName: String): UserCreatedEvent {
        return UserCreatedEvent(
            userId = UUID.randomUUID(),
            login = login,
            password = password,
            displayName = displayName
        )
    }

    @StateTransitionFunc
    fun createUser(event: UserCreatedEvent) {
        userId = event.userId
        login = event.login
        password = event.password
        displayName = event.displayName
    }

    fun updateDisplayName(userId: UUID, newDisplayName: String): UserUpdateEvent {
        if (displayName == newDisplayName) {
            throw IllegalArgumentException("New display name is the same as the old name")
        }

        return UserUpdateEvent(
            userId = userId,
            newDisplayName = newDisplayName
        )
    }

    @StateTransitionFunc
    fun updateDisplayName(event: UserUpdateEvent) {
        userId = event.userId
        displayName = event.newDisplayName
    }
}
