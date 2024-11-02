package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.dto.taskandstatus.AddExecutorDto
import ru.quipy.dto.taskandstatus.CreateStatusDto
import ru.quipy.dto.taskandstatus.CreateTaskDto
import ru.quipy.dto.taskandstatus.UpdateTaskDto
import ru.quipy.logic.*
import ru.quipy.logic.command.*
import ru.quipy.logic.state.ProjectAggregateState
import ru.quipy.logic.state.TaskAndStatusAggregateState
import java.util.*

@RestController
class TaskController(
        val tasksEsService: EventSourcingService<UUID, TaskAndStatusAggregate, TaskAndStatusAggregateState>,
        val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
) {
    @PostMapping("/project/{projectId}/task/")
    fun createTask(
            @PathVariable projectId: UUID,
            @RequestBody body: CreateTaskDto
    ): TaskCreatedEvent {
        val project = projectEsService.getState(projectId)
                ?: throw NullPointerException("Project $projectId was not found.")

        return tasksEsService.create { it.createTask(UUID.randomUUID(), projectId, body.name, body.description) }
    }

    @GetMapping("/project/{projectId}/tasks-and-statuses")
    fun getTaskStatusesAndTasks(@PathVariable projectId: UUID) : TaskAndStatusAggregateState? {
        val project = projectEsService.getState(projectId)
                ?: throw NullPointerException("Project $projectId was not found.")

        return tasksEsService.getState(project.getTaskAndStatusAggregateId())
    }

    @PatchMapping("/project/{projectId}/task/{taskId}")
    fun updateTask(
            @PathVariable projectId: UUID,
            @PathVariable taskId: UUID,
            @RequestBody body: UpdateTaskDto
    ): TaskUpdatedEvent {
        val project = projectEsService.getState(projectId)
                ?: throw NullPointerException("Project $projectId was not found.")

        return tasksEsService.update(taskId) { it.updateTask(body.name, body.description) }
    }

    @PostMapping("/{taskId}/executors")
    fun addExecutor(@PathVariable taskId: UUID, @RequestBody body: AddExecutorDto): ExecutorAddedEvent {
        return tasksEsService.update(taskId) { it.addExecutor(body.userId) }
    }

//    @PostMapping("/{taskId}/statuses/{statusId}")
//    fun assignStatus(@PathVariable taskId: UUID, @PathVariable statusId: UUID): StatusAssignedToTaskEvent {
//        return tasksEsService.update(taskId) { it.assignStatus(statusId) }
//    }
//
//
//    @DeleteMapping("/{taskId}/statuses")
//    fun removeStatus(@PathVariable taskId: UUID): StatusRemovedFromTaskEvent {
//        return tasksEsService.update(taskId) { it.removeStatus() }
//    }

    @PostMapping("/{projectId}/statuses")
    fun createStatus(@PathVariable projectId: UUID, @RequestBody body: CreateStatusDto) : StatusCreatedEvent {
        return projectEsService.update(projectId) { it.createStatus(UUID.randomUUID(), body.statusName, body.color) }
    }

    @DeleteMapping("/{projectId}/statuses/{statusId}")
    fun deleteStatus(@PathVariable projectId: UUID, @PathVariable statusId:UUID) : StatusDeletedEvent{
        return projectEsService.update(projectId) { it.deleteStatus(statusId) }
    }
}

