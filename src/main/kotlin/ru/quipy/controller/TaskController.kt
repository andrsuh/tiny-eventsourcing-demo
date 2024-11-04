package ru.quipy.controller

import javassist.NotFoundException
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.DeleteMapping
import ru.quipy.api.TaskAndStatusAggregate
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskUpdatedEvent
import ru.quipy.api.StatusDeletedEvent
import ru.quipy.api.StatusCreatedEvent
import ru.quipy.api.StatusPositionChangedEvent
import ru.quipy.api.TaskStatusChangedEvent
import ru.quipy.api.ExecutorAddedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.entity.StatusEntity
import ru.quipy.entity.TaskEntity
import ru.quipy.enum.ColorEnum
import ru.quipy.logic.command.createTask
import ru.quipy.logic.command.updateTask
import ru.quipy.logic.command.addExecutor
import ru.quipy.logic.command.createStatus
import ru.quipy.logic.command.changeStatus
import ru.quipy.logic.command.changeTaskStatusPosition
import ru.quipy.logic.command.deleteStatus
import ru.quipy.logic.state.ProjectAggregateState
import ru.quipy.logic.state.TaskAndStatusAggregateState
import java.util.UUID

@RestController
class TaskController(
        val tasksEsService: EventSourcingService<UUID, TaskAndStatusAggregate, TaskAndStatusAggregateState>,
        val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
) {
    @PostMapping("/projects/{projectId}/task") //f3bc6e46-bd89-48cb-bc29-ef305c6559dc
    fun createTask(
            @PathVariable projectId: UUID,
            @RequestParam name: String,
            @RequestParam description: String,
            @RequestParam statusId: UUID,
    ): TaskCreatedEvent {
        val project = projectEsService.getState(projectId)
                ?: throw NotFoundException("Project $projectId was not found.")

        return tasksEsService.update(project.getId()) { it.createTask(UUID.randomUUID(), name, description, projectId, statusId) }
    }

    @GetMapping("/projects/{projectId}/tasks-and-statuses")
    fun getTaskStatusesAndTasks(@PathVariable projectId: UUID): TaskAndStatusAggregateState? {
        val project = projectEsService.getState(projectId)
                ?: throw NotFoundException("Project $projectId was not found.")

        return tasksEsService.getState(project.getId())
    }

    @GetMapping("/projects/{projectId}/task/{taskId}")
    fun getTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID): TaskEntity? {
        val project = projectEsService.getState(projectId)
                ?: throw NotFoundException("Project $projectId was not found.")

        return tasksEsService.getState(project.getId())?.getTaskById(taskId)
    }

    @PatchMapping("/projects/{projectId}/task/{taskId}")
    fun updateTask(
            @PathVariable projectId: UUID,
            @PathVariable taskId: UUID,
            @RequestParam name: String,
            @RequestParam description: String,
    ): TaskUpdatedEvent {
        val project = projectEsService.getState(projectId)
                ?: throw NotFoundException("Project $projectId was not found.")

        return tasksEsService.update(project.getId()) { it.updateTask(taskId, name, description) }
    }

    @PostMapping("projects/{projectId}/task/{taskId}/executors")
    fun addExecutor(
            @PathVariable projectId: UUID,
            @PathVariable taskId: UUID,
            @RequestParam userId: UUID,
    ): ExecutorAddedEvent {
        val project = projectEsService.getState(projectId)
                ?: throw NotFoundException("Project $projectId was not found.")

        if (project.getParticipantById(userId) == null)
            throw NotFoundException("Project participant $userId was not found.")

        return tasksEsService.update(projectId) { it.addExecutor(taskId, userId) }
    }

    @PostMapping("projects/{projectId}/statuses")
    fun createStatus(
            @PathVariable projectId: UUID,
            @RequestParam statusName: String,
            @RequestParam color: String,
    ): StatusCreatedEvent {
        val project = projectEsService.getState(projectId)
                ?: throw NotFoundException("Project $projectId was not found.")

        if (tasksEsService.getState(project.getId()) == null)
            throw NotFoundException("Tasks and statuses aggregate $projectId does not exist.")

        return tasksEsService.update(projectId) {
            it.createStatus(
                    UUID.randomUUID(),
                    statusName,
                    projectId,
                    ColorEnum.valueOf(color)
            )
        }
    }

    @GetMapping("projects/{projectId}/statuses/{id}")
    fun getTaskStatus(@PathVariable projectId: UUID, @PathVariable id: UUID): StatusEntity? {
        return tasksEsService.getState(projectId)?.getStatusById(id)
    }

    @PatchMapping("projects/{projectId}/task/{taskId}/change-status")
    fun changeStatus(
            @PathVariable projectId: UUID,
            @PathVariable taskId: UUID,
            @RequestParam statusId: UUID,
    ): TaskStatusChangedEvent {
        val project = projectEsService.getState(projectId)
                ?: throw NotFoundException("Project $projectId was not found.")

        return tasksEsService.update(project.getId()) { it.changeStatus(taskId, statusId) }
    }

    @DeleteMapping("/{projectId}/statuses/{statusId}")
    fun deleteStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID): StatusDeletedEvent {
        val project = projectEsService.getState(projectId)
                ?: throw NotFoundException("Project $projectId was not found.")

        return tasksEsService.update(project.getId()) { it.deleteStatus(statusId) }
    }

    @PatchMapping("/projects/{projectId}/status/{statusId}/position")
    fun changeTaskStatusPosition(
            @PathVariable projectId: UUID,
            @PathVariable statusId: UUID,
            @RequestParam position: Int,
    ): StatusPositionChangedEvent {
        val project = projectEsService.getState(projectId)
                ?: throw NotFoundException("Project $projectId was not found.")

        return tasksEsService.update(project.getId()) {
            it.changeTaskStatusPosition(statusId, position)
        }
    }
}

