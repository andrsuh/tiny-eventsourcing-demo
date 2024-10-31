package ru.quipy.logic.project

import ru.quipy.api.project.*
import ru.quipy.api.project.ProjectAggregate
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectTitle: String
    lateinit var creator: String
    var tasks = mutableSetOf<UUID>()
    var members = mutableSetOf<String>()
    var statuses = mutableSetOf<String>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creator = event.creator
        members.add(event.creator)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun memberAddedApply(event: MemberAddedEvent) {
        members.add(event.newMember)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks.add(event.taskId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskDeletedApply(event: TaskDeletedEvent) {
        tasks.remove(event.taskId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusAddedApply(event: StatusAddedEvent) {
        statuses.add(event.addedStatus)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        statuses.remove(event.deletedStatus)
        updatedAt = createdAt
    }
}
