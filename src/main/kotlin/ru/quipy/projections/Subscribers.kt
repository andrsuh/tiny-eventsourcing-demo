package ru.quipy.projections

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.ProjectStatusCreatedEvent
import ru.quipy.api.ProjectStatusRemovedEvent
import ru.quipy.api.ProjectTaskCreatedEvent
import ru.quipy.api.ProjectTaskModifiedEvent
import ru.quipy.api.ProjectUserAddedEvent
import ru.quipy.api.ProjectUserRemovedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Component
class UserEventsSubscriber(
    private val usersNamesRepository: UsersNamesRepository,
) {
    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "users-subscriber") {
            `when`(UserCreatedEvent::class) { event ->
                usersNamesRepository.save(
                    UserWithName(
                        userId = event.userId,
                        name = event.username
                    )
                )
            }
        }
    }
}

@Component
class ProjectsSubscriber(
    private val projectsRepository: ProjectsRepository,
) {
    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "project-subscriber") {
            `when`(ProjectCreatedEvent::class) { event ->
                projectsRepository.save(
                    Project(
                        projectId = event.projectId,
                        ownerId = event.ownerId,
                        name = event.projectName,
                        participants = mutableListOf(event.ownerId),
                        statuses = mutableListOf(event.default_status_id)
                    )
                )
            }
        }

        subscriptionsManager.createSubscriber(ProjectAggregate::class, "project-subscriber") {
            `when`(ProjectUserAddedEvent::class) { event ->
                val project = projectsRepository.findByProjectId(event.projectId)
                project.participants.add(event.userId)
                projectsRepository.save(project)
            }
        }

        subscriptionsManager.createSubscriber(ProjectAggregate::class, "project-subscriber") {
            `when`(ProjectUserRemovedEvent::class) { event ->
                val project = projectsRepository.findByProjectId(event.projectId)
                project.participants.remove(event.userId)
                projectsRepository.save(project)
            }
        }
    }
}

@Component
class TaskSubscriber(
    private val tasksRepository: TasksRepository,
    private val projectsRepository: ProjectsRepository,
    private val statusesRepository: StatusesRepository
) {
    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "task-subscriber") {
            `when`(ProjectTaskCreatedEvent::class) { event ->
                tasksRepository.save(
                    Task(
                        taskId = event.taskId,
                        projectId = event.projectId,
                        creatorId = event.userId,
                        status = statusesRepository.findByStatusId(
                            projectsRepository.findByProjectId(event.projectId).statuses[0]
                        ).name,
                        executors = mutableListOf(),
                        name = event.taskName
                    )
                )
            }
        }

        subscriptionsManager.createSubscriber(ProjectAggregate::class, "task-subscriber") {
            `when`(ProjectTaskModifiedEvent::class) { event ->
                val task = tasksRepository.findByTaskId(event.taskId)
                tasksRepository.save(
                    Task(
                        taskId = task.taskId,
                        projectId = task.projectId,
                        creatorId = task.creatorId,
                        status = event.statusId?.let { statusesRepository.findByStatusId(it).name } ?: task.status,
                        executors = event.executors?.toMutableList() ?: task.executors,
                        name = event.taskName ?: task.name
                    )
                )
            }
        }
    }
}

@Component
class StatusSubscriber(
    private val projectsRepository: ProjectsRepository,
    private val statusesRepository: StatusesRepository
) {

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "status-subscriber") {
            `when`(ProjectStatusCreatedEvent::class) { event ->
                statusesRepository.save(Status(
                    statusId = event.statusId,
                    name = event.statusName
                ))
                val project = projectsRepository.findByProjectId(event.projectId)
                project.statuses.add(event.statusId)
                projectsRepository.save(project)
            }
        }

        subscriptionsManager.createSubscriber(ProjectAggregate::class, "status-subscriber") {
            `when`(ProjectStatusRemovedEvent::class) { event ->
                statusesRepository.deleteById(event.statusId)
                val project = projectsRepository.findByProjectId(event.projectId)
                project.statuses.remove(event.statusId)
                projectsRepository.save(project)
            }
        }
    }
}
