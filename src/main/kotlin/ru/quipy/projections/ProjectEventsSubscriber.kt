package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.project.ProjectAggregate
import ru.quipy.api.project.StatusCreatedEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class ProjectEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(ProjectEventsSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "ProjectAggregateSubscriber") {
            `when`(StatusCreatedEvent::class) { event ->
                logger.info("Tag created: {}", event.statusName)
            }

//            `when`(TaskCreatedEvent::class) { event ->
//                logger.info("Task created: {}", event.taskName)
//            }
//
//            `when`(TagAssignedToTaskEvent::class) { event ->
//                logger.info("Tag {} assigned to task {}: ", event.tagId, event.taskId)
//            }
        }
    }
}