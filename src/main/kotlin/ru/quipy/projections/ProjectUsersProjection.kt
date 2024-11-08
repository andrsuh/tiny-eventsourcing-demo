package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.project.ProjectAggregate
import ru.quipy.api.project.UserAssignedToProjectEvent
import ru.quipy.api.task.UserAssignedToTaskEvent
import ru.quipy.api.user.UserAggregate
import ru.quipy.api.user.UserCreatedEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class ProjectUserRelation(
        private val projectUserProjectionRepo: ProjectUserProjectionRepo
) {

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "ProjectAggregateSubscriberPUProjection") {

            `when`(UserAssignedToProjectEvent::class) { event ->
                projectUserProjectionRepo.save(ProjectUserProjection(event.projectId, event.userId))
            }
        }
    }
}

@Document("user-project-projection")
data class ProjectUserProjection(
        val projectId: UUID,
        val userId: UUID
)

@Repository
interface ProjectUserProjectionRepo : MongoRepository<ProjectUserProjection, UUID>;
