package ru.quipy.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
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
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {

    @PostMapping()
    fun createProject(@RequestParam name: String, @RequestParam creatorId: UUID) : ProjectCreatedEvent {
        return projectEsService.create {
            it.create(UUID.randomUUID(), name, creatorId);
        }
    }

    @GetMapping("{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/users")
    fun addUser(@PathVariable projectId: UUID, @RequestParam userId: UUID) : ProjectUserAddedEvent {
        return projectEsService.update(projectId) {
            it.addUser(userId)
        }
    }

    @DeleteMapping("/{projectId}/users")
    fun removeUser(@PathVariable projectId: UUID, @RequestParam userId: UUID) : ProjectUserRemovedEvent {
        return projectEsService.update(projectId) {
            it.removeUser(userId)
        }
    }

    @GetMapping("{projectId}/users")
    fun getUsers(@PathVariable projectId: UUID) : List<UUID>? {
        return projectEsService.getState(projectId)?.members?.toList()
    }

    @PostMapping("/{projectId}/tasks")
    fun addTask(
        @PathVariable projectId: UUID,
        @RequestParam userId: UUID,
        @RequestParam name: String
    ) : ProjectTaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.createTask(userId, UUID.randomUUID(), name)
        }
    }

    @PatchMapping("/{projectId}/task/{taskId}")
    fun modifyTask(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam statusId: UUID?,
        @RequestParam executors: MutableSet<UUID>?,
        @RequestParam name: String?
    ) : ProjectTaskModifiedEvent {
        return projectEsService.update(projectId) {
            it.modifyTask(taskId, statusId, executors, name)
        }
    }

    @GetMapping("{projectId}/tasks")
    fun getTask(@PathVariable projectId: UUID) : List<TaskEntity>? {
        return projectEsService.getState(projectId)?.tasks?.values?.toList()
    }

    @GetMapping("{projectId}/task/{taskId}")
    fun getTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID) : TaskEntity? {
        return projectEsService.getState(projectId)?.tasks?.get(taskId)
    }

    @PostMapping("/{projectId}/statuses")
    fun createStatus(
        @PathVariable projectId: UUID,
        @RequestParam name: String
    ) : ProjectStatusCreatedEvent {
        return projectEsService.update(projectId) {
            it.createStatus(name, UUID.randomUUID())
        }
    }


    @DeleteMapping("/{projectId}/status/{statusId}")
    fun removeStatus(
        @PathVariable projectId: UUID,
        @PathVariable statusId: UUID
    ): ProjectStatusRemovedEvent {
        return projectEsService.update(projectId) {
            it.removeStatus(statusId)
        }
    }

    @GetMapping("{projectId}/statuses")
    fun getStatus(@PathVariable projectId: UUID) : List<StatusEntity>? {
        return projectEsService.getState(projectId)?.statuses?.values?.toList()
    }

    @GetMapping("{projectId}/status/{statusId}")
    fun getStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID) : StatusEntity? {
        return projectEsService.getState(projectId)?.statuses?.get(statusId)
    }
}