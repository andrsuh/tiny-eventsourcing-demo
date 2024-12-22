package ru.quipy.projections

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class TaskEventsSubscriber {

//    val logger: Logger = LoggerFactory.getLogger(TaskEventsSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
//        subscriptionsManager.createSubscriber(TaskAggregate::class, "task-create") {

//            `when`(TaskCreatedEvent::class) { event ->
//                projectService.update(event.projectId) { it.createTaskApply(event) }
//            }

//            `when`(TagCreatedEvent::class) { event ->
//                logger.info("Tag created: {}", event.tagName)
//            }
//
//            `when`(TagAssignedToTaskEvent::class) { event ->
//                logger.info("Tag {} assigned to task {}: ", event.tagId, event.taskId)
//            }
//        }
    }
}