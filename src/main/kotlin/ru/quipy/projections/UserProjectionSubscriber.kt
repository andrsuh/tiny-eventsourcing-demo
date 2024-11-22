package ru.quipy.projections

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.ProjectAddedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.projections.entity.UserProjection
import ru.quipy.projections.repository.UserProjectionRepository
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.*

@Service
@AggregateSubscriber(aggregateClass = UserAggregate::class, subscriberName = "user-projection-subscriber")
class UserProjectionSubscriber(
        private val userProjectionRepository: UserProjectionRepository
) {
    private val logger = LoggerFactory.getLogger(UserProjectionSubscriber::class.java)

    @SubscribeEvent
    fun onUserCreated(event: UserCreatedEvent) {
        logger.info("Handling UserCreatedEvent for userId: {}", event.userId)
        val userProjection = UserProjection(
                userId = event.userId,
                login = event.login,
                username = event.username,
                updatedAt = event.createdAt
        )
        userProjectionRepository.save(userProjection)
    }


    @SubscribeEvent
    fun onProjectAdded(event: ProjectAddedEvent) {
        logger.info("Handling ProjectAddedEvent for userId: {}, projectId: {}", event.userId, event.projectId)
        val user = userProjectionRepository.findById(event.userId).orElse(null)
        if (user != null) {
            if (!user.projects.contains(event.projectId)) {
                user.projects.add(event.projectId)
                userProjectionRepository.save(user)
            } else {
                logger.warn("ProjectId: {} already assigned to userId: {}", event.projectId, event.userId)
            }
        } else {
            logger.warn("UserProjection not found for userId: {}", event.userId)
        }
    }
}
