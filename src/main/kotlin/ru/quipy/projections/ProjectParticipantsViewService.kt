package ru.quipy.projections

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import ru.quipy.projections.repositories.ProjectParticipantRepository
import org.springframework.stereotype.Service
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ParticipantAddedToProjectEvent
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.projections.entities.ProjectParticipantEntity
import ru.quipy.projections.repositories.UserRepository
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.UUID
import javax.transaction.Transactional

@Service
@AggregateSubscriber(aggregateClass = ProjectAggregate::class, subscriberName = "project-participants-subscriber")
class ProjectParticipantsViewService {

    @Autowired
    lateinit var projectParticipantsRepository: ProjectParticipantRepository
    @Autowired
    lateinit var usersRepository: UserRepository

    @SubscribeEvent
    @Transactional
    fun onProjectCreated(event: ProjectCreatedEvent) {
        val participant = ProjectParticipantEntity(projectId = event.projectId, participantId = event.creatorId)
        projectParticipantsRepository.save(participant)
    }

    @SubscribeEvent
    fun onParticipantAdded(event: ParticipantAddedToProjectEvent) {
        val participant = ProjectParticipantEntity(projectId = event.projectId, participantId = event.participantId)
        projectParticipantsRepository.save(participant)
    }

    fun getParticipants(projectId: UUID): List<ProjectParticipantDto> {
        val participants = projectParticipantsRepository.findParticipantIdsByProjectId(projectId)
        val users = usersRepository.findAllByUserIdIn(participants).associateBy { it.userId }
        return participants.map { userId ->
            val user = users[userId]
            ProjectParticipantDto(
                id = userId,
                name = user?.name,
                nickname = user?.nickname
            )
        }

    }
}

data class ProjectParticipantDto (
    val name: String?,
    val nickname: String?,
    val id: UUID
)