package ru.quipy.projections

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.projections.dto.UserDto
import ru.quipy.projections.entities.ProjectTaskEntity
import ru.quipy.projections.repositories.ProjectTaskRepository
import ru.quipy.projections.repositories.UserRepository
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
@AggregateSubscriber(aggregateClass = ProjectAggregate::class, subscriberName = "project-tasks-subscriber")
class ProjectTasksViewService {

    @Autowired
    lateinit var projectTasksRepository: ProjectTaskRepository
    @Autowired
    lateinit var usersRepository: UserRepository

    @SubscribeEvent
    fun onTaskAdded(event: TaskCreatedEvent) {
        val task = ProjectTaskEntity(
            projectId = event.projectId,
            taskId = event.taskId,
            description = event.description,
            name = event.taskName,
            statusName = event.statusName
        )
        projectTasksRepository.save(task)
    }

    @SubscribeEvent
    fun onTaskNameChangedEvent(event: TaskNameChangedEvent) {
        val existingTask = projectTasksRepository.findByTaskId(event.taskId)
        if (existingTask.isPresent) {
            val task = existingTask.get()
            task.name = event.newName
            projectTasksRepository.save(task)
        } else {
            throw EntityNotFoundException("Task with ID ${event.taskId} not found")
        }
    }

    @SubscribeEvent
    fun onTaskStatusChangedEvent(event: TaskStatusChangedEvent) {
        val existingTask = projectTasksRepository.findByTaskId(event.taskId)
        if (existingTask.isPresent) {
            val task = existingTask.get()
            task.statusName = event.newStatusName
            projectTasksRepository.save(task)
        } else {
            throw EntityNotFoundException("Task with ID ${event.taskId} not found")
        }
    }


    @SubscribeEvent
    fun onTaskDeletedEvent(event: TaskDeletedEvent) {
        val existingTask = projectTasksRepository.findByTaskId(event.taskId)
        if (existingTask.isPresent) {
            projectTasksRepository.delete(existingTask.get())
        } else {
            throw EntityNotFoundException("Task with ID ${event.taskId} not found")
        }
    }

    @SubscribeEvent
    fun onTaskAssigneeAddedEvent(event: TaskAssigneeAddedEvent) {
        val existingTask = projectTasksRepository.findByTaskId(event.taskId)
        if (existingTask.isPresent) {
            val task = existingTask.get()
            task.assignees.add(event.participantId)
            projectTasksRepository.save(task)
        } else {
            throw EntityNotFoundException("Task with ID ${event.taskId} not found")
        }
    }

    fun getTasks(projectId: UUID): List<ProjectTaskDto> {
        val tasks = projectTasksRepository.findTasksIdsByProjectId(projectId)
        val tasksUsers = mutableListOf<ProjectTaskDto>()
        for (task in tasks) {
            val users = task.assignees.map { userId ->
                val user = usersRepository.findByUserId(userId)
                UserDto(userId = userId, name = user?.name, nickname = user?.nickname)
            }
            tasksUsers.add(ProjectTaskDto(
                taskId = task.taskId,
                projectId = task.projectId,
                name = task.name,
                statusName = task.statusName,
                description = task.description,
                assignees = users))
        }
        return tasksUsers
    }
}

data class ProjectTaskDto (
    val taskId: UUID,
    val projectId: UUID,
    val name: String,
    val description: String,
    val assignees: List<UserDto>,
    val statusName: String
)