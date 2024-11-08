package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.project.ProjectAggregate
import ru.quipy.api.project.ProjectCreatedEvent
import ru.quipy.api.project.StatusCreatedEvent
import ru.quipy.api.project.UserAssignedToProjectEvent
import ru.quipy.api.task.StatusAssignedToTaskEvent
import ru.quipy.api.task.TaskAggregate
import ru.quipy.api.task.TaskCreatedEvent
import ru.quipy.api.user.UserAggregate
import ru.quipy.api.user.UserCreatedEvent
import ru.quipy.logic.StatusEntity
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct
import kotlin.collections.HashMap

@Component
class ProjectTasksRelation(
        private val projectTaskUserRepo : ProjectTaskUserRepo
) {

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "ProjectAggregateSubscriberPTUProjection") {

            `when`(ProjectCreatedEvent::class) { event ->
                projectTaskUserRepo.save(ProjectTasksUserProjection(event.projectId, event.createdAt, event.createdAt,
                        event.title, event.creatorId, HashMap(), ArrayList(), arrayListOf(event.creatorId)))
            }

            `when`(UserAssignedToProjectEvent::class) { event ->
                var projectTaskUser = projectTaskUserRepo.findByProjectId(event.projectId) ?: throw Exception();
                projectTaskUser.users.add(event.userId.toString())
            }

            `when`(StatusCreatedEvent::class) { event ->
                val status = StatusEntity(event.id, event.statusName)
                val projectUserTask = projectTaskUserRepo.findByProjectId(event.projectId) ?: throw Exception();
                if (!projectUserTask.projectStatuses?.contains(event.id)!!) {
                    projectUserTask.projectStatuses!!.put(event.statusId, status);
                }
                projectTaskUserRepo.save(projectUserTask);
            }
        }

        subscriptionsManager.createSubscriber(TaskAggregate::class, "TaskAggregateSubscriberPTUProjection") {
            `when`(TaskCreatedEvent::class) {event ->
                val projectTaskUser = projectTaskUserRepo.findByProjectId(event.projectId) ?: throw Exception();
                projectTaskUser.tasks?.add(TaskEntity(event.taskId, event.projectId,
                        event.statusId, event.taskTitle, event.createdAt));
            }

            `when`(StatusAssignedToTaskEvent::class) { event ->
                val projectTasksUserProjections = projectTaskUserRepo.findAll()
                var projectionFound: ProjectTasksUserProjection? = null
                for (ptu in projectTasksUserProjections){
                    for (task in ptu.tasks!!) {
                        if (task.taskId.equals(event.taskId)) {
                            projectionFound = ptu
                            task.statusId = event.statusId
                        }
                    }
                }
                if (projectionFound == null) {
                    throw Exception()
                }
                projectTaskUserRepo.save(projectionFound);
            }
        }
    }
}

@Document("project-task-user-projection")
data class ProjectTasksUserProjection(
        var projectId: UUID,
        var createdAt: Long,
        var updatedAt: Long,
        var projectTitle: String,
        var creatorId: String,
        var projectStatuses: HashMap<UUID, StatusEntity>?,
        var tasks: ArrayList<TaskEntity>?,
        var users: ArrayList<String>
)

data class TaskEntity(
    var taskId: UUID,
    var projectId: UUID,
    var statusId: UUID,
    var title: String,
    var createdAt: Long)
@Repository
interface ProjectTaskUserRepo : MongoRepository<ProjectTasksUserProjection, UUID> {
    fun findByProjectId(projectId: UUID) : ProjectTasksUserProjection?;
}