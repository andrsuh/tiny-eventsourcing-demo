package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.*
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class ProjectEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(ProjectEventsSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "project subdomains") {

            `when`(StatusCreatedEvent::class) { event ->
                logger.info("Status created: ${event.statusName}")
            }

            `when`(StatusDeletedEvent::class) { event ->
                logger.info("Status deleted: ${event.statusId}")
            }

            `when`(ProjectCreatedEvent::class) { event ->
                logger.info("Project created: ${event.projectId}")
            }

            `when`(TaskAddedEvent::class) { event ->
                logger.info("Task added: ${event.taskId}")
            }

            `when`(ProjectNameChangedEvent::class) { event ->
                logger.info("Project name changed: ${event.name}")
            }

            `when`(UserAddedToProjectEvent::class) { event ->
                logger.info("User added: ${event.userId}")
            }
        }
    }
}