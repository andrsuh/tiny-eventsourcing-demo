package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(
    aggregateClass = ProjectAggregate::class, subscriberName = "task-service-subs-stream"
)
class AnnotationBasedTaskServiceEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(AnnotationBasedTaskServiceEventsSubscriber::class.java)

    @SubscribeEvent
    fun boardManagerCreatedSubscriber(event: BoardManagerCreatedEvent) {
        logger.info("Board manager created to project {}", event.projectId)
    }

    @SubscribeEvent
    fun statusCreatedSubscriber(event: StatusCreatedEvent) {
        logger.info("Status created: {}", event.statusName)
    }

    @SubscribeEvent
    fun taskCreatedSubscriber(event: TaskCreatedEvent) {
        logger.info("Task created: {}", event.taskName)
    }

    @SubscribeEvent
    fun taskAssigneeAddedEventSubscriber(event: TaskAssigneeAddedEvent) {
        logger.info("Assignee {} added to task {}", event.participantId, event.taskId)
    }
}