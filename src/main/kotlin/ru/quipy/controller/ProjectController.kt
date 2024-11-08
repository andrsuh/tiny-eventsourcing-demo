package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.project.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.project.*
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {

    @PostMapping
    fun createProject(@RequestBody createProjectRequest: CreateProjectRequest): ProjectCreatedEvent {
        return projectEsService.create {
            it.createProject(UUID.randomUUID(), createProjectRequest.projectTitle, createProjectRequest.creatorId)
        }
    }

    @PostMapping("/participants/create")
    fun addParticipant(
            @RequestBody request: AddParticipantRequest
    ): ParticipantAddedEvent {
        return projectEsService.update(request.projectId) {
            it.addParticipant(request.projectId, request.userId, request.userName)
        }
    }

}

data class CreateProjectRequest(
        val projectTitle: String,
        val creatorId: String
)

data class AddParticipantRequest(
        val projectId: UUID,
        val userId: UUID,
        val userName: String
)


