package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(
        val tasksEsService: EventSourcingService<UUID, TaskAndStatusAggregate, TaskAndStatusAggregateState>
) {
    @PostMapping("")
    fun createTask(@RequestBody body: CreateTaskDto): TaskCreatedEvent {
        return tasksEsService.create { it.createTask(UUID.randomUUID(), body.projectId, body.name, body.description) }
    }

    @GetMapping("/{taskId}")
    fun getTask(@PathVariable taskId: UUID): TaskAndStatusAggregateState? {
        return tasksEsService.getState(taskId)
    }

    @PatchMapping("/{taskId}")
    fun updateTask(@PathVariable taskId: UUID, @RequestBody body: UpdateTaskDto): TaskUpdatedEvent {
        return tasksEsService.update(taskId) { it.updateTask(body.name, body.description) }
    }

    @PostMapping("/{taskId}/executors")
    fun addExecutor(@PathVariable taskId: UUID, @RequestBody body: AddExecutorDto): ExecutorAddedEvent {
        return tasksEsService.update(taskId) { it.addExecutor(body.userId) }
    }

    @PostMapping("/{taskId}/statuses/{statusId}")
    fun assignStatus(@PathVariable taskId: UUID, @PathVariable statusId: UUID): StatusAssignedToTaskEvent {
        return tasksEsService.update(taskId) { it.assignStatus(statusId) }
    }


    @DeleteMapping("/{taskId}/statuses")
    fun removeStatus(@PathVariable taskId: UUID): StatusRemovedFromTaskEvent {
        return tasksEsService.update(taskId) { it.removeStatus() }
    }
}

data class CreateTaskDto(
        val projectId: UUID,
        val name: String,
        val description: String
)

data class UpdateTaskDto(
        val name: String,
        val description: String
)

data class AddExecutorDto(
        val userId: UUID
)
