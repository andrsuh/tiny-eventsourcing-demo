package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectTitle: String
    lateinit var creatorId: String
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var projectTags = mutableMapOf<UUID, TagEntity>()

    var membersList = mutableMapOf<UUID, UserEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun tagCreatedApply(event: TagCreatedEvent) {
        projectTags[event.tagId] = TagEntity(event.tagId, event.tagName, event.color)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, null, null)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun userAddedToProjectApply(event: UserAddedToProjectEvent) {
        membersList[event.userId] = UserEntity(event.userId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun projectNameChangedApply(event: ProjectNameChangedEvent) {
        projectTitle = event.title
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun tagDeletedApply(event: TagDeletedEvent) {
        projectTags.remove(event.tagId)
        updatedAt = createdAt

    }

    @StateTransitionFunc
    fun taskNameChangedApply(event: TaskNameChangedEvent) {
        tasks[event.taskId]?.name= event.title
        updatedAt = createdAt
    }


}

data class TaskEntity(
        val id: UUID = UUID.randomUUID(),
        var name: String,
        var tag: UUID?,
        var userId: UUID?,
)

data class TagEntity(
        val id: UUID = UUID.randomUUID(),
        val name: String,
        val color: String
)

data class UserEntity(
        val id: UUID
)


/**
 * Demonstrates that the transition functions might be representer by "extension" functions, not only class members functions
 */
@StateTransitionFunc
fun ProjectAggregateState.tagAssignedApply(event: TagAssignedToTaskEvent) {
    tasks[event.taskId]?.tag = event.tagId
    updatedAt = createdAt
}

@StateTransitionFunc
fun ProjectAggregateState.userAssignedApply(event: UserAssignedToTaskEvent) {
    tasks[event.taskId]?.userId = event.userId
    updatedAt = createdAt
}
