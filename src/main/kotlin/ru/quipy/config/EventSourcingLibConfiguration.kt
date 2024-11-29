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
import ru.quipy.projections.ProjectProjectionSubscriber
import ru.quipy.projections.userProjects.UserProjectsSubscriber
import ru.quipy.projections.UsersSubscriber
import ru.quipy.projections.services.UserProjectServices
import ru.quipy.projections.userProjects.UserProjectSubscriber
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
    private lateinit var userProjectionSubscriber: UsersSubscriber

    @Autowired
    private lateinit var userProjectsSubscriber: UserProjectsSubscriber

    @Autowired
    private lateinit var userProjectSubscriber: UserProjectSubscriber

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Autowired
    private lateinit var eventStreamManager: AggregateEventStreamManager

    @Autowired
    private lateinit var userProjectServices: UserProjectServices

    @Bean
    fun projectEsService() = eventSourcingServiceFactory.create<UUID, ProjectAggregate, ProjectAggregateState>()

    @Bean
    fun userEsService() = eventSourcingServiceFactory.create<UUID, UserAggregate, UserAggregateState>()


    @PostConstruct
    fun init() {
        subscriptionsManager.subscribe<ProjectAggregate>(projectEventSubscriber)
        subscriptionsManager.subscribe<ProjectAggregate>(projectProjectionSubscriber)
        subscriptionsManager.subscribe<UserAggregate>(userProjectionSubscriber)
        subscriptionsManager.subscribe<UserAggregate>(userProjectsSubscriber)
        subscriptionsManager.subscribe<ProjectAggregate>(userProjectSubscriber)
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
