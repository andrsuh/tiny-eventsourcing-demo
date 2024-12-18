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
    var members = mutableSetOf<UUID>()

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
        projectTags[event.tagId] = TagEntity(event.tagId, event.tagName)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, mutableSetOf(), mutableSetOf())
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun memberAddedApply(event: MemberAddedEvent) {
        members.add(event.userId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun executorAssignedApply(event: ExecutorAssignedToTaskEvent) {
        tasks[event.taskId]?.executorsAssigned?.add(event.executorId)
                ?: throw IllegalArgumentException("No such task: ${event.taskId}")
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusChangedApply(event: TaskStatusChangedEvent) {
        tasks[event.taskId]?.statusEnum = StatusEnum.values()[event.newStatus]
    }
}

data class TaskEntity(
        val id: UUID = UUID.randomUUID(),
        val name: String,
        val tagsAssigned: MutableSet<UUID>,
        val executorsAssigned: MutableSet<UUID>,
        var statusEnum: StatusEnum = StatusEnum.OPENED
)

data class TagEntity(
        val id: UUID = UUID.randomUUID(),
        val name: String
)

enum class StatusEnum(description: String){
    OPENED("Opened"),
    IN_PROGRESS("In progress"),
    CLOSED("Closed");
}

/**
 * Demonstrates that the transition functions might be representer by "extension" functions, not only class members functions
 */
@StateTransitionFunc
fun ProjectAggregateState.tagAssignedApply(event: TagAssignedToTaskEvent) {
    tasks[event.taskId]?.tagsAssigned?.add(event.tagId)
            ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    updatedAt = event.createdAt
}
