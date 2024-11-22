package ru.quipy.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.projections.AnnotationBasedProjectEventsSubscriber
import ru.quipy.projections.ProjectEventsSubscriber
import ru.quipy.projections.ProjectProjectionSubscriber
import ru.quipy.projections.UserProjectionSubscriber
import ru.quipy.streams.AggregateEventStreamManager
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Configuration
class EventSourcingLibConfiguration {

    private val logger = LoggerFactory.getLogger(EventSourcingLibConfiguration::class.java)

    @Autowired
    private lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @Autowired
    private lateinit var projectEventSubscriber: AnnotationBasedProjectEventsSubscriber

    @Autowired
    private lateinit var projectProjectionSubscriber: ProjectProjectionSubscriber

    @Autowired
    private lateinit var userProjectionSubscriber: UserProjectionSubscriber

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Autowired
    private lateinit var eventStreamManager: AggregateEventStreamManager

    @Bean
    fun projectEsService() = eventSourcingServiceFactory.create<UUID, ProjectAggregate, ProjectAggregateState>()

    @Bean
    fun userEsService() = eventSourcingServiceFactory.create<UUID, UserAggregate, UserAggregateState>()

    @PostConstruct
    fun init() {
        subscriptionsManager.subscribe<ProjectAggregate>(projectEventSubscriber)
        subscriptionsManager.subscribe<ProjectAggregate>(projectProjectionSubscriber)
        subscriptionsManager.subscribe<UserAggregate>(userProjectionSubscriber)
        eventStreamManager.maintenance {
            onRecordHandledSuccessfully { streamName, eventName ->
                logger.info("Stream $streamName successfully processed record of $eventName")
            }

            onBatchRead { streamName, batchSize ->
                logger.info("Stream $streamName read batch size: $batchSize")
            }
        }
    }
}
