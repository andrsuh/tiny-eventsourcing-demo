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

    private lateinit var projectTitle: String
    private var projectDescription: String = ""


    private lateinit var creatorId: UUID
    var participants = mutableMapOf<UUID, ParticipantEntity>()
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var projectTags = mutableMapOf<UUID, TagEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId

        participants[creatorId] = ParticipantEntity(id = creatorId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun projectUpdatedApply(event: ProjectUpdatedEvent) {
        projectId = event.projectId

        if (event.title != null)
            projectTitle = event.title

        if (event.description != null)
            projectDescription = event.description

        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun projectUserAddedApply(event: ProjectMemberCreatedEvent) {
        participants[event.userId] = ParticipantEntity(id = event.userId)
        updatedAt = event.createdAt
    }

    // TODO Delete also from all tasks entities
    @StateTransitionFunc
    fun projectUserRemovedApply(event: ProjectMemberRemovedEvent) {
        participants.remove(event.userId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskUpdatedApply(event: TaskUpdatedEvent) {
        val task: TaskEntity = tasks[event.taskId] ?: throw IllegalStateException("Task not found")

        if (event.taskName != null)
            task.name = event.taskName

        if (event.taskDescription != null)
            task.description = event.taskDescription
    }

    @StateTransitionFunc
    fun taskAssignedApply(event: TaskAssignedEvent) {
        val task: TaskEntity = tasks[event.taskId] ?: throw IllegalStateException("Task not found")

        val participant: ParticipantEntity = participants[event.userId] ?: throw IllegalStateException("Participant not found")

        task.assignee =  participant
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskDeletedApply(event: TaskDeletedEvent) {
        tasks.remove(event.taskId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun tagCreatedApply(event: TagCreatedEvent) {
        projectTags[event.tagId] = TagEntity(event.tagId, event.tagName, Color.valueOf(event.tagColor))
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun tagDeletedApply(event: TagDeletedEvent) {
        projectTags.remove(event.tagId)
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, "", null, mutableSetOf())
        updatedAt = createdAt
    }
}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var description: String = "",
    var assignee: ParticipantEntity? = null,
    val tagsAssigned: MutableSet<UUID>
)

data class TagEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String = "CREATED",
    val color: Color
)

data class ParticipantEntity (
    val id: UUID = UUID.randomUUID(),
)

enum class Color {
    GREEN, BLUE, YELLOW, RED, PURPLE
}

/**
 * Demonstrates that the transition functions might be represented by "extension" functions, not only class members functions
 */
@StateTransitionFunc
fun ProjectAggregateState.tagAddedToTaskApply(event: TagAddedToTaskEvent) {
    tasks[event.taskId]?.tagsAssigned?.add(event.tagId) ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    updatedAt = createdAt
}
