package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class UserAggregateState : AggregateState<UUID, UserAggregate>
{
    private lateinit var userId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var login: String
    lateinit var password: String
    lateinit var username : String
    var projects = ArrayList<UUID>()

    override fun getId() = userId

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        userId = event.userId
        login = event.login
        password = event.password
        username = event.username
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun projectAddedApply(event: ProjectAddedEvent) {
        projects.add(event.projectId)
        updatedAt = createdAt
    }
}