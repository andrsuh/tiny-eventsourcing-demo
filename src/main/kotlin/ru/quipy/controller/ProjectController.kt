package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.controller.requests.CreateStatusRequest
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {

    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: UUID): ProjectCreatedEvent {
        return projectEsService.create { it.create(UUID.randomUUID(), projectTitle, creatorId) }
    }

    @PostMapping("/{projectId}/participants")
    fun addParticipants(@PathVariable projectId: UUID, @RequestParam participantId: UUID): ParticipantAddedEvent {
        return projectEsService.update(aggregateId = projectId) {
            it.addParticipants(participantId = participantId)
        }
    }

    @PutMapping("/{projectId}")
    fun editProjectsTitle(@PathVariable projectId: UUID, @RequestParam title: String): EditProjectTitleEvent {
        return projectEsService.update(aggregateId = projectId) {
            it.editProjectTitle(title = title)
        }
    }

    @PostMapping("/{projectId}/status")
    fun createStatus(@PathVariable projectId: UUID, @RequestBody request: CreateStatusRequest): StatusCreatedEvent {
        return projectEsService.update(aggregateId = projectId) {
            it.createStatus(statusId = UUID.randomUUID(), statusName = request.statusName, color = request.color)
        }
    }

    @DeleteMapping("/{projectId}/{statusId}")
    fun deleteStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID): StatusDeletedEvent {
        return projectEsService.update(aggregateId = projectId) {
            it.deleteStatus(statusId = statusId)
        }
    }
}