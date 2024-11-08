package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.task.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.task.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(
        val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>
) {
    @PostMapping("/create")
    fun create(@RequestBody request: CreateTaskRequest): TaskCreatedEvent {
        val taskId = UUID.randomUUID()

        val deadline = LocalDateTime.parse(request.deadlineTimestamp, DateTimeFormatter.ISO_DATE_TIME)

        return taskEsService.create {
            it.createTask(
                    id = taskId,
                    title = request.title,
                    description = request.description,
                    projectId = request.projectId,
                    deadlineTimestamp = deadline,
                    statusId = request.statusId
            )
        }
    }

    @PostMapping("/updateTitle")
    fun updateTitle(@RequestBody request: UpdateTaskTitleRequest): TaskTitleUpdatedEvent {
        return taskEsService.update(request.taskId) {
            it.updateTitle(request.taskId, request.newTitle)
        }
    }

    @PostMapping("/updateDescription")
    fun updateDescription(@RequestBody request: UpdateTaskDescriptionRequest): TaskDescriptionUpdatedEvent {
        return taskEsService.update(request.taskId) {
            it.updateDescription(request.taskId, request.newDescription)
        }
    }

    @PostMapping("/updateStatus")
    fun updateStatus(@RequestBody request: UpdateTaskStatusRequest): TaskStatusUpdatedEvent {
        return taskEsService.update(request.taskId) {
            it.updateStatus(request.taskId, request.newStatusId)
        }
    }
}

data class CreateTaskRequest(
        val title: String,
        val description: String,
        val projectId: UUID,
        val deadlineTimestamp: String,
        val statusId: UUID
)

data class UpdateTaskTitleRequest(
        val taskId: UUID,
        val newTitle: String
)

data class UpdateTaskDescriptionRequest(
        val taskId: UUID,
        val newDescription: String
)

data class UpdateTaskStatusRequest(
        val taskId: UUID,
        val newStatusId: UUID
)