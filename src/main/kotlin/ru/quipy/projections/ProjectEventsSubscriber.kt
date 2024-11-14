package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class ProjectEventsSubscriber {

//    val logger: Logger = LoggerFactory.getLogger(ProjectEventsSubscriber::class.java)
//
//    @Autowired
//    lateinit var subscriptionsManager: AggregateSubscriptionsManager
//
//    @PostConstruct
//    fun init() {
//        subscriptionsManager.createSubscriber(ProjectAggregate::class, "project-subscriber") {
//
//            `when`(ProjectCreatedEvent::class) { event ->
//                logger.info("Project {} with id {} was created by user with id {}",
//                    event.title, event.projectId, event.creatorId)
//            }
//
//            `when`(ProjectUpdatedEvent::class) { event ->
//                logger.info("Project {} with id {} was updated, update description: {}",
//                    event.title, event.projectId, event.description)
//            }
//
//            `when`(ProjectUserAddedEvent::class) { event ->
//                logger.info("User with id {} was added to the project with id {}",
//                    event.userId, event.projectId)
//            }
//
//            `when`(ProjectUserRemovedEvent::class) { event ->
//                logger.info("User with id {} was removed from the project with id {}",
//                    event.userId, event.projectId)
//            }
//
//            `when`(TaskCreatedEvent::class) { event ->
//                logger.info("Task {} with id {} was created in the project with id {}",
//                    event.taskName, event.taskId, event.projectId)
//            }
//
//            `when`(TaskUpdatedEvent::class) { event ->
//                logger.info("Task {} with id {} in project with id {} was updated",
//                    event.taskName, event.taskId, event.projectId)
//            }
//
//            `when`(TaskExecutorAddedEvent::class) { event ->
//                logger.info("User with id {} became executor of task with id {} in project with id {}",
//                    event.userId, event.taskId, event.projectId)
//            }
//
//            `when`(TaskDeletedEvent::class) { event ->
//                logger.info("Task with id {} was deleted from project with id {}",
//                    event.taskId, event.projectId)
//            }
//
//            `when`(TagCreatedEvent::class) { event ->
//                logger.info("Tag {} with id {} was created in project with id {}",
//                    event.tagName, event.tagId, event.projectId)
//            }
//
//            `when`(TagAssignedToTaskEvent::class) { event ->
//                logger.info("Tag with id {} was assigned to task with id {} in project with id {}",
//                    event.tagId, event.taskId, event.projectId)
//            }
//
//        }
//    }
}