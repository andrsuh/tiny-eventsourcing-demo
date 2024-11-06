package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.project.*
import ru.quipy.api.user.UserUpdateEvent
import ru.quipy.logic.project.*
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class ProjectRelation(
    private val projectProjectionRepository: ProjectProjectionRepo
) {

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "project-simple-projection") {
            `when`(ProjectCreatedEvent::class) { event ->
                projectProjectionRepository.save(ProjectProjection(event.projectId, event.title, event.creatorId))
            }

            `when`(ProjectTitleChangedEvent::class) { event ->
                var project = projectProjectionRepository.findById(event.projectId).get()
                project.projectTitle = event.title
                projectProjectionRepository.save(project)
            }
        }
    }
}

@Document("Project-projection")
data class ProjectProjection(
    @Id
    var projectId: UUID,
    var projectTitle: String,
    var creatorId: UUID,
)

@Repository
interface ProjectProjectionRepo : MongoRepository<ProjectProjection, UUID>{}