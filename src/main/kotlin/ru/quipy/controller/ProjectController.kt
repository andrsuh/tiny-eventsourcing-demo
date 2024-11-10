package ru.quipy.controller

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
) {

    @PostMapping
    fun createProject(
        @RequestParam creatorId: UUID,
        @RequestBody request: CreateProjectRequest
    ): ProjectCreatedEvent {
        val user = userEsService.getState(creatorId)
        require(user != null) { "Пользователь должен существовать" }

        return projectEsService.create { it.create(request.projectTitle, creatorId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID): ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/participants")
    fun addParticipant(
        @PathVariable projectId: UUID,
        @RequestBody request: AddParticipantRequest,
        @RequestParam userId: UUID
    ): ProjectParticipantAddedEvent {
        val user = userEsService.getState(userId)
        require(user != null) { "Пользователь должен существовать" }

        return projectEsService.update(projectId) {
            it.addParticipant(request.userId)
        }
    }

    @PutMapping("/{projectId}/name")
    fun editProjectName(
        @PathVariable projectId: UUID,
        @RequestBody request: EditProjectNameRequest,
        @RequestParam userId: UUID
    ): ProjectNameEditedEvent {
        val user = userEsService.getState(userId)
        require(user != null) { "Пользователь должен существовать" }

        return projectEsService.update(projectId) {
            it.nameEdited(request.newName, userId)
        }
    }

    @PostMapping("/{projectId}/tasks")
    fun createTask(
        @PathVariable projectId: UUID,
        @RequestBody request: CreateTaskRequest,
        @RequestParam userId: UUID
    ): TaskCreatedEvent {
        val user = userEsService.getState(userId)
        require(user != null) { "Пользователь должен существовать" }

        return projectEsService.update(projectId) {
            it.addTask(request.taskName, userId)
        }
    }

    @PostMapping("/{projectId}/statuses")
    fun createTaskStatus(
        @PathVariable projectId: UUID,
        @RequestBody request: CreateTaskStatusRequest,
        @RequestParam userId: UUID
    ): TaskStatusCreatedEvent {
        val user = userEsService.getState(userId)
        require(user != null) { "Пользователь должен существовать" }

        return projectEsService.update(projectId) {
            it.taskStatusCreatedEvent(request.statusColor, request.statusName, userId)
        }
    }

    @PutMapping("/{projectId}/tasks/{taskId}/status")
    fun setTaskStatus(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestBody request: SetTaskStatusRequest,
        @RequestParam userId: UUID
    ): TaskStatusSetEvent {
        val user = userEsService.getState(userId)
        require(user != null) { "Пользователь должен существовать" }

        return projectEsService.update(projectId) {
            it.taskStatusSetEvent(taskId, request.statusName, userId)
        }
    }

    @DeleteMapping("/{projectId}/statuses/{statusName}")
    fun deleteTaskStatus(
        @PathVariable projectId: UUID,
        @PathVariable statusName: String,
        @RequestParam userId: UUID
    ): TaskStatusDeletedEvent {
        val user = userEsService.getState(userId)
        require(user != null) { "Пользователь должен существовать" }

        return projectEsService.update(projectId) {
            it.taskStatusDeletedEvent(statusName, userId)
        }
    }

    @PutMapping("/{projectId}/tasks/{taskId}/name")
    fun editTaskName(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestBody request: EditTaskNameRequest,
        @RequestParam userId: UUID
    ): TaskNameEditedEvent {
        val user = userEsService.getState(userId)
        require(user != null) { "Пользователь должен существовать" }

        return projectEsService.update(projectId) {
            it.taskNameEditedEvent(taskId, request.newTaskName, userId)
        }
    }

    @PutMapping("/{projectId}/tasks/{taskId}/performer")
    fun setTaskPerformer(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestBody request: SetTaskPerformerRequest,
        @RequestParam userId: UUID
    ): TaskPerfomerSetEvent {
        val user = userEsService.getState(userId)
        require(user != null) { "Пользователь должен существовать" }

        return projectEsService.update(projectId) {
            it.taskPerformerSetEvent(taskId, request.performerId, userId)
        }
    }

    @DeleteMapping("/{projectId}/tasks/{taskId}/performer/{performerId}")
    fun deleteTaskPerformer(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @PathVariable performerId: UUID,
        @RequestParam userId: UUID
    ): TaskPerfomerDeletedEvent {
        val user = userEsService.getState(userId)
        require(user != null) { "Пользователь должен существовать" }

        return projectEsService.update(projectId) {
            it.taskPerformerDeletedEvent(taskId, performerId, userId)
        }
    }

    @DeleteMapping("/{projectId}/tasks/{taskId}")
    fun deleteTask(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam userId: UUID
    ): TaskDeletedEvent {
        val user = userEsService.getState(userId)
        require(user != null) { "Пользователь должен существовать" }

        return projectEsService.update(projectId) {
            it.taskDeletedEvent(taskId, userId)
        }
    }
}
