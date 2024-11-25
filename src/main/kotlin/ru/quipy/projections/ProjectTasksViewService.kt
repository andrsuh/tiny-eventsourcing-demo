package ru.quipy.projections

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.projections.entities.ProjectTaskEntity
import ru.quipy.projections.repositories.ProjectTaskRepository
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
@AggregateSubscriber(aggregateClass = ProjectAggregate::class, subscriberName = "project-tasks-subscriber")
class ProjectTasksViewService {

    @Autowired
    lateinit var projectTasksRepository: ProjectTaskRepository

    @SubscribeEvent
    fun onTaskAdded(event: TaskCreatedEvent) {
        val task = ProjectTaskEntity(
            projectId = event.projectId,
            taskId = event.taskId,
            description = event.description,
            name = event.taskName,
        )
        projectTasksRepository.save(task)
    }

    @SubscribeEvent
    fun onTaskNameChangedEvent(event: TaskNameChangedEvent) {
        val existingTask = projectTasksRepository.findById(event.taskId)
        if (existingTask.isPresent) {
            val task = existingTask.get()
            task.name = event.newName
            projectTasksRepository.save(task)
        } else {
            throw EntityNotFoundException("Task with ID ${event.taskId} not found")
        }
    }


    @SubscribeEvent
    fun onTaskDeletedEvent(event: TaskDeletedEvent) {
        val existingTask = projectTasksRepository.findById(event.taskId)
        if (existingTask.isPresent) {
            projectTasksRepository.delete(existingTask.get())
        } else {
            throw EntityNotFoundException("Task with ID ${event.taskId} not found")
        }
    }

    @SubscribeEvent
    fun onTaskAssigneeAddedEvent(event: TaskAssigneeAddedEvent) {
        val existingTask = projectTasksRepository.findById(event.taskId)
        if (existingTask.isPresent) {
            val task = existingTask.get()
            task.assignees.add(event.participantId)
            projectTasksRepository.save(task)
        } else {
            throw EntityNotFoundException("Task with ID ${event.taskId} not found")
        }
    }

    fun getTasks(projectId: UUID): List<UUID> {
        return projectTasksRepository.findTasksIdsByProjectId(projectId)
    }
}
