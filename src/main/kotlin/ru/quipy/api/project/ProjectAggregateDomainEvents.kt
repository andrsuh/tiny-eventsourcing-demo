package ru.quipy.api.project

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val PROJECT_MEMBER_ADDED_Event = "PROJECT_MEMBER_ADDED_Event"
const val PROJECT_TITLE_CHANGED_EVENT = "PROJECT_TITLE_CHANGED_EVENT"
const val STATUS_CREATED_EVENT = "STATUS_CREATED_EVENT"
const val STATUS_CHANGED_EVENT = "STATUS_CHANGED_EVENT"
const val TASK_ADDED_IN_PROJECT_EVENT = "TASK_ADDED_IN_PROJECT_EVENT"

@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val title: String,
    val creatorId: UUID,
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED_EVENT
)

@DomainEvent(name = PROJECT_MEMBER_ADDED_Event)
class AddParticipantToProjectEvent(
    val projectId: UUID,
    val participantId: UUID,
) : Event<ProjectAggregate>(
    name = PROJECT_MEMBER_ADDED_Event
)

@DomainEvent(name = TASK_ADDED_IN_PROJECT_EVENT)
class TaskAddedInProjectEvent(
    val projectId: UUID,
    val taskId: UUID,
) : Event<ProjectAggregate>(
    name = TASK_ADDED_IN_PROJECT_EVENT
)

@DomainEvent(name = PROJECT_TITLE_CHANGED_EVENT)
class ProjectTitleChangedEvent(
    val projectId: UUID,
    val title: String,
) : Event<ProjectAggregate>(
    name = PROJECT_TITLE_CHANGED_EVENT
)
@DomainEvent(name = STATUS_CREATED_EVENT)
class StatusCreatedEvent(
    val projectId: UUID,
    val statusId: UUID,
    val statusName: String,
    val color: String,
    val taskQuantity: Int,
) : Event<ProjectAggregate>(
    name = STATUS_CREATED_EVENT
)

@DomainEvent(name = STATUS_CHANGED_EVENT)
class StatusChangedEvent(
    val projectId: UUID,
    val statusId: UUID,
    val newStatusName: String
) : Event<ProjectAggregate>(
    name = STATUS_CHANGED_EVENT
)

