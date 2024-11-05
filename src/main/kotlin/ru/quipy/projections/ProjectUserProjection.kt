package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.UserAddedToProjectEvent
import ru.quipy.api.UserCreatedEvent
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.*

@Service
@AggregateSubscriber(
    aggregateClass = ProjectAggregate::class,
    subscriberName = "project-user-projection"
)
class ProjectUserProjection(
    val userRepository: ProjectUserProjectionUserRepository,
    val projectRepository: ProjectUserProjectionProjectRepository
) {
    val logger: Logger = LoggerFactory.getLogger(ProjectUserProjection::class.java)
    @SubscribeEvent
    fun projectCreatedSubscriber(event: ProjectCreatedEvent) {
        var user = userRepository.findByIdOrNull(event.creatorId)
        if (user == null) {
            user = ProjectUserProjectionUser(event.creatorId, mutableSetOf())
        }
        user.projectIds.add(event.projectId)
        userRepository.save(user)

        val project = ProjectUserProjectionProject(event.projectId, event.title)
        projectRepository.save(project)

        logger.info("Project created: {}", event.projectId)
    }

    @SubscribeEvent
    fun userAddedToProjectSubscriber(event: UserAddedToProjectEvent) {
        var user = userRepository.findByIdOrNull(event.userId)
        if (user == null) {
            user = ProjectUserProjectionUser(event.userId, mutableSetOf())
        }
        user.projectIds.add(event.projectId)
        userRepository.save(user)

        logger.info("User {} added to {} created", event.userId, event.projectId)
    }
}

@Document("project-user-projection-user")
data class ProjectUserProjectionUser(
    @Id
    var userId: UUID,
    val projectIds: MutableSet<UUID>
)

@Repository
interface ProjectUserProjectionUserRepository : MongoRepository<ProjectUserProjectionUser, UUID>

@Document("project-user-projection-project")
data class ProjectUserProjectionProject(
    @Id
    var projectId: UUID,
    val title: String
)

@Repository
interface ProjectUserProjectionProjectRepository : MongoRepository<ProjectUserProjectionProject, UUID>