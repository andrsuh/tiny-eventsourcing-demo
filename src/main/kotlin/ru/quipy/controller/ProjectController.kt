package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.addTask
import ru.quipy.logic.create
import ru.quipy.logic.createTag
import ru.quipy.logic.*
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

    @GetMapping("/{projectId}")
    fun getAccount(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/tasks/{taskName}")
    fun createTask(@RequestParam userId: UUID,@PathVariable projectId: UUID, @PathVariable taskName: String) : TaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.addTask(userId,taskName)
        }
    }

    @PostMapping("/{projectId}/tags/{tagName}")
    fun createTag(@RequestParam userId: UUID,@PathVariable projectId: UUID, @PathVariable tagName: String) : TagCreatedEvent {
        return projectEsService.update(projectId) {
            it.createTag(userId,tagName)
        }
    }

    @PostMapping("/{projectId}/tasks/{taskName}")
    fun assignTagToTask(@RequestParam userId: UUID, @PathVariable projectId: UUID, @RequestParam tagId: UUID, @RequestParam taskId: UUID) : TagAssignedToTaskEvent {
        return projectEsService.update(projectId) {
            it.assignTagToTask(userId, tagId,taskId)
        }
    }

    @PostMapping("/{projectId}/membersList")
    fun addUserToProject(@RequestParam userId: UUID,@RequestParam userNewId: UUID, @PathVariable projectId: UUID) : UserAddedToProjectEvent {
        return projectEsService.update(projectId) {
            it.addUserToProject(userId, userNewId)
        }
    }

    @PostMapping("/{projectId}")
    fun changeProjectName(@RequestParam userId: UUID, @PathVariable projectId: UUID, @RequestParam title: String) : ProjectNameChangedEvent {
        return projectEsService.update(projectId) {
            it.changeProjectName(userId, title)
        }
    }

    @PostMapping("/{projectId}/tags/{tagId}")
    fun deleteTag(@RequestParam userId: UUID, @PathVariable projectId: UUID, @PathVariable tagId: UUID) : TagDeletedEvent {
        return projectEsService.update(projectId) {
            it.deleteTag(userId, tagId)
        }
    }

    @PostMapping("/{projectId}/tasks/{taskId}")
    fun assignUserToTask(@RequestParam userId: UUID, @PathVariable projectId: UUID, @RequestParam taskId: UUID) : UserAssignedToTaskEvent {
        return projectEsService.update(projectId) {
            it.assignUserToTask(userId, taskId)
        }
    }

    @PostMapping("/{projectId}/tasks/{taskId}")
    fun changeTaskName(@RequestParam userId: UUID, @PathVariable projectId: UUID, @RequestParam title: String, @RequestParam taskId: UUID) : TaskNameChangedEvent {
        return projectEsService.update(projectId) {
            it.changeTaskName(userId, taskId, title)
        }
    }
}