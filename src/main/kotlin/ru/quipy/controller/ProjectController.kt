package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.dto.project.AddParticipantDto
import ru.quipy.dto.project.CreateProjectDto
import ru.quipy.enum.ColorEnum
import ru.quipy.logic.*
import ru.quipy.logic.command.addParticipantById
import ru.quipy.logic.command.createProject
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
    fun createProject(@RequestBody body: CreateProjectDto): ProjectCreatedEvent {
        val user = userEsService.getState(body.creatorId)
                ?: throw NullPointerException("User ${body.creatorId} does not found")

        val response =  projectEsService.create { it.createProject(UUID.randomUUID(), UUID.randomUUID(), body.projectName, body.creatorId) }

        taskEsService.create {
            it.createStatus(UUID.randomUUID(), "CREATED", response.projectId, ColorEnum.GREEN)
        }

        projectEsService.update(response.projectId) {
            it.addParticipantById(userId = body.creatorId)
        }

        return response;
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID): ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/participants")
    fun addParticipant(@PathVariable projectId: UUID, @RequestBody body: AddParticipantDto): ParticipantAddedEvent {
        val user = userEsService.getState(body.userId)
                ?: throw NullPointerException("User ${body.userId} does not found")

        return projectEsService.update(projectId) { it.addParticipantById(userId = body.userId) }
    }

    //    @PatchMapping("/{projectId}")
//    fun updateProject(@PathVariable projectId: UUID, @RequestBody body: UpdateProjectDto): ProjectUpdatedEvent {
//        return projectEsService.update(projectId) { it.updateProject(projectId, body.name) }
//    }
}


//data class UpdateProjectDto(
//        val name: String
//)
