package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {

    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: UUID) : ProjectCreatedEvent {
        return projectEsService.create { it.createProject(UUID.randomUUID(), projectTitle, creatorId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/add-participant/{projectId}")
    fun addParticipantToProject(@PathVariable projectId: UUID, @RequestParam participantId: UUID): ParticipantAddedToProjectEvent {
        return projectEsService.update(projectId) {
            it.addParticipant(participantId)
        }
    }

    @PostMapping("/create-status/{projectId}")
    fun createStatus(@PathVariable projectId: UUID, @RequestBody dto: CreateStatusDto) : StatusCreatedEvent {
        return projectEsService.update(projectId) {
            it.createStatus(dto.name, dto.color, dto.participantId)
        }
    }

    @PostMapping("/create-task/{projectId}")
    fun createTask(@PathVariable projectId: UUID, @RequestBody dto: CreateTaskDto) : TaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.createTask(UUID.randomUUID(), dto.name, dto.description, dto.participantId)
        }
    }

    @PostMapping("/add-task-assignee/{projectId}")
    fun addTaskAssignee(@PathVariable projectId: UUID, @RequestParam taskId: UUID, @RequestParam participantId: UUID) : TaskAssigneeAddedEvent {
        return projectEsService.update(projectId) {
            it.addTaskAssignee(taskId, participantId)
        }
    }
}

data class CreateTaskDto(
    val name: String,
    val description: String,
    val participantId: UUID
)

data class CreateStatusDto(
    val name: String,
    val color: String,
    val participantId: UUID
)