package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.ParticipantAddedToProjectEvent
import ru.quipy.api.ParticipantCreatedEvent
import ru.quipy.api.ProjectAggregate
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(
    aggregateClass = ProjectAggregate::class, subscriberName = "participant-subs-stream"
)
class AnnotationBasedParticipantEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(AnnotationBasedParticipantEventsSubscriber::class.java)

    @SubscribeEvent
    fun participantCreatedSubscriber(event: ParticipantCreatedEvent) {
        logger.info("Participant created: {}", event.participantId)
    }

    @SubscribeEvent
    fun participantAddedToProjectSubscriber(event: ParticipantAddedToProjectEvent) {
        logger.info("Participant added to project: {}", event.projectId)
    }
}