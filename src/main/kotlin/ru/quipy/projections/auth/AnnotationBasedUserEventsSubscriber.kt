package ru.quipy.projections.auth

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.auth.UserAggregate
import ru.quipy.api.auth.UserCreatedEvent
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(
    aggregateClass = UserAggregate::class, subscriberName = "user-subs-stream"
)
class AnnotationBasedUserEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(AnnotationBasedUserEventsSubscriber::class.java)

    @SubscribeEvent
    fun userCreatedSubscriber(event: UserCreatedEvent) {
        logger.info("User created: {}", event.personName)
    }
}