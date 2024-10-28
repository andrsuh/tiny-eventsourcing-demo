package ru.quipy.logic

import org.springframework.beans.factory.annotation.Autowired
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    @Autowired
    private lateinit var taskServiceEsService: EventSourcingService<UUID, TaskServiceAggregate, TaskServiceAggregateState>
    @Autowired
    private lateinit var participantEsService: EventSourcingService<UUID, ParticipantAggregate, ParticipantAggregateState>

    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectTitle: String
    lateinit var creatorId: UUID

    override fun getId() = projectId

    fun createProject(id: UUID, title: String, creatorId: UUID): ProjectCreatedEvent {
        val projectCreatedEvent = ProjectCreatedEvent(
            projectId = id,
            title = title,
            creatorId = creatorId,
        )
        taskServiceEsService.create { it.create(id, creatorId) }
        if (participantEsService.getState(creatorId) == null) {
            participantEsService.create { it.create(creatorId, id) }
        } else {
            participantEsService.update(creatorId) {
                it.addProject(id)
            }
        }

        return projectCreatedEvent
    }

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
        updatedAt = createdAt
    }
}