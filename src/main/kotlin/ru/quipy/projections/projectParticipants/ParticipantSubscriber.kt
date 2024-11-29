package ru.quipy.projections.projectParticipants

import org.springframework.stereotype.Service
import ru.quipy.api.ProjectAddedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.projections.entity.ProjectParticipantProjection
import ru.quipy.projections.repository.ProjectParticipantsRepository
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(aggregateClass = UserAggregate::class,  subscriberName = "participant-subscriber")
class ParticipantSubscriber (
    private val projectParticipantsRepository: ProjectParticipantsRepository,
) {

    @SubscribeEvent
    fun onProjectAdded(event: ProjectAddedEvent) {
        var project: ProjectParticipantProjection? = null
        while (true) {
            project = projectParticipantsRepository.findById(event.projectId).orElse(null)
            if (project != null) {
                project.participants.add(event.userId)
                projectParticipantsRepository.save(project)
                return
            }
        }


    }
}