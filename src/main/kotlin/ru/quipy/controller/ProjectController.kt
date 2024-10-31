package ru.quipy.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: String): ProjectCreatedEvent {
        return projectEsService.create { it.createProject(UUID.randomUUID(), projectTitle, creatorId) }
    }

    @GetMapping("/{projectId}")
    fun getAccount(@PathVariable projectId: UUID): ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/createStatus/{statusName}")
    fun createStatus(
        @PathVariable statusName: String,
        @RequestParam projectId: String,
        @RequestParam color: String,
        @RequestParam userInitiatorId: String,
    ): StatusCreatedEvent {
        return projectEsService.update(UUID.fromString(projectId)) {
            it.createStatus(statusName, color, UUID.fromString(userInitiatorId))
        }
    }

    @PutMapping("/{name}")
    fun changeProjectName(
        @PathVariable name: String,
        @RequestParam projectId: String,
        @RequestParam userInitiatorId: String
    ): ProjectNameChangedEvent {
        return projectEsService.update(UUID.fromString(projectId)) {
            it.changeProjectName(name, UUID.fromString(userInitiatorId))
        }
    }

    @PutMapping("/addUser/{projectId}")
    fun addUserToProject(
        @PathVariable projectId: String,
        @RequestParam userId: String,
        @RequestParam userInitiatorId: String,
    ): UserAddedToProjectEvent {
        return projectEsService.update(UUID.fromString(projectId)) {
            it.addUserToProject(UUID.fromString(userInitiatorId), UUID.fromString(userId))
        }
    }

    @DeleteMapping("/{statusId}")
    fun deleteStatus(
        @PathVariable statusId: String,
        @RequestParam projectId: String,
        @RequestParam userInitiatorId: String
    ): StatusDeletedEvent {
        return projectEsService.update(UUID.fromString(projectId)) {
            it.deleteStatus(UUID.fromString(statusId), UUID.fromString(userInitiatorId))
        }
    }
}