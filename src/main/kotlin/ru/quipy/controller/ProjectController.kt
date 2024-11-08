package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.project.ProjectAggregate
import ru.quipy.api.project.ProjectCreatedEvent
import ru.quipy.api.project.StatusCreatedEvent
import ru.quipy.api.project.UserAssignedToProjectEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.project.ProjectAggregateState
import ru.quipy.logic.project.assignUser
import ru.quipy.logic.project.create
import ru.quipy.logic.project.createStatus
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {

    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: String) : ProjectCreatedEvent {
        return projectEsService.create { it.create(UUID.randomUUID(), projectTitle, creatorId) }
    }

    @PostMapping("/{projectId}/statuses")
    fun createStatus(@PathVariable projectId: UUID, @RequestParam title: String) : StatusCreatedEvent{
        return projectEsService.update(projectId){
            it.createStatus(title)
        }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/{userId}")
    fun assignUserToProject(@PathVariable projectId: UUID, @PathVariable userId: UUID) : UserAssignedToProjectEvent {
        return projectEsService.update(projectId) {
            it.assignUser(userId)
        }
    }
}