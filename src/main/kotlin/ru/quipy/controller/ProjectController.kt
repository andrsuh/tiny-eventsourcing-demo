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
import ru.quipy.projections.entity.StatusesWithTasksProjection
import ru.quipy.projections.projectParticipants.ProjectParticipantService
import ru.quipy.projections.repository.TaskInfoRepository
import ru.quipy.projections.statusesWithTasks.StatusesWithTasksServices
import ru.quipy.projections.views.ProjectParticipantsView
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val projectParticipantService: ProjectParticipantService,
    val statusesWithTasksServices: StatusesWithTasksServices,
    private val projectProjectionRepository: TaskInfoRepository
) {

    @PostMapping("/create")
    fun createProject(@RequestParam projectTitle: String, @RequestParam description: String,  @RequestParam userId: UUID) : ProjectCreatedEvent {
        val createProjectEvent =  projectEsService.create { it.create(UUID.randomUUID(), projectTitle, description)}
        userEsService.update(userId) {it.addProject(projectEsService.getState(createProjectEvent.projectId)) }
        return createProjectEvent
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID): StatusesWithTasksProjection? {
        return statusesWithTasksServices.getStatusesByProjectById(projectId)
    }

    @GetMapping("/{projectId}/users")
    fun getProjectUsers(@PathVariable projectId: UUID): ProjectParticipantsView? {
        return projectParticipantService.findUsersByProject(projectId)
    }

    @GetMapping("/{projectId}/account")
    fun getAccount(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/status")
    fun createStatus(@PathVariable projectId: UUID, @RequestParam name: String) : StatusCreatedEvent {
        return projectEsService.create { it.createStatus(name) }
    }

    @PostMapping("/{projectId}/{statusId}")
    fun assignStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID, @RequestParam taskId: UUID) : StatusAssignedToTaskEvent {
        return projectEsService.create { it.assignStatusToTask(statusId=statusId, taskId=taskId) }
    }

    @PostMapping("/{projectId}/tasks/add")
    fun createTask(@PathVariable projectId: UUID, @RequestParam taskName: String, @RequestParam description: String) : TaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.addTask(name = taskName, description = description)
        }
    }
}