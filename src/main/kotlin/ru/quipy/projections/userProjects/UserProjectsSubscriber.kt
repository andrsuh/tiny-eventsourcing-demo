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
@AggregateSubscriber(aggregateClass = UserAggregate::class,  subscriberName = "user-projects-subscriber")
class UserProjectsSubscriber(
    private val userProjectsRepository: UserProjectsRepository,
) {
    private val logger = LoggerFactory.getLogger(UsersSubscriber::class.java)

    @SubscribeEvent
    fun onUserCreated(event: UserCreatedEvent) {
        logger.info("Handling UserCreatedEvent for userId: {}", event.userId)
        val userProjection = UserProjectsProjection(
            userId = event.userId,
            userLogin = event.login,
            username = event.username,
        )
        userProjectsRepository.save(userProjection)
    }

    @SubscribeEvent
    fun onProjectAdded(event: ProjectAddedEvent) {
        val user = userProjectsRepository.findById(event.userId).orElse(null)
        if (user != null) {
            user.projects.add(event.projectId)
            userProjectsRepository.save(user)
        }
    }
}