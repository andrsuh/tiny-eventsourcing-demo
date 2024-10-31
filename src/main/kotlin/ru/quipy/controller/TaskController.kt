package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.controller.requests.CreateTaskRequest
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>
) {

    @PostMapping("/task")
    fun createProject(@RequestBody request: CreateTaskRequest): TaskCreatedEvent {
        return taskEsService.create {
            it.create(
                projectId = request.projectId,
                taskId = UUID.randomUUID(),
                taskTitle = request.taskTitle,
                executorId = request.executorId,
                statusId = request.statusId
            )
        }
    }

    @PostMapping("/{taskId}")
    fun addExecutors(@PathVariable taskId: UUID, @RequestParam executorsIds: Set<UUID>): ExecutorsAddedEvent {
        return taskEsService.update(aggregateId = taskId) {
            it.addExecutors(executorsIds)
        }
    }

    @PutMapping("/{taskId}")
    fun editTask(@PathVariable taskId: UUID, @RequestParam title: String): TaskEditedEvent {
        return taskEsService.update(aggregateId = taskId) {
            it.edit(title = title)
        }
    }

    @PostMapping("/{taskId}/status")
    fun assignStatus(@PathVariable taskId: UUID, @RequestParam statusId: UUID): TaskStatusAssignedEvent {
        return taskEsService.update(taskId) {
            it.assignStatus(statusId = statusId)
        }
    }
}