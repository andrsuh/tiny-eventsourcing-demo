package ru.quipy.logic.status

import ru.quipy.api.status.*
import java.util.*

fun StatusAggregateState.createStatus(name: String, color: String): StatusCreatedEvent {
    val statusId = UUID.randomUUID()
    return StatusCreatedEvent(
            statusId = statusId,
            title = name,
            color = color
    )
}

fun StatusAggregateState.updateTitle(statusId: UUID, newTitle: String): StatusTitleUpdatedEvent {
    return StatusTitleUpdatedEvent(
            statusId = statusId,
            newTitle = newTitle
    )
}

fun StatusAggregateState.updateColor(statusId: UUID, newColor: String): StatusColorUpdatedEvent {
    return StatusColorUpdatedEvent(
            statusId = statusId,
            newColor = newColor
    )
}
