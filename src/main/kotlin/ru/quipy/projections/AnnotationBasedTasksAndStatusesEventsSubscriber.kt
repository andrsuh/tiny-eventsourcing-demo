package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.UserAggregate
import ru.quipy.streams.annotation.AggregateSubscriber

@Service
@AggregateSubscriber(
        aggregateClass = UserAggregate::class, subscriberName = "tasks-and-statuses-subs-stream"
)
class AnnotationBasedTaskStatusAndTasksEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(AnnotationBasedTaskStatusAndTasksEventsSubscriber::class.java)

//    TODO: сделать
//    @SubscribeEvent
//    fun taskCreatedSubscriber(event: TaskCreatedEvent) {
//        logger.info("Task created: {}", event.taskName)
//    }
//
//    @SubscribeEvent
//    fun tagCreatedSubscriber(event: TagCreatedEvent) {
//        logger.info("Tag created: {}", event.tagName)
//    }
}
