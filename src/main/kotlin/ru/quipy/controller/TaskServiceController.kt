package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.StatusCreatedEvent
import ru.quipy.api.TaskAssigneeAddedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskServiceAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.TaskServiceAggregateState
import java.util.*

@RestController
@RequestMapping("/task-service")
class TaskServiceController(
    val taskServiceEsService: EventSourcingService<UUID, TaskServiceAggregate, TaskServiceAggregateState>
) {

    @PostMapping("/create-status")
    fun createStatus(@RequestBody dto: CreateStatusDto) : StatusCreatedEvent {
        return taskServiceEsService.update(dto.projectId) {
            it.createStatus(dto.name, dto.color, dto.projectId, dto.participantId)
        }
    }

    @PostMapping("/create-task")
    fun createTask(@RequestBody dto: CreateTaskDto) : TaskCreatedEvent {
        return taskServiceEsService.update(dto.projectId) {
            it.createTask(UUID.randomUUID(), dto.name, dto.description, dto.projectId, dto.participantId)
        }
    }

    @GetMapping("/projects/{projectId}")
    fun getBoardManager(@PathVariable projectId: UUID) : TaskServiceAggregateState? {
        return taskServiceEsService.getState(projectId)
    }

    @PostMapping("/projects/{projectId}/add-task-assignee")
    fun addTaskAssignee(@PathVariable projectId: UUID, @RequestParam taskId: UUID, @RequestParam participantId: UUID) : TaskAssigneeAddedEvent {
        return taskServiceEsService.update(projectId) {
            it.addTaskAssignee(taskId, participantId)
        }
    }
}

data class CreateTaskDto(
    val name: String,
    val description: String,
    val projectId: UUID,
    val participantId: UUID
)

data class CreateStatusDto(
    val name: String,
    val color: String,
    val projectId: UUID,
    val participantId: UUID
)