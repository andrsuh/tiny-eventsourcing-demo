package ru.quipy.logic.status

import ru.quipy.api.status.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class StatusAggregateState : AggregateState<UUID, StatusAggregate> {
    lateinit var statusId: UUID
    lateinit var title: String
    lateinit var color: String

    override fun getId() = statusId

    @StateTransitionFunc
    fun onStatusCreated(event: StatusCreatedEvent) {
        statusId = event.statusId
        title = event.title
        color = event.color
    }

    @StateTransitionFunc
    fun onStatusTitleUpdated(event: StatusTitleUpdatedEvent) {
        title = event.newTitle
    }

    @StateTransitionFunc
    fun onStatusColorUpdated(event: StatusColorUpdatedEvent) {
        color = event.newColor
    }
}
