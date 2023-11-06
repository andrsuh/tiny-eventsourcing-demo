package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateCommands
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.TaskAggregateState
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.*
import javax.annotation.PostConstruct

@Service
@AggregateSubscriber(
    aggregateClass = TaskAggregate::class, subscriberName = "change-status-stream"
)
class AnnotationBasedProjectEventsSubscriber (
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>,
    val projectAggregateCommands: ProjectAggregateCommands,
){

    @Autowired
    private lateinit var subscriptionsManager: AggregateSubscriptionsManager

    val logger: Logger = LoggerFactory.getLogger(AnnotationBasedProjectEventsSubscriber::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.subscribe<TaskAggregate>(this)
    }

    @SubscribeEvent
    fun statusChangedSubscriber(event: StatusChangedEvent) {
        val task = taskEsService.getState(event.taskId)!!
        logger.info("Status changed: from {} to {} ",task.oldStatusId, event.statusId)
        projectEsService.update(task.projectID) { _ ->
            projectAggregateCommands.statusesCountChanged(task.oldStatusId, event.statusId)
        }
        logger.info("Status changed: {}", event.statusId)
    }

}