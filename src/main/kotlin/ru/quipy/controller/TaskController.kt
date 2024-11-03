package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.dto.taskandstatus.AddExecutorDto
import ru.quipy.dto.taskandstatus.CreateStatusDto
import ru.quipy.dto.taskandstatus.CreateTaskDto
import ru.quipy.dto.taskandstatus.UpdateTaskDto
import ru.quipy.entity.StatusEntity
import ru.quipy.entity.TaskEntity
import ru.quipy.enum.ColorEnum
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
            @RequestParam name: String,
            @RequestParam description: String,
            @RequestParam statusId: UUID,
    ): TaskCreatedEvent {
        val project = projectEsService.getState(projectId)
                ?: throw NullPointerException("Project $projectId was not found.")

        return tasksEsService.update(project.getId()) { it.createTask(UUID.randomUUID(), name, description, projectId, statusId) }
    }

    @GetMapping("/project/{projectId}/tasks-and-statuses")
    fun getTaskStatusesAndTasks(@PathVariable projectId: UUID): TaskAndStatusAggregateState? {
        val project = projectEsService.getState(projectId)
                ?: throw NullPointerException("Project $projectId was not found.")

        return tasksEsService.getState(project.getId())
    }

    @GetMapping("/project/{projectId}/task/{taskId}")
    fun getTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID): TaskEntity? {
        val project = projectEsService.getState(projectId)
                ?: throw NullPointerException("Project $projectId was not found.")

        return tasksEsService.getState(project.getId())?.getTaskById(taskId)
    }

    @PatchMapping("/project/{projectId}/task/{taskId}")
    fun updateTask(
            @PathVariable projectId: UUID,
            @PathVariable taskId: UUID,
            @RequestParam name: String,
            @RequestParam description: String,
    ): TaskUpdatedEvent {
        val project = projectEsService.getState(projectId)
                ?: throw NullPointerException("Project $projectId was not found.")

        return tasksEsService.update(project.getId()) { it.updateTask(taskId, name, description) }
    }

    @PostMapping("project/{projectId}/task/{taskId}/executors")
    fun addExecutor(
            @PathVariable projectId: UUID,
            @PathVariable taskId: UUID,
            @RequestParam userId: UUID
    ): ExecutorAddedEvent {
        val project = projectEsService.getState(projectId)
                ?: throw NullPointerException("Project $projectId was not found.")

        if (project.getParticipantById(userId) == null)
            throw NullPointerException("Project participant $userId was not found.")

        return tasksEsService.update(taskId) { it.addExecutor(taskId, userId) }
    }

    @PostMapping("/{projectId}/statuses")
    fun createStatus(
            @PathVariable projectId: UUID,
            @RequestParam statusName: String,
            @RequestParam color: String,
    ): StatusCreatedEvent {
        val project = projectEsService.getState(projectId)
                ?: throw NullPointerException("Project $projectId was not found.")

        if (tasksEsService.getState(project.getId()) == null)
            throw NullPointerException("Tasks and statuses aggregate $projectId does not exist.")

        return projectEsService.update(projectId) {
            it.createStatus(
                    UUID.randomUUID(),
                    statusName,
                    projectId,
                    ColorEnum.valueOf(color)
            )
        }
    }

    @GetMapping("project/{projectId}/statuses/{id}")
    fun getTaskStatus(@PathVariable projectId: UUID, @PathVariable id: UUID): StatusEntity? {
        return tasksEsService.getState(projectId)?.getStatusById(id)
    }

    @PatchMapping("project/{projectId}/task/{taskId}/change-status")
    fun changeStatus(
            @PathVariable projectId: UUID,
            @PathVariable taskId: UUID,
            @RequestParam statusId: UUID,
    ): TaskStatusChangedEvent {
        val project = projectEsService.getState(projectId)
                ?: throw NullPointerException("Project $projectId was not found.")

        return tasksEsService.update(project.getId()) { it.changeStatus(taskId, statusId) }
    }

    @DeleteMapping("/{projectId}/statuses/{statusId}")
    fun deleteStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID): StatusDeletedEvent {
        val project = projectEsService.getState(projectId)
                ?: throw NullPointerException("Project $projectId was not found.")

        return projectEsService.update(project.getId()) { it.deleteStatus(statusId) }
    }

    @PatchMapping("/project/{projectId}/status/{statusId}/position")
    fun changeTaskStatusPosition(
            @PathVariable projectId: UUID,
            @PathVariable statusId: UUID,
            @RequestParam position: Int,
    ): StatusPositionChangedEvent {
        val project = projectEsService.getState(projectId)
                ?: throw NullPointerException("Project $projectId was not found.")

        return tasksEsService.update(project.getId()) {
            it.changeTaskStatusPosition(statusId, position)
        }
    }
}

