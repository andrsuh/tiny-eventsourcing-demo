package ru.quipy.logic.project

import ru.quipy.api.project.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    lateinit var title: String
    lateinit var creatorId: String
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var participants = mutableMapOf<UUID, Participant>()

    override fun getId() = projectId

    @StateTransitionFunc
    fun onProjectCreated(event: ProjectCreatedEvent) {
        projectId = event.projectId
        title = event.title
        creatorId = event.creatorId
    }

    @StateTransitionFunc
    fun onParticipantAdded(event: ParticipantAddedEvent) {
        participants[event.userId] = Participant(event.userId, event.userName)
    }
}

data class TaskEntity(
        val id: UUID,
        val name: String
)

data class Participant(
        val userId: UUID,
        val userName: String
)

/**
 * Функция для назначения участника проекта
 */
@StateTransitionFunc
fun ProjectAggregateState.participantAddedApply(event: ParticipantAddedEvent) {
    participants[event.userId] = Participant(event.userId, event.userName)
}
