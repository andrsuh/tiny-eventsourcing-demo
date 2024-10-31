package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateCommands
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.ProjectAggregateState.Companion.defaultStatus
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.*
import javax.annotation.PostConstruct

@Service
@AggregateSubscriber(
    aggregateClass = ProjectAggregate::class, subscriberName = "default-status-stream"
)
class DefaultStatusProjectEventsSubscriber (
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val projectAggregateCommands: ProjectAggregateCommands,
){

    @Autowired
    private lateinit var subscriptionsManager: AggregateSubscriptionsManager

    val logger: Logger = LoggerFactory.getLogger(DefaultStatusProjectEventsSubscriber::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.subscribe<ProjectAggregate>(this)
    }

    @SubscribeEvent
    fun addDefaultStatus(event: ProjectCreatedEvent) {
        projectEsService.update(event.projectId) { _ ->
            projectAggregateCommands.addStatus(projectId = event.projectId, statusName = defaultStatus.name,
                color = defaultStatus.color, statusId = defaultStatus.id)
        }
    }

}