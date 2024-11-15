package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import ru.quipy.models.UpdateProjectRequest
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: UUID) : ProjectCreatedEvent {
        val user = userEsService.getState(creatorId)
        require(user != null) { "User should exist" }

        return projectEsService.create { it.create(UUID.randomUUID(), projectTitle, creatorId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PatchMapping("/{projectId}")
    fun updateProject(@PathVariable projectId: UUID, @RequestBody request: UpdateProjectRequest) : ProjectUpdatedEvent {
        return projectEsService.update(projectId) { it.update(request.title, request.description) }
    }

    @PostMapping("/{projectId}/participants/{participantId}")
    fun addParticipant(@PathVariable projectId: UUID, @PathVariable participantId: UUID) : ProjectUserAddedEvent {
        val user = userEsService.getState(participantId)
        require(user != null) { "User should exist" }

        return projectEsService.update(projectId) { it.addParticipant(projectId, participantId) }
    }

    @DeleteMapping("/{projectId}/participants/{participantId}")
    fun removeParticipant(@PathVariable projectId: UUID, @PathVariable participantId: UUID) : ProjectUserRemovedEvent {
        val project = projectEsService.getState(projectId)
        require(project != null) { "Project should exist" }
        require(project.participants.containsKey(participantId)) { "User should participate project" }

        return projectEsService.update(projectId) { it.removeParticipant(projectId, participantId) }
    }

    @PostMapping("/{projectId}/tasks/{taskName}")
    fun createTask(@PathVariable projectId: UUID, @PathVariable taskName: String) : TaskCreatedEvent {
        return projectEsService.update(projectId) { it.addTask(taskName) }
    }
}