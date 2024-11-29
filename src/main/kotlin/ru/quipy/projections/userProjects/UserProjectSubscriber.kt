package ru.quipy.projections.userProjects

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.projections.UsersSubscriber
import ru.quipy.projections.entity.UserProjectProjection
import ru.quipy.projections.entity.UserProjectsProjection
import ru.quipy.projections.repository.UserProjectRepository
import ru.quipy.projections.repository.UserProjectsRepository
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(aggregateClass = ProjectAggregate::class,  subscriberName = "user-project-subscriber")
class UserProjectSubscriber(
    private val projectRepository: UserProjectRepository,
) {

    @SubscribeEvent
    fun onProjectCreated(event: ProjectCreatedEvent) {
        val projectProjection = UserProjectProjection(
            projectId = event.projectId,
            title = event.title,
            description = event.description,
        )
        projectRepository.save(projectProjection)
    }

}