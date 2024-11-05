package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.*

@Service
@AggregateSubscriber(
    aggregateClass = ProjectAggregate::class,
    subscriberName = "project-projection"
)
class ProjectProjection(
    val projectRepository: ProjectProjectionProjectRepository,
    val userMemberRepository: ProjectProjectionUserMemberRepository,
    val executorRepository: ProjectProjectionExecutorRepository,
    val taskRepository: ProjectProjectionTaskRepository,
    val statusRepository: ProjectProjectionStatusRepository
) {
    val logger: Logger = LoggerFactory.getLogger(ProjectProjection::class.java)
    @SubscribeEvent
    fun projectCreatedSubscriber(event: ProjectCreatedEvent) {
        val user = ProjectProjectionUserMember(event.creatorId, event.creatorMemberId)
        userMemberRepository.save(user)

        val defaultStatus = ProjectProjectionStatus(event.defaultStatusId, "CREATED", "White")
        statusRepository.save(defaultStatus)

        val project = ProjectProjectionProject(event.projectId, event.title, event.creatorId, mutableSetOf(event.creatorId), mutableSetOf(), mutableSetOf(event.defaultStatusId))
        projectRepository.save(project)

        logger.info("Project created: {}", event.projectId)
    }

    @SubscribeEvent
    fun userAddedToProjectSubscriber(event: UserAddedToProjectEvent) {
        val user = ProjectProjectionUserMember(event.userId, event.userMemberId)
        userMemberRepository.save(user)

        val project = projectRepository.findByIdOrNull(event.projectId)
        project!!.memberIds.add(event.userId)
        projectRepository.save(project)

        logger.info("User {} added to {} created", event.userId, event.projectId)
    }

    @SubscribeEvent
    fun taskCreatedSubscriber(event: TaskCreatedEvent) {
        val task = ProjectProjectionTask(event.taskId, event.taskName, event.statusId, mutableSetOf())
        taskRepository.save(task)

        val project = projectRepository.findByIdOrNull(event.projectId)
        project!!.taskIds.add(event.taskId)
        projectRepository.save(project)

        logger.info("Task created {}", event.taskId)
    }

    @SubscribeEvent
    fun userAssignedSubscriber(event: UserAssignedEvent) {
        val executor = ProjectProjectionExecutor(event.memberId, event.memberExecutorId)
        executorRepository.save(executor)

        val task = taskRepository.findByIdOrNull(event.taskId)
        task!!.execurorsIds.add(event.memberId)

        logger.info("User assigned {}", event.memberId)
    }

    @SubscribeEvent
    fun taskStatusChangedSubscriber(event: TaskStatusChangedEvent) {
        val task = taskRepository.findByIdOrNull(event.taskId)
        task!!.taskStatusId = event.statusId
        taskRepository.save(task)

        logger.info("Task {} changed status to {} ", event.taskId, event.statusId)
    }

    @SubscribeEvent
    fun taskStatusCreatedSubscriber(event: TaskStatusCreatedEvent) {
        val status = ProjectProjectionStatus(event.statusId, event.statusText, event.statusColor)
        statusRepository.save(status)

        val project = projectRepository.findByIdOrNull(event.projectId)
        project!!.taskStatusIds.add(event.statusId)
        projectRepository.save(project)


        logger.info("Status created {} ", event.statusText)
    }


    @SubscribeEvent
    fun taskTittleChangedSubscriber(event: TaskTittleChangedEvent) {
        val task = taskRepository.findByIdOrNull(event.taskId)
        task!!.title = event.newTittle
        taskRepository.save(task)

        logger.info("Task {} changed title {} ", event.taskId, event.newTittle)
    }

    @SubscribeEvent
    fun taskStatusDeletedSubscriber(event: TaskStatusDeletedEvent) {
        statusRepository.deleteById(event.statusId)

        val project = projectRepository.findByIdOrNull(event.projectId)
        project!!.taskIds.remove(event.statusId)
        projectRepository.save(project)

        logger.info("Status {} deleted", event.statusId)
    }
}

@Document("project-projection-project")
data class ProjectProjectionProject(
    @Id
    var projectId: UUID,
    var title: String,
    var creatorId: UUID,
    var memberIds: MutableSet<UUID>,
    var taskIds: MutableSet<UUID>,
    var taskStatusIds: MutableSet<UUID>
)

@Repository
interface ProjectProjectionProjectRepository : MongoRepository<ProjectProjectionProject, UUID>

@Document("project-projection-user-member")
data class ProjectProjectionUserMember(
    @Id
    var userId: UUID,
    var userMemberId:UUID
)

@Repository
interface ProjectProjectionUserMemberRepository : MongoRepository<ProjectProjectionUserMember, UUID>

@Document("project-projection-executors")
data class ProjectProjectionExecutor(
    @Id
    var memberId: UUID,
    var executorId: UUID
)

@Repository
interface ProjectProjectionExecutorRepository : MongoRepository<ProjectProjectionExecutor, UUID>

@Document("project-projection-task")
data class ProjectProjectionTask(
    @Id
    var taskId: UUID,
    var title: String,
    var taskStatusId: UUID,
    var execurorsIds: MutableSet<UUID>
)

@Repository
interface ProjectProjectionTaskRepository : MongoRepository<ProjectProjectionTask, UUID>

@Document("project-projection-status")
data class ProjectProjectionStatus(
    @Id
    var statusId: UUID,
    var title: String,
    var color: String,
)

@Repository
interface ProjectProjectionStatusRepository : MongoRepository<ProjectProjectionStatus, UUID>