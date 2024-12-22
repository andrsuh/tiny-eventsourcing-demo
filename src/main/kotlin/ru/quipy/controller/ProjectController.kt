package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.awt.Color
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
        val projectService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {
    @PostMapping("")
    fun createProject(@RequestParam projectTitle: String, @RequestParam creatorId: UUID): ProjectCreatedEvent {
        return projectService.create {
            it.create(UUID.randomUUID(), projectTitle, creatorId)
        }
    }

    @GetMapping("")
    fun getProject(@RequestParam projectId: UUID): ProjectAggregateState? {
        return projectService.getState(projectId)
    }

    @PostMapping("/user")
    fun addUserInProject(@RequestParam userId: UUID, @RequestParam projectId: UUID, @RequestParam adderId: UUID): UserAddedInProjectEvent {
        return projectService.update(projectId) {
            it.addUserInProject(userId, projectId, adderId)
        }
    }

    @PostMapping("/status")
    fun addStatus(@RequestParam adderId: UUID, @RequestParam projectId: UUID, @RequestParam statusName: String,
                  @RequestParam red: Int, @RequestParam green: Int, @RequestParam blue: Int): StatusAddedEvent {
        val color = Color(red, green, blue)
        return projectService.update(projectId) {
            it.addStatus(statusName, color, UUID.randomUUID(), projectId, adderId)
        }
    }

    @DeleteMapping("/status")
    fun removeStatus(@RequestParam projectId: UUID, @RequestParam statusId: UUID, @RequestParam removerId: UUID): StatusRemovedEvent {
        return projectService.update(projectId) {
            it.removeStatus(statusId, removerId)
        }
    }

    @PutMapping("/status")
    fun changeTaskStatus(@RequestParam projectId: UUID, @RequestParam taskId: UUID, @RequestParam newStatusId: UUID,
                         @RequestParam changerId: UUID): TaskStatusChangedEvent {
        return projectService.update(projectId) {
            it.changeTaskStatus(taskId, newStatusId, changerId)
        }
    }

    @PostMapping("/task")
    fun createTask(@RequestParam taskName: String, @RequestParam projectId: UUID, @RequestParam creatorId: UUID): TaskCreatedEvent {
        return projectService.update(projectId) {
            it.createTask(UUID.randomUUID(), taskName, projectId, creatorId)
        }
    }

    @PutMapping("/task")
    fun changeTask(@RequestParam projectId: UUID, @RequestParam taskId: UUID, @RequestParam newName: String,
                   @RequestParam changerId: UUID): TaskChangedEvent {
        return projectService.update(projectId) {
            it.changeTask(taskId, newName, changerId)
        }
    }

    @PostMapping("/task/assignee")
    fun addAssignee(@RequestParam projectId: UUID, @RequestParam taskId: UUID, @RequestParam assigneeId: UUID,
                    @RequestParam adderId: UUID): AssigneeAddedEvent {
        return projectService.update(projectId) {
            it.addAssignee(taskId, assigneeId, adderId)
        }
    }
}