package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct
import ru.quipy.api.project.*
import java.util.UUID

@Component
class ProjectUserRelation(
    private val projectUserProjectionRepo: ProjectUserProjectionRepo
) {

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "user-project-Projection") {
            `when`(ProjectCreatedEvent::class) { event ->
                projectUserProjectionRepo.save(ProjectUserProjection(event.projectId, event.creatorId))
            }

            `when`(AddParticipantToProjectEvent::class) { event ->
                projectUserProjectionRepo.save(ProjectUserProjection(event.projectId, event.participantId))
            }
        }
    }
}

@Document("user-project-projection")
data class ProjectUserProjection(
    var projectId: UUID,
    var userId: UUID
)

@Repository
interface ProjectUserProjectionRepo : MongoRepository<ProjectUserProjection, UUID>{
    fun findAllByUserIdNotNull(userId : UUID): List<ProjectUserProjection>
    fun findAllByProjectIdNotNull(projectId : UUID): List<ProjectUserProjection>
}