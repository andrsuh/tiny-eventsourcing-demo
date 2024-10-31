package ru.quipy.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.project.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.project.*
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {

    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam creator: String) : ProjectCreatedEvent {
        return projectEsService.create { it.create(UUID.randomUUID(), projectTitle, creator) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/members")
    fun addMember(@PathVariable projectId: UUID, @RequestParam member: String) : MemberAddedEvent {
        return projectEsService.update(projectId) {
            it.addMember(member)
        }
    }

    @PostMapping("/{projectId}/tasks/{taskId}")
    fun createTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID) : TaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.addTask(taskId)
        }
    }

    @DeleteMapping("/{projectId}/tasks/{taskId}")
    fun deleteTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID) : TaskDeletedEvent {
        return projectEsService.update(projectId) {
            it.delTask(taskId)
        }
    }

    @PostMapping("/{projectId}/statuses/{status}")
    fun addStatus(@PathVariable projectId: UUID, @PathVariable status: String) : StatusAddedEvent {
        return projectEsService.update(projectId) {
            it.addStatus(status)
        }
    }

    @DeleteMapping("/{projectId}/statuses/{status}")
    fun deleteStatus(@PathVariable projectId: UUID, @PathVariable status: String) : StatusDeletedEvent {
        return projectEsService.update(projectId) {
            it.delStatus(status)
        }
    }
}