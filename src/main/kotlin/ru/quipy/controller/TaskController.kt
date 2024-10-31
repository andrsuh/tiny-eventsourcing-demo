package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.StatusChangedEvent
import ru.quipy.api.TaskAggregate
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>,
    val taskAggregateStateCommands: TaskAggregateStateCommands
) {

    @PostMapping("/create/{projectId}")
    fun createTask(
        @PathVariable projectId: UUID, @RequestParam creatorId: UUID,
        @RequestParam executors: MutableList<UUID>,
        @RequestParam title: String
    ): TaskCreatedEvent {
        return taskEsService.create { taskAggregateStateCommands.createTask(projectId, title, executors, creatorId) }
    }

    @GetMapping("/{taskId}")
    fun getAccount(@PathVariable taskId: UUID): TaskAggregateState? {
        return taskEsService.getState(taskId)
    }

    @PostMapping("/changeStatus/{taskId}")
    fun changeStatus(@PathVariable taskId: UUID, @RequestParam statusId: UUID): StatusChangedEvent {
        return taskEsService.update(taskId) { taskAggregateState ->
            taskAggregateStateCommands.changeStatus(taskId, statusId, taskAggregateState.projectID)
        }
    }
}