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
class UsersSubscriber(
        private val userProjectionRepository: UserProjectionRepository
) {
    private val logger = LoggerFactory.getLogger(UsersSubscriber::class.java)

    @SubscribeEvent
    fun onUserCreated(event: UserCreatedEvent) {
        logger.info("Handling UserCreatedEvent for userId: {}", event.userId)
        val userProjection = UserProjection(
                userId = event.userId,
                login = event.login,
                username = event.username,
        )
        userProjectionRepository.save(userProjection)
    }

}
