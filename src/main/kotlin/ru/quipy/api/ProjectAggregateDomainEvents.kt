package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val PROJECT_UPDATED_EVENT = "PROJECT_UPDATED_EVENT"

const val PROJECT_MEMBER_CREATED_EVENT = "PROJECT_MEMBER_CREATED_EVENT"
const val PROJECT_MEMBER_REMOVED_EVENT = "PROJECT_MEMBER_REMOVED_EVENT"

const val TAG_CREATED_EVENT = "CUSTOM_TAG_CREATED_EVENT"
const val TAG_ADDED_TO_TASK_EVENT = "TAG_ADDED_TO_TASK_EVENT"
const val TAG_DELETED_EVENT = "CUSTOM_TAG_DELETED_EVENT"

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_ASSIGNEE_ADDED_EVENT = "TASK_ASSIGNEE_ADDED_EVENT"
const val TASK_UPDATED_EVENT = "TASK_UPDATED_EVENT"
const val TASK_DELETED_EVENT = "TASK_DELETED_EVENT"

// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val title: String,
    val creatorId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = PROJECT_UPDATED_EVENT)
class ProjectUpdatedEvent(
    val projectId: UUID,
    val title: String?,
    val description: String?,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate> (
    name = PROJECT_UPDATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = PROJECT_MEMBER_CREATED_EVENT)
class ProjectMemberCreatedEvent(
    val projectId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_MEMBER_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = PROJECT_MEMBER_REMOVED_EVENT)
class ProjectMemberRemovedEvent(
    val projectId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_MEMBER_REMOVED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TAG_CREATED_EVENT)
class TagCreatedEvent(
    val tagId: UUID,
    val projectId: UUID,
    val tagName: String,
    val tagColor: String,
    val creatorId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TAG_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TAG_DELETED_EVENT)
class TagDeletedEvent(
    val tagId: UUID,
    val projectID: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TAG_DELETED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt
)


@DomainEvent(name = TASK_UPDATED_EVENT)
class TaskUpdatedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskName: String?,
    val taskDescription: String?,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_UPDATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_ASSIGNEE_ADDED_EVENT)
class TaskAssignedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_ASSIGNEE_ADDED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_DELETED_EVENT)
class TaskDeletedEvent(
    val projectId: UUID,
    val taskId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_DELETED_EVENT,
    createdAt = createdAt,
)


@DomainEvent(name = TAG_ADDED_TO_TASK_EVENT)
class TagAddedToTaskEvent(
    val projectId: UUID,    
    val taskId: UUID,
    val tagId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TAG_ADDED_TO_TASK_EVENT,
    createdAt = createdAt
)