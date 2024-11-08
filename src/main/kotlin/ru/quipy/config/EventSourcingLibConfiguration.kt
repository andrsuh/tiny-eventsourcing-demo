package ru.quipy.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.logic.Project
import ru.quipy.logic.User
import ru.quipy.streams.AggregateEventStreamManager
import java.util.*
import javax.annotation.PostConstruct

@Configuration
class EventSourcingLibConfiguration {

    private val logger = LoggerFactory.getLogger(EventSourcingLibConfiguration::class.java)

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Autowired
    private lateinit var eventStreamManager: AggregateEventStreamManager

    @Bean
    fun projectEsService() = eventSourcingServiceFactory.create<UUID, ProjectAggregate, Project>()

    @Bean
    fun userEsService() = eventSourcingServiceFactory.create<UUID, UserAggregate, User>()

    @PostConstruct
    fun init() {
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
