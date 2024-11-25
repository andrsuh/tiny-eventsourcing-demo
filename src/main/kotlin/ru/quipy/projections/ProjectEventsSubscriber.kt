package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.api.auth.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.auth.UserAggregateState
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct

@Service
class ProjectEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(ProjectEventsSubscriber::class.java)

    @Autowired
    val projectParticipants: ProjectParticipantsViewService = ProjectParticipantsViewService()

    @Autowired
    val projectTasks: ProjectTasksViewService = ProjectTasksViewService()

    @Autowired
    val projectStatuses: ProjectStatusesViewService = ProjectStatusesViewService()

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "project-subscriber") {

            `when`(ProjectCreatedEvent::class) { event ->
                projectParticipants.onProjectCreated(event)
                logger.info("Project created: {}", event.title)
            }

            `when`(ParticipantAddedToProjectEvent::class) { event ->
                projectParticipants.onParticipantAdded(event)
                logger.info("Participant {} added to project", event.participantId)
            }

            `when`(StatusCreatedEvent::class) { event ->
                projectStatuses.onStatusCreated(event)
                logger.info("Status created: {}", event.statusName)
            }
            `when`(StatusColorChangedEvent::class) { event ->
                projectStatuses.onStatusColorChanged(event)
                logger.info("Status color changed: {}", event.newColor)

            }
            `when`(StatusDeletedEvent::class) { event ->
                projectStatuses.onStatusDeleted(event)
                logger.info("Status was deleted: {}", event)

            }

            `when`(TaskCreatedEvent::class) { event ->
                projectTasks.onTaskAdded(event)
                logger.info("Task created: {}", event.taskName)
            }
            `when`(TaskNameChangedEvent::class) { event ->
                projectTasks.onTaskNameChangedEvent(event)
                logger.info("Task changed name: {}", event.newName)
            }
            `when`(TaskDeletedEvent::class) { event ->
                projectTasks.onTaskDeletedEvent(event)
                logger.info("Task was deleted: {}", event)
            }
            `when`(TaskAssigneeAddedEvent::class) { event ->
                projectTasks.onTaskAssigneeAddedEvent(event)
                logger.info("Task changed name: {}", event.participantId)
            }
            `when`(TaskAssigneeAddedEvent::class) { event ->
                logger.info("Assignee {} added to task {}", event.participantId, event.taskId)
            }
        }
    }

    fun getParticipants(projectId: UUID): List<UUID>? {
        return projectParticipants.getParticipants(projectId)
    }

    fun getTasks(projectId: UUID): List<UUID>? {
        return projectTasks.getTasks(projectId)
    }

    fun getStatuses(projectId: UUID): List<ReturnStatusDto> {
        return projectStatuses.getStatuses(projectId)
            .map { entity ->
                ReturnStatusDto(
                    name = entity.name,
                    color = entity.color.toString(),
                    projectId = entity.projectId
                )
            }
    }

}


data class ReturnStatusDto(
    val name: String,
    val color: String,
    val projectId: UUID
)