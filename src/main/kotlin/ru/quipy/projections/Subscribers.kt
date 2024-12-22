package ru.quipy.projections

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.quipy.api.*
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Component
class StatusEventsSubscriber(
        private val statusRepository: StatusRepository,
) {
    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "status-created-subscriber") {
            `when`(StatusAddedEvent::class) { event ->
                statusRepository.save(
                        Status(
                                id = event.id,
                                name = event.name,
                                projectId = event.projectId,
                                colorRed = event.color.red,
                                colorGreen = event.color.green,
                                colorBlue = event.color.blue
                        )
                )
            }
        }

        subscriptionsManager.createSubscriber(ProjectAggregate::class, "status-removed-subscriber") {
            `when`(StatusRemovedEvent::class) { event ->
                statusRepository.deleteById(event.statusId)
            }
        }

        subscriptionsManager.createSubscriber(ProjectAggregate::class, "default-status-created-subscriber") {
            `when`(ProjectCreatedEvent::class) { event ->
                statusRepository.save(
                        Status(
                                id = event.id,
                                name = event.name,
                                projectId = event.projectId,
                                colorRed = event.defaultStatus.color.red,
                                colorGreen = event.defaultStatus.color.green,
                                colorBlue = event.defaultStatus.color.blue
                        )
                )
            }
        }
    }
}

@Component
class TaskEventsSubscriber(
        private val taskRepository: TaskRepository,
) {
    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "task-created-subscriber") {
            `when`(TaskCreatedEvent::class) { event ->
                taskRepository.save(
                        Task(
                                id = event.taskId,
                                taskName = event.taskName,
                                status = event.status,
                                projectID = event.projectId,
                                taskAssignees = mutableSetOf()
                        )
                )
            }
        }

        subscriptionsManager.createSubscriber(ProjectAggregate::class, "task-status-changed-subscriber") {
            `when`(TaskStatusChangedEvent::class) { event ->
                val task = taskRepository.findById(event.taskId).get()
                task.status = event.newStatusId
                taskRepository.save(task)
            }
        }

        subscriptionsManager.createSubscriber(ProjectAggregate::class, "task-changed-subscriber") {
            `when`(TaskChangedEvent::class) { event ->
                val task = taskRepository.findById(event.taskId).get()
                task.taskName = event.newName
                taskRepository.save(task)
            }
        }

        subscriptionsManager.createSubscriber(ProjectAggregate::class, "task-assignee-added-subscriber") {
            `when`(AssigneeAddedEvent::class) { event ->
                val task = taskRepository.findById(event.taskId).get()
                task.taskAssignees += event.assigneeId
                taskRepository.save(task)
            }
        }
    }
}

@Component
class UsersAndProjectsSubscriber(
        private val projectRepository: ProjectRepository,
        private val userRepository: UserRepository
) {
    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "user-created-subscriber") {
            `when`(UserCreatedEvent::class) { event ->
                userRepository.save(
                        User(
                                id = event.userId,
                                name = event.userName
                        )
                )
            }
        }

        subscriptionsManager.createSubscriber(ProjectAggregate::class, "project-created-subscriber") {
            `when`(ProjectCreatedEvent::class) { event ->
                projectRepository.save(
                        Project(
                                id = event.projectId,
                                name = event.title,
                                members = mutableSetOf(event.creatorId)
                        )
                )
            }
        }

        subscriptionsManager.createSubscriber(ProjectAggregate::class, "user-added-in-project-subscriber") {
            `when`(UserAddedInProjectEvent::class) { event ->
                val project = projectRepository.findById(event.projectId).get()
                project.members += event.userId
                projectRepository.save(project)
            }
        }
    }
}