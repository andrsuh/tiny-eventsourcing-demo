package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import ru.quipy.models.project.*
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {


    @PostMapping
    fun createProject(@RequestBody request: CreateProjectRequest, @RequestParam creatorId: UUID) : ProjectCreatedEvent {
        val user = userEsService.getState(creatorId)
        require(user != null) { "User should exist" }

        return projectEsService.create { it.create(UUID.randomUUID(), request.projectTitle, creatorId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PatchMapping("/{projectId}")
    fun updateProject(@RequestBody request: UpdateProjectRequest, @PathVariable projectId: UUID) : ProjectUpdatedEvent {
        return projectEsService.update(projectId) { it.update(request.title, request.description) }
    }

    @PostMapping("/{projectId}/participants/{participantId}")
    fun addParticipant(@PathVariable projectId: UUID, @PathVariable participantId: UUID) : ProjectMemberCreatedEvent {
        val user = userEsService.getState(participantId)
        require(user != null) { "User should exist" }

        return projectEsService.update(projectId) { it.addMember(participantId) }
    }

    @DeleteMapping("/{projectId}/participants/{participantId}")
    fun removeParticipant(@PathVariable projectId: UUID, @PathVariable participantId: UUID) : ProjectMemberRemovedEvent {
        val user = userEsService.getState(participantId)
        require(user != null) { "User should exist" }

        return projectEsService.update(projectId) { it.removeMember(participantId) }
    }

    @PostMapping("/{projectId}/tasks")
    fun createTask(@RequestBody request: CreateTaskRequest, @PathVariable projectId: UUID) : TaskCreatedEvent {
        return projectEsService.update(projectId) { it.addTask(request.taskName) }
    }

    @PatchMapping("/{projectId}/tasks/{taskId}")
    fun updateTask(@RequestBody request: UpdateTaskRequest, @PathVariable projectId: UUID, @PathVariable taskId: UUID) : TaskUpdatedEvent {
        return projectEsService.update(projectId) { it.updateTask(taskId, request.taskName, request.taskDescription) }
    }

    @PostMapping("/{projectId}/tasks/{taskId}/assignTo/{assigneeId}")
    fun addAssignee(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @PathVariable assigneeId: UUID) : TaskAssignedEvent {
        val user = userEsService.getState(assigneeId)
        require(user != null) { "User should exist" }

        return projectEsService.update(projectId) { it.addAssignee(taskId, assigneeId) }
    }

    @PostMapping("/{projectId}/tasks/{taskId}/addTag/{tagId}")
    fun addTagToTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @PathVariable tagId: UUID) : TagAddedToTaskEvent {
        return projectEsService.update(projectId) { it.addTagToTask(taskId, tagId) }
    }

    @DeleteMapping("/{projectId}/tasks/{taskId}")
    fun removeTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID) : TaskDeletedEvent {
        return projectEsService.update(projectId) { it.removeTask(taskId) }
    }

    @PostMapping("/{projectId}/tags")
    fun createTag(@RequestBody request: CreateTagRequest, @PathVariable projectId: UUID, @RequestParam creatorId: UUID) : TagCreatedEvent {
        val user = userEsService.getState(creatorId)
        require(user != null) { "User should exist" }

        return projectEsService.update(projectId) { it.createTag(request.name, request.color, creatorId) }
    }

    @DeleteMapping("/{projectId}/tags/{tagId}")
    fun removeTag(@PathVariable projectId: UUID, @PathVariable tagId: UUID) : TagDeletedEvent {
        return projectEsService.update(projectId) { it.removeTag(tagId) }
    }
}
