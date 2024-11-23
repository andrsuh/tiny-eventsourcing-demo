package ru.quipy.projections

import org.springframework.beans.factory.annotation.Autowired
import ru.quipy.projections.repositories.ProjectParticipantRepository
import org.springframework.stereotype.Service
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ParticipantAddedToProjectEvent
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.projections.entities.ProjectParticipantEntity
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.UUID
import javax.transaction.Transactional

@Service
@AggregateSubscriber(aggregateClass = ProjectAggregate::class, subscriberName = "project-participants-subscriber")
class ProjectParticipantsViewService {

    @Autowired
    lateinit var projectParticipantsRepository: ProjectParticipantRepository

    @SubscribeEvent
    @Transactional
    fun onProjectCreated(event: ProjectCreatedEvent) {
        // Initialize the project with the creator as the first participant
        val participant = ProjectParticipantEntity(projectId = event.projectId, participantId = event.creatorId)
        projectParticipantsRepository.save(participant)
    }

    @SubscribeEvent
    fun onParticipantAdded(event: ParticipantAddedToProjectEvent) {
        // Add the participant to the project's participant list
        val participant = ProjectParticipantEntity(projectId = event.projectId, participantId = event.participantId)
        projectParticipantsRepository.save(participant)
    }

    // Method to get participants of a project
    fun getParticipants(projectId: UUID): List<UUID> {
        return projectParticipantsRepository.findParticipantIdsByProjectId(projectId)
    }
}