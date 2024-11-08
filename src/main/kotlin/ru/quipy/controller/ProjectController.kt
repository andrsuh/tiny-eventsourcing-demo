package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.Project
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(val projectEsService: EventSourcingService<UUID, ProjectAggregate, Project>) {

    @PostMapping
    fun createProject(
        @RequestParam projectTitle: String,
        @RequestParam creatorId: UUID
    ): ProjectCreatedEvent {
        return projectEsService.create {
            it.createProject(projectTitle, creatorId)
        }
    }

    @PostMapping("/{projectId}")
    fun changeProjectTitle(
        @PathVariable projectId: UUID,
        @RequestParam newTitle: String
    ): ProjectTitleChangedEvent {
        return projectEsService.update(projectId) {
            it.changeProjectTitle(newTitle)
        }
    }

    @PostMapping("/{projectId}/addParticipant")
    fun addParticipantToProject(
        @PathVariable projectId: UUID,
        @RequestParam participantId: UUID
    ): AddParticipantToProjectEvent {
        return projectEsService.update(projectId) {
            it.addParticipantToProject(participantId)
        }
    }

    @PostMapping("/{projectId}/addStatus")
    fun addStatus(
        @PathVariable projectId: UUID,
        @RequestParam statusName: String,
        @RequestParam color: String
    ): StatusCreatedEvent {
        return projectEsService.update(projectId) {
            it.createStatus(statusName, color)
        }
    }

    @PostMapping("/{projectId}/deleteStatus")
    fun deleteStatus(
        @PathVariable projectId: UUID,
        @RequestParam statusId: UUID
    ): StatusDeletedEvent {
        return projectEsService.update(projectId) {
            it.deleteStatus(statusId)
        }
    }

    @PostMapping("/{projectId}/changeStatusTitle")
    fun changeStatusTitle (
        @PathVariable projectId: UUID,
        @RequestParam statusId: UUID,
        @RequestParam newStatusTitle: String
    ): StatusTitleChangedEvent {
        return projectEsService.update(projectId) {
            it.changeStatusTitle(statusId, newStatusTitle);
        }
    }

    @PostMapping("/{projectId}/task")
    fun createTask(
        @PathVariable projectId: UUID,
        @RequestParam taskTitle: String
    ): TaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.createTask(taskTitle)
        }
    }

    @PostMapping("/{projectId}/changeTaskTitle")
    fun changeTaskTitle(
        @PathVariable projectId: UUID,
        @RequestParam taskId: UUID,
        @RequestParam newTitle: String
    ): TaskNameChangeEvent {
        return projectEsService.update(projectId) {
            it.changeTaskTitle(taskId, newTitle)
        }
    }

    @PostMapping("/{projectId}/changeStatus")
    fun changeTaskStatus(
        @PathVariable projectId: UUID,
        @RequestParam taskId: UUID,
        @RequestParam newStatusId: UUID
    ): TaskStatusChangedEvent {
        return projectEsService.update(projectId) {
            it.changeTaskStatus(taskId, newStatusId)
        }
    }

    @PostMapping("/{projectId}/addExecutor")
    fun addExecutorToTask(
        @PathVariable projectId: UUID,
        @RequestParam taskId: UUID,
        @RequestParam userId: UUID
    ): TaskExecutorAssignedEvent? {
        return projectEsService.update(projectId) {
            it.addExecutorToTask(taskId, userId)
        }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID): Project? {
        return projectEsService.getState(projectId)
    }
}
