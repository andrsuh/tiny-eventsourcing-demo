package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*


class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    private lateinit var name: String
    lateinit var description: String
    val participants = mutableListOf<UUID>()
    val projectStatuses = mutableMapOf<UUID, StatusEntity>()
    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        name = event.projectName
        participants.add(element = event.creatorId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun projectUpdatedApply(event: ProjectUpdatedEvent) {
        name = event.projectName
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun participantAddApply(event: ParticipantAddedEvent) {
        participants.add(element = event.userId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun leaveProjectApply(event: LeaveProjectEvent) {
        participants.remove(element = event.userId)
        updatedAt = event.createdAt
    }
    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        projectStatuses[event.statusId] = StatusEntity(id=event.statusId, name = event.statusName, color = event.color)
        updatedAt = event.createdAt
    }
    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        projectStatuses.remove(event.statusId)
        updatedAt = event.createdAt
    }
}

data class StatusEntity(
        val id: UUID,
        val name: String,
        val color: String
)


//
//// Service's business logic
//class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
//    private lateinit var projectId: UUID
//    var createdAt: Long = System.currentTimeMillis()
//    var updatedAt: Long = System.currentTimeMillis()
//
//    lateinit var projectTitle: String
//    lateinit var creatorId: UUID
//    var tasks = mutableMapOf<UUID, TaskEntity>()
//    var projectTags = mutableMapOf<UUID, TagEntity>()
//
//    override fun getId() = projectId
//
//    // State transition functions which is represented by the class member function
//    @StateTransitionFunc
//    fun projectCreatedApply(event: ProjectCreatedEvent) {
//        projectId = event.projectId
//        projectTitle = event.title
//        creatorId = event.creatorId
//        updatedAt = createdAt
//    }
//
//    @StateTransitionFunc
//    fun tagCreatedApply(event: TagCreatedEvent) {
//        projectTags[event.tagId] = TagEntity(event.tagId, event.tagName)
//        updatedAt = createdAt
//    }
//
//    @StateTransitionFunc
//    fun taskCreatedApply(event: TaskCreatedEvent) {
//        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, mutableSetOf())
//        updatedAt = createdAt
//    }
//}
//
//data class TaskEntity(
//    val id: UUID = UUID.randomUUID(),
//    val name: String,
//    val tagsAssigned: MutableSet<UUID>
//)
//
//data class TagEntity(
//    val id: UUID = UUID.randomUUID(),
//    val name: String
//)

/**
 * Demonstrates that the transition functions might be representer by "extension" functions, not only class members functions
// */
//@StateTransitionFunc
//fun ProjectAggregateState.tagAssignedApply(event: TagAssignedToTaskEvent) {
//    tasks[event.taskId]?.tagsAssigned?.add(event.tagId)
//        ?: throw IllegalArgumentException("No such task: ${event.taskId}")
//    updatedAt = createdAt
//}
