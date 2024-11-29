package ru.quipy.projections.statusesWithTasks

import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.projections.entity.StatusesWithTasksProjection
import ru.quipy.projections.entity.StatusesProjection
import ru.quipy.projections.entity.TasksProjection
import ru.quipy.projections.repository.StatusesWithTasksRepository
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(aggregateClass = ProjectAggregate::class,  subscriberName = "statuses-tasks-subscriber")
class StatusesWithTasksSubscriber (
    private val statusesWithTasksRepository: StatusesWithTasksRepository,
) {

    @SubscribeEvent
    fun onProjectCreated(event: ProjectCreatedEvent) {
        val projectProjection = StatusesWithTasksProjection(
            projectId = event.projectId,
        )
        val baseStatus = StatusesProjection(statusId = event.baseStatusId, statusName = "Created", statusOrder = 0)
        projectProjection.statuses.add(baseStatus)
        statusesWithTasksRepository.save(projectProjection)
    }

    @SubscribeEvent
    fun onTaskCreated(event: TaskCreatedEvent) {
        val project = statusesWithTasksRepository.findById(event.projectId).orElse(null)
        if (project != null) {
            val task = TasksProjection(
                taskId = event.taskId,
                taskName = event.taskName
            )
            project.statuses.first().tasks.add(task)
            statusesWithTasksRepository.save(project)
        }
    }

    @SubscribeEvent
    fun onTaskUpdated(event: TaskUpdatedEvent) {
        val project = statusesWithTasksRepository.findById(event.projectId).orElse(null)
        if (project != null) {
            for (status in project.statuses) {
                val task = status.tasks.find {it.taskId == event.taskId }
                if (task != null) {
                    task.taskName = event.taskName
                    statusesWithTasksRepository.save(project)
                    return
                }
            }
        }
    }

    @SubscribeEvent
    fun onStatusCreated(event: StatusCreatedEvent) {
        val project = statusesWithTasksRepository.findById(event.projectId).orElse(null)
        if (project != null) {
            val status = StatusesProjection(
                statusId = event.statusId,
                statusName = event.statusName,
                statusOrder = event.order
            )
            project.statuses.add(status)
            statusesWithTasksRepository.save(project)
        }
    }

    @SubscribeEvent
    fun onStatusDeleted(event: StatusDeletedEvent) {
        val project = statusesWithTasksRepository.findById(event.projectId).orElse(null)
        if (project != null) {
            val removed = project.statuses.removeIf { it.statusId == event.statusId}
            if (removed) {
                statusesWithTasksRepository.save(project)
            }
        }
    }

    @SubscribeEvent
    fun onStatusAssignedToTask(event: StatusAssignedToTaskEvent) {
        val project = statusesWithTasksRepository.findById(event.projectId).orElse(null)
        if (project != null) {
            var assignedTask : TasksProjection? = null
            for (status in project.statuses) {
                val task = status.tasks.find { it.taskId == event.taskId }
                if (task != null) {
                    status.tasks.remove(task)
                    assignedTask = task
                    break
                }
            }
            if (assignedTask != null) {
                var status = project.statuses.find { it.statusId == event.statusId }
                if (status != null) {
                    status.tasks.add(assignedTask)
                    statusesWithTasksRepository.save(project)
                }
            }
        }
    }

    @SubscribeEvent
    fun onStatusOrderChanged(event: StatusOrderChangedEvent) {
        val project = statusesWithTasksRepository.findById(event.projectId).orElse(null)
        if (project != null) {
            event.order.forEach { (statusId, newOrder) ->
                val status = project.statuses.find { it.statusId == statusId }
                if (status != null) {
                    status.statusOrder = newOrder
                }
            }
            statusesWithTasksRepository.save(project)
        }
    }
}