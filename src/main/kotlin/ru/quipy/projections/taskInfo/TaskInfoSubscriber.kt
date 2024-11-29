package ru.quipy.projections.taskInfo

import org.springframework.stereotype.Service
import ru.quipy.api.PerformerAssignedToTaskEvent
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskUpdatedEvent
import ru.quipy.projections.entity.TaskInfoProjection
import ru.quipy.projections.repository.TaskInfoRepository
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent


@Service
@AggregateSubscriber(aggregateClass = ProjectAggregate::class,  subscriberName = "tasks-info-subscriber")
class TaskInfoSubscriber (
    private val taskInfoRepository: TaskInfoRepository,
) {
    @SubscribeEvent
    fun onTaskCreated(event: TaskCreatedEvent) {
        val task = taskInfoRepository.findById(event.taskId).orElse(null)
        if (task == null) {
            val infoProjection = TaskInfoProjection(
                taskId = event.taskId,
                taskName = event.taskName,
                taskDescription = event.taskDescription,
            )
            taskInfoRepository.save(infoProjection)
        }
    }

    @SubscribeEvent
    fun onTaskUpdated(event: TaskUpdatedEvent) {
        val task = taskInfoRepository.findById(event.taskId).orElse(null)
        if (task != null) {
            task.taskId = event.taskId
            task.taskName = event.taskName
            task.taskDescription = event.taskDescription
            taskInfoRepository.save(task)
        }
    }

        @SubscribeEvent
    fun onPerformerAssignedToTask(event: PerformerAssignedToTaskEvent) {
        val task = taskInfoRepository.findById(event.projectId).orElse(null)
        if (task != null) {
            if (!task.performers.contains(event.userId)) {
                    task.performers.add(event.userId)
                taskInfoRepository.save(task)
            }
        }
    }
}