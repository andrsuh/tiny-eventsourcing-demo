package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.project.ProjectAggregate
import ru.quipy.api.project.StatusCreatedEvent
import ru.quipy.api.task.StatusAssignedToTaskEvent
import ru.quipy.api.task.TaskAggregate
import ru.quipy.api.task.TaskCreatedEvent
import ru.quipy.api.task.TaskRenamedEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class TaskStatusRelation(
        private val taskStatusProjectionRepo: TaskStatusProjectionRepo
) {

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TaskAggregate::class, "TaskAggregateSubscriberTSProjection") {

            `when`(StatusAssignedToTaskEvent::class) { event ->
                val status = taskStatusProjectionRepo.findByStatusIdAndStatusNameNotNull(event.statusId)
                        ?: throw Exception()
                val task = taskStatusProjectionRepo.findByTaskId(event.taskId) ?: throw Exception()
                task.statusId = event.statusId
                task.statusName = status.statusName
                task.taskUpdatedAt = System.currentTimeMillis();
                taskStatusProjectionRepo.save(task);
            }

            `when`(TaskRenamedEvent::class) { event ->
                val taskStatus = taskStatusProjectionRepo.findByTaskId(event.taskId) ?: throw Exception()
                taskStatus.taskTitle = event.title;
                taskStatus.taskUpdatedAt = System.currentTimeMillis();
                taskStatusProjectionRepo.save(taskStatus);
            }

            `when`(TaskCreatedEvent::class) { event ->
                taskStatusProjectionRepo.save(TaskStatusProjection(event.statusId, null, event.taskId, event.createdAt,
                        null, event.taskTitle, event.projectId, null))
            }
        }

        subscriptionsManager.createSubscriber(ProjectAggregate::class, "ProjectAggregateSubscriberTSProjection") {

            `when`(StatusCreatedEvent::class) { event ->
                taskStatusProjectionRepo.save(TaskStatusProjection(event.statusId, event.statusName, null, event.createdAt, null, null, event.projectId, null))
            }

        }
    }
}

@Document("task-status-projection")
data class TaskStatusProjection(
        var statusId: UUID?,
        var statusName: String?,
        var taskId: UUID?,
        var taskCreatedAt: Long?,
        var taskUpdatedAt: Long?,
        var taskTitle: String?,
        var projectId: UUID,
        var assignedUserId: UUID? = null
)

@Repository
interface TaskStatusProjectionRepo : MongoRepository<TaskStatusProjection, UUID> {
    fun findByTaskId(taskId: UUID): TaskStatusProjection?
    fun findByStatusIdAndStatusNameNotNull(statusId: UUID): TaskStatusProjection?
    fun findAllByTaskIdNotNull(): List<TaskStatusProjection>
}