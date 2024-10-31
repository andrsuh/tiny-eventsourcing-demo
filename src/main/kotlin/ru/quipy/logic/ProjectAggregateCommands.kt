package ru.quipy.logic

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.quipy.api.*
import ru.quipy.projections.AnnotationBasedProjectEventsSubscriber

import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions
@Component
class ProjectAggregateCommands {
    val logger: Logger = LoggerFactory.getLogger(ProjectAggregateCommands::class.java)
    fun createProject(title: String, creatorId: String): ProjectCreatedEvent {
        return ProjectCreatedEvent(
            projectId = UUID.randomUUID(),
            title = title,
            creatorId = creatorId
        )
    }

    fun addProjectMember(projectId: UUID, userId: UUID): ProjectExecutorAddedEvent {
        return ProjectExecutorAddedEvent(
            projectId = projectId,
            userId = userId
        )
    }

    fun addStatus(projectId: UUID, statusName: String, color: String, statusId: UUID = UUID.randomUUID()): StatusCreatedEvent {
        return StatusCreatedEvent(
            projectId = projectId,
            statusName = statusName,
            color = color,
            statusId = statusId
        )
    }

    fun deleteStatus(projectId: UUID, statusId: UUID): StatusDeletedEvent {
        return StatusDeletedEvent(
            projectId = projectId,
            statusId = statusId
        )
    }

    fun statusesCountChanged(oldStatusId: UUID, newStatusId: UUID): StatusesCountChangedEvent {
        logger.info("Status changed: from {} to {}", oldStatusId, newStatusId)
        return StatusesCountChangedEvent(
            oldStatusId = oldStatusId,
            newStatusId = newStatusId
        )
    }

//    fun addDefaultStatus() : DefaultStatusAddedEvent{
//        return DefaultStatusAddedEvent(
//            name =
//        )
//    }

}


