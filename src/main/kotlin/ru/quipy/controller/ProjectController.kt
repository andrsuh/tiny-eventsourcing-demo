package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.project.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.project.*
import ru.quipy.projections.ProjectionsService
import ru.quipy.projections.*
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val projectionsService: ProjectionsService,
) {
    @PostMapping()
    fun getAllProjectInfor() : List<ProjectProjection>
    {
        return projectionsService.getAllProjectInfor();
    }

    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: UUID) : ProjectCreatedEvent {
        return projectEsService.create { it.createProject(UUID.randomUUID(), projectTitle, creatorId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectProjection {
        return projectionsService.getProjectById(projectId);
    }
    @PostMapping("/changeProjectTitle/{projectId}")
    fun changeProjectTitle(@PathVariable projectId: UUID, @RequestParam newTitle: String) : ProjectTitleChangedEvent {
        return projectEsService.update(projectId) {
            it.changeProjectTitle(projectId, newTitle)
        }
    }
    @PostMapping("addStatus/{projectId}")
    fun addStatus(@PathVariable projectId: UUID, @RequestParam statusName: String, @RequestParam color: String) : StatusCreatedEvent {
        return projectEsService.update(projectId) {
            it.createStatus(statusName, color);
        }
    }

    @PostMapping("/{projectId}/status/changeStatus/{statusId}/{newStatusName}")
    fun changeStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID, @PathVariable newStatusName: String) : StatusChangedEvent{
        return projectEsService.update(projectId) {
            it.changeStatusName(statusId, newStatusName);
        }
    }

    @PostMapping("participants/{projectId}")
    fun addParticipantToProject(@PathVariable projectId: UUID, @RequestParam participantId: UUID) : AddParticipantToProjectEvent {
        return projectEsService.update(projectId) {
            it.addParticipantToProject(participantId, projectionsService.userProjectionRepo);
        }
    }
}