package ru.quipy.projections

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.api.task.*
import java.util.*
import javax.annotation.PostConstruct

@Component
class ProjectTasksRelation(
    private val projectTaskProjectionRepo : ProjectTaskProjectionRepo
) {

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TaskAggregate::class, "task-project-projection") {
            `when`(TaskCreatedEvent::class) { event ->
                projectTaskProjectionRepo.save(ProjectTasksProjection(event.projectId, event.taskId))
            }
        }
    }
}

@Document("project-task-projection")
data class ProjectTasksProjection(
    var projectId: UUID,
    var taskId: UUID
)

@Repository
interface ProjectTaskProjectionRepo : MongoRepository<ProjectTasksProjection, UUID>{
    fun findAllByProjectIdNotNull(projectId : UUID): List<ProjectTasksProjection>
}