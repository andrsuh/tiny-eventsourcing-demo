package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class TaskServiceEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(TaskServiceEventsSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TaskServiceAggregate::class, "task-service-subscriber") {

            `when`(BoardManagerCreatedEvent::class) { event ->
                logger.info("Board manager created to project {}", event.projectId)
            }

            `when`(StatusCreatedEvent::class) { event ->
                logger.info("Status created: {}", event.statusName)
            }

            `when`(TaskCreatedEvent::class) { event ->
                logger.info("Task created: {}", event.taskName)
            }

            `when`(TaskAssigneeAddedEvent::class) { event ->
                logger.info("Assignee {} added to task {}", event.participantId, event.taskId)
            }
        }
    }
}