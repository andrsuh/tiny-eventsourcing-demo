package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(
    aggregateClass = ProjectAggregate::class, subscriberName = "project-subscriber"
)
class AnnotationBasedProjectEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(AnnotationBasedProjectEventsSubscriber::class.java)

    @SubscribeEvent
    fun projectCreatedSubscriber(event: ProjectCreatedEvent) {
        logger.info("Project {} with id {} was created by user with id {}",
            event.title, event.projectId, event.creatorId)
    }

    @SubscribeEvent
    fun projectUpdatedSubscriber(event: ProjectUpdatedEvent) {
        logger.info("Project {} with id {} was updated, update description: {}",
            event.title, event.projectId, event.description)
    }

    @SubscribeEvent
    fun projectUserAddedSubscriber(event: ProjectMemberCreatedEvent) {
        logger.info("User with id {} was added to the project with id {}",
            event.userId, event.projectId)
    }

    @SubscribeEvent
    fun projectUserRemovedSubscriber(event: ProjectMemberRemovedEvent) {
        logger.info("User with id {} was removed from the project with id {}",
            event.userId, event.projectId)
    }

    @SubscribeEvent
    fun taskCreatedSubscriber(event: TaskCreatedEvent) {
        logger.info("Task {} with id {} was created in the project with id {}",
            event.taskName, event.taskId, event.projectId)
    }

    @SubscribeEvent
    fun taskUpdatedSubscriber(event: TaskUpdatedEvent) {
        logger.info("Task {} with id {} in project with id {} was updated",
            event.taskName, event.taskId, event.projectId)
    }

    @SubscribeEvent
    fun taskExecutorAddedSubscriber(event: TaskAssignedEvent) {
        logger.info("User with id {} became executor of task with id {} in project with id {}",
            event.userId, event.taskId, event.projectId)
    }

    @SubscribeEvent
    fun taskDeletedSubscriber(event: TaskDeletedEvent) {
        logger.info("Task with id {} was deleted from project with id {}",
            event.taskId, event.projectId)
    }

    @SubscribeEvent
    fun tagCreatedSubscriber(event: TagCreatedEvent) {
        logger.info("Tag {} with id {} was created in project with id {}",
            event.tagName, event.tagId, event.projectId)
    }

    @SubscribeEvent
    fun tagAssignedToTaskSubscriber(event: TagAddedToTaskEvent) {
        logger.info("Tag with id {} was assigned to task with id {} in project with id {}",
            event.tagId, event.taskId, event.projectId)
    }
}