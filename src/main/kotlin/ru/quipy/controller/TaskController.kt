package ru.quipy.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.project.*
import ru.quipy.api.task.*
import ru.quipy.api.task.TaskCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.project.*
import ru.quipy.logic.task.*
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>
) {

    @PostMapping("/{projectId}")
    fun createTask(@PathVariable projectId: UUID, @RequestParam title: String) : TaskCreatedEvent {
        return taskEsService.create { it.create(title, projectId) }
    }

    @GetMapping("/{taskId}")
    fun getTask(@PathVariable taskId: UUID) : TaskAggregateState? {
        return taskEsService.getState(taskId)
    }

    @PostMapping("/{taskId}/title")
    fun changeTaskTitle(@PathVariable taskId: UUID, @RequestParam title: String) : ChangeTasksTitleEvent {
        return taskEsService.update(taskId) { it.changeTitle(title) }
    }

    @PostMapping("/{taskId}/executor")
    fun addExecutor(@PathVariable taskId: UUID, @RequestParam executor: String) : AddTaskExecutorEvent {
        return taskEsService.update(taskId) { it.addExecutor(executor) }
    }

    @DeleteMapping("/{taskId}/executor")
    fun delExecutor(@PathVariable taskId: UUID, @RequestParam executor: String) : DelTaskExecutorEvent {
        return taskEsService.update(taskId) { it.delExecutor(executor) }
    }

    @PostMapping("/{taskId}/status")
    fun changeStatus(@PathVariable taskId: UUID, @RequestParam status: String) : ChangeTaskStatusEvent {
        return taskEsService.update(taskId) { it.changeStatus(status) }
    }

    @DeleteMapping("/{taskId}")
    fun delTask(@PathVariable taskId: UUID) : DeleteTaskEvent {
        return taskEsService.update(taskId) { it.deleteTask() }
    }

}