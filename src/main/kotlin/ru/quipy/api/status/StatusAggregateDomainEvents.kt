package ru.quipy.api.status

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val STATUS_CREATED_EVENT = "STATUS_CREATED_EVENT"
const val STATUS_TITLE_UPDATED_EVENT = "STATUS_TITLE_UPDATED_EVENT"
const val STATUS_COLOR_UPDATED_EVENT = "STATUS_COLOR_UPDATED_EVENT"

@DomainEvent(name = STATUS_CREATED_EVENT)
class StatusCreatedEvent(
        val statusId: UUID,
        val title: String,
        val color: String,
        createdAt: Long = System.currentTimeMillis()
) : Event<StatusAggregate>(
        name = STATUS_CREATED_EVENT,
        createdAt = createdAt
) {}

@DomainEvent(name = STATUS_TITLE_UPDATED_EVENT)
class StatusTitleUpdatedEvent(
        val statusId: UUID,
        val newTitle: String,
        createdAt: Long = System.currentTimeMillis()
) : Event<StatusAggregate>(
        name = STATUS_TITLE_UPDATED_EVENT,
        createdAt = createdAt
)

@DomainEvent(name = STATUS_COLOR_UPDATED_EVENT)
class StatusColorUpdatedEvent(
        val statusId: UUID,
        val newColor: String,
        createdAt: Long = System.currentTimeMillis()
) : Event<StatusAggregate>(
        name = STATUS_COLOR_UPDATED_EVENT,
        createdAt = createdAt
)