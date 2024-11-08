package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.task.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.task.*
import java.util.*


@RestController
@RequestMapping("/tasks")
class TaskController (
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>
){
    @PostMapping("/{taskId}")
    fun renameTask(@PathVariable taskId: UUID, @RequestParam title: String) : TaskRenamedEvent {
        return taskEsService.update(taskId) {
            it.renameTask(title)
        }
    }

    @PostMapping("/{taskId}/{userId}")
    fun assignUserToTask(@PathVariable taskId: UUID, @PathVariable userId: UUID) : UserAssignedToTaskEvent {
        return taskEsService.update(taskId) {
            it.assignUserToTask(userId)
        }
    }

    @PostMapping("/create")
    fun createTask(
        @RequestParam projectId: UUID,
        @RequestParam statusId: UUID,
        @RequestParam title: String
    ) : TaskCreatedEvent{
        return taskEsService.create {
            it.create(
                id = UUID.randomUUID(),
                projectId = projectId,
                statusId = statusId,
                taskTitle = title,
            )
        }
    }

    @GetMapping("/{taskId}")
    fun getTask(@PathVariable taskId: UUID) : TaskAggregateState? {
        return taskEsService.getState(taskId)
    }

    @PostMapping("/assignStatus")
    fun assignStatusToTask(@RequestParam taskId: UUID, @RequestParam statusId: UUID) : StatusAssignedToTaskEvent {
        return taskEsService.update(taskId) {
            it.assignStatusToTask(statusId)
        }
    }
}