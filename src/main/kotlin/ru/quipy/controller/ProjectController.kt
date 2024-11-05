package ru.quipy.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {

    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: UUID) : ProjectCreatedEvent {
        return projectEsService.create { it.create(UUID.randomUUID(), projectTitle, creatorId) }
    }

    @PostMapping("/{projectId}/tasks/{taskName}")
    fun createTask(@PathVariable projectId: UUID, @PathVariable taskName: String, @RequestParam statusId: UUID) : TaskCreatedEvent {
        return projectEsService.update(projectId) { it.createTask(UUID.randomUUID(), taskName, statusId) }
    }

    @PostMapping("/{projectId}/tasks/{taskId}/assign/{userId}")
    fun assignUserOnTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @PathVariable userId: UUID) : UserAssignedEvent {
        return projectEsService.update(projectId) { it.assignUser(UUID.randomUUID(), taskId, userId) }
    }

    @PostMapping("/{projectId}/tasks/{taskId}/set-status/{statusId}")
    fun changeTaskStatus(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @PathVariable statusId: UUID) : TaskStatusChangedEvent {
        return projectEsService.update(projectId) { it.changeTaskStatus(taskId, statusId) }
    }

    @PostMapping("/{projectId}/statuses/{statusText}")
    fun createNewStatus(@PathVariable projectId: UUID, @PathVariable statusText: String, @RequestParam statusColor: String) : TaskStatusCreatedEvent {
        return projectEsService.update(projectId) { it.createStatus(UUID.randomUUID(), statusText, statusColor) }
    }

    @PostMapping("/{projectId}/members/{userId}")
    fun addUserToProject(@PathVariable projectId: UUID, @PathVariable userId: UUID) : UserAddedToProjectEvent {
        return projectEsService.update(projectId) { it.addUser(UUID.randomUUID(), userId) }
    }

    @PostMapping("/{projectId}/tasks/{taskId}/rename/{newTitle}")
    fun renameTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @PathVariable newTitle: String) : TaskTittleChangedEvent {
        return projectEsService.update(projectId) { it.changeTaskTitle(taskId, newTitle) }
    }

    @PostMapping("/{projectId}/statuses/{taskId}/delete")
    fun deleteStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID) : TaskStatusDeletedEvent {
        return projectEsService.update(projectId) { it.deleteStatus(statusId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @GetMapping("/{projectId}/tasks")
    fun getTasks(@PathVariable projectId: UUID) : MutableCollection<TaskEntity>? {
        return projectEsService.getState(projectId)!!.tasks.values
    }

    @GetMapping("/{projectId}/tasks/{taskId}")
    fun getTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID) : TaskEntity? {
        return projectEsService.getState(projectId)!!.tasks[taskId]
    }

    @GetMapping("/{projectId}/members")
    fun getMembers(@PathVariable projectId: UUID) : MutableCollection<ProjectMemberEntity>? {
        return projectEsService.getState(projectId)!!.members.values
    }

    @GetMapping("/{projectId}/statuses")
    fun getStatuses(@PathVariable projectId: UUID) : MutableCollection<StatusEntity>? {
        return projectEsService.getState(projectId)!!.statuses.values
    }
}