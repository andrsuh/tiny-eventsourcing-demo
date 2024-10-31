package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.addTaskToProject
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Service
class TaskEventSubscriber @Autowired constructor(
    private val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {

    val logger: Logger = LoggerFactory.getLogger(ProjectEventsSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TaskAggregate::class, "task sub") {
            `when`(TaskCreatedEvent::class) { event ->
                projectEsService.update(event.projectId) {
                    it.addTaskToProject(event.taskId)
                }

                logger.info("Task created: ${event.taskName}")
            }

            `when`(TaskStatusChangedEvent::class) { event ->
                logger.info("Status ${event.statusId} assigned to task ${event.taskId} ")
            }

            `when`(UserAddedToTaskEvent::class) { event ->
                logger.info("User added: ${event.userId}")
            }

            `when`(TaskNameChangedEvent::class) { event ->
                logger.info("Task name changed, new name: ${event.taskName}")
            }
        }
    }
}