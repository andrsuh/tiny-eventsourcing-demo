package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var username: String
    lateinit var name: String

    override fun getId() = userId

    @StateTransitionFunc
    fun userRegisteredApply(event: UserRegisteredEvent) {
        userId = event.userId
        username = event.username
        name = event.name
        updatedAt = createdAt
    }
}