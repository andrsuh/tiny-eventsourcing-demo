package ru.quipy.projections.projectParticipants

import org.springframework.stereotype.Service
import ru.quipy.api.ProjectAddedEvent
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.projections.entity.ProjectParticipantProjection
import ru.quipy.projections.entity.UserProjectProjection
import ru.quipy.projections.repository.ProjectParticipantsRepository
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(aggregateClass = ProjectAggregate::class,  subscriberName = "project-participant-subscriber")
class ProjectParticipantSubscriber(
    private val projectParticipantsRepository: ProjectParticipantsRepository,
) {

    @SubscribeEvent
    fun onProjectCreated(event: ProjectCreatedEvent) {
        val projectProjection = ProjectParticipantProjection(
            projectId = event.projectId,
        )
        projectParticipantsRepository.save(projectProjection)
    }
}