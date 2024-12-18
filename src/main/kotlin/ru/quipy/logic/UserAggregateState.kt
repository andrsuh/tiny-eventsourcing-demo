package ru.quipy.logic

import ru.quipy.api.UserAggregate
import ru.quipy.api.UserRegisteredEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var username: String
    lateinit var password: String
    lateinit var fio: String
    override fun getId() = userId

    @StateTransitionFunc
    fun userRegisteredApply(event: UserRegisteredEvent){
        userId = event.userId
        username = event.username
        password = event.password
        updatedAt = createdAt
    }
}