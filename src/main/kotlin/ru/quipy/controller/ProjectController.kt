package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.enum.ColorEnum
import ru.quipy.logic.*
import ru.quipy.logic.command.addParticipantById
import ru.quipy.logic.command.createProject
import ru.quipy.logic.command.createStatus
import ru.quipy.logic.state.ProjectAggregateState
import ru.quipy.logic.state.TaskAndStatusAggregateState
import ru.quipy.logic.state.UserAggregateState
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
        val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
        val taskEsService: EventSourcingService<UUID, TaskAndStatusAggregate, TaskAndStatusAggregateState>,
        val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    @PostMapping("")
    fun createProject(
            @RequestParam projectName: String,
            @RequestParam creatorId: UUID,
    ): ProjectCreatedEvent {
        val user = userEsService.getState(creatorId)
                ?: throw NullPointerException("User $creatorId does not found")

        val response = projectEsService.create { it.createProject(UUID.randomUUID(), projectName) }

        taskEsService.create {
            it.createStatus(UUID.randomUUID(), "CREATED", response.projectId, ColorEnum.GREEN)
        }

        projectEsService.update(response.projectId) {
            it.addParticipantById(userId = creatorId)
        }

        return response;
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID): ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/participants")
    fun addParticipant(
            @PathVariable projectId: UUID,
            @RequestParam userId: UUID
    ): ParticipantAddedEvent {
        val user = userEsService.getState(userId) ?: throw NullPointerException("User $userId wasn't not found.")

        return projectEsService.update(projectId) { it.addParticipantById(userId = userId) }
    }
}
