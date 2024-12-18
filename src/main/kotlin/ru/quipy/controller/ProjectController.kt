package ru.quipy.controller

import liquibase.hub.model.Project
import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import ru.quipy.projections.ProjectProjection
import ru.quipy.service.ProjectionService
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val projectionService: ProjectionService,
) {

    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: String): ProjectCreatedEvent {
        return projectEsService.create { it.create(UUID.randomUUID(), projectTitle, creatorId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID): ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @GetMapping("/{projectId}/tasks/{taskId}")
    fun getTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID): TaskEntity? {
        return projectEsService.getState(projectId)?.tasks?.get(taskId)
    }

    @PostMapping("/{projectId}/tasks/{taskName}")
    fun createTask(@PathVariable projectId: UUID, @PathVariable taskName: String): TaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.addTask(taskName)
        }
    }

    @PutMapping("/{projectId}/members/{userId}")
    fun addMember(@PathVariable projectId: UUID, @PathVariable userId: UUID): MemberAddedEvent {
        return projectEsService.update(projectId) {
            it.addMember(userId)
        }
    }

    @PutMapping("/{projectId}/tasks/{taskId}")
    fun changeStatus(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam newStatus: Int
    ): TaskStatusChangedEvent {
        return projectEsService.update(projectId) {
            it.changeStatus(taskId, newStatus)
        }
    }

    @PutMapping("/{projectId}/tasks/{taskId}/executors/{executorId}")
    fun addExecutor(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @PathVariable executorId: UUID)
            : ExecutorAssignedToTaskEvent {
        return projectEsService.update(projectId) {
            it.addExecutor(taskId, executorId)
        }
    }

    @GetMapping("/owner/{ownerId}")
    fun findAllByOwnerId(@PathVariable ownerId: UUID): List<ProjectProjection> {
        return projectionService.findAllProjectsByOwnerId(ownerId)
    }

    @GetMapping("/participating/{participantId}")
    fun findAllByParticipantId(@PathVariable participantId: UUID): List<ProjectProjection> {
        return projectionService.findAllProjectByParticipantIdContains(participantId)
    }
}