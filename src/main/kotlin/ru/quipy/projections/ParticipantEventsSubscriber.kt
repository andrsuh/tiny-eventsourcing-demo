package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class ParticipantEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(ParticipantEventsSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ParticipantAggregate::class, "participant-subscriber") {

            `when`(ParticipantCreatedEvent::class) { event ->
                logger.info("Participant {} created and added to project {}", event.participantId, event.projectId)
            }

            `when`(ParticipantAddedToProjectEvent::class) { event ->
                logger.info("Participant added to project {}", event.projectId)
            }
        }
    }
}