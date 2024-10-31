package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val projectAggregateCommands: ProjectAggregateCommands
) {

    @PostMapping("/create/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: String) : ProjectCreatedEvent {
        return projectEsService.create { projectAggregateCommands.createProject(projectTitle, creatorId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }



    @PostMapping("/newMember/{projectId}")
    fun addProjectMember(@PathVariable projectId: UUID, @RequestParam userId: UUID) : ProjectExecutorAddedEvent {
        return projectEsService.update(projectId) {
            projectAggregateCommands.addProjectMember(projectId, userId)
        }
    }

    @PostMapping("/newStatus/{projectId}")
    fun addStatus(@PathVariable projectId: UUID, @RequestParam nameStatus: String, @RequestParam color: String):
            StatusCreatedEvent {
        return projectEsService.update(projectId) {
            projectAggregateCommands.addStatus(projectId, nameStatus, color)
        }
    }

    @PostMapping("/deleteStatus/{projectId}")
    fun addDefaultStatus(@PathVariable projectId: UUID, @RequestParam statusId: UUID):
            StatusDeletedEvent {
        return projectEsService.update(projectId) {
            projectAggregateCommands.deleteStatus(projectId, statusId)
        }
    }
}