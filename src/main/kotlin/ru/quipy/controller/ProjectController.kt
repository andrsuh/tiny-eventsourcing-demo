package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.api.auth.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.auth.UserAggregateState
import ru.quipy.logic.auth.UserAggregateState.Companion.usersAggregateId
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val userEsService: EventSourcingService<String, UserAggregate, UserAggregateState>
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
        checkIfUserExists(participantId)

        return projectEsService.update(projectId) {
            it.addParticipant(participantId)
        }
    }

    @PostMapping("/create-status/{projectId}")
    fun createStatus(@PathVariable projectId: UUID, @RequestBody dto: CreateStatusDto) : StatusCreatedEvent {
        checkIfUserExists(dto.participantId)

        return projectEsService.update(projectId) {
            it.createStatus(dto.name, dto.color, dto.participantId)
        }
    }

    @PostMapping("/create-task/{projectId}")
    fun createTask(@PathVariable projectId: UUID, @RequestBody dto: CreateTaskDto) : TaskCreatedEvent {
        checkIfUserExists(dto.participantId)

        return projectEsService.update(projectId) {
            it.createTask(UUID.randomUUID(), dto.name, dto.description, dto.participantId)
        }
    }

    @PostMapping("/add-task-assignee/{projectId}")
    fun addTaskAssignee(@PathVariable projectId: UUID, @RequestParam taskId: UUID, @RequestParam participantId: UUID) : TaskAssigneeAddedEvent {
        checkIfUserExists(participantId)

        return projectEsService.update(projectId) {
            it.addTaskAssignee(taskId, participantId)
        }
    }

    @PostMapping("/change-task-status/{projectId}")
    fun changeTaskStatus(@PathVariable projectId: UUID, @RequestParam taskId: UUID, @RequestParam newStatus: String, @RequestParam participantId: UUID) : TaskStatusChangedEvent {
        checkIfUserExists(participantId)

        return projectEsService.update(projectId) {
            it.changeTaskStatus(taskId, newStatus, participantId)
        }
    }

    @PostMapping("/change-task-name/{projectId}")
    fun changeTaskName(@PathVariable projectId: UUID, @RequestParam taskId: UUID, @RequestParam newTaskName: String, @RequestParam participantId: UUID) : TaskNameChangedEvent {
        checkIfUserExists(participantId)

        return projectEsService.update(projectId) {
            it.changeTaskName(taskId, newTaskName, participantId)
        }
    }

    @DeleteMapping("/delete-task/{projectId}")
    fun deleteTask(@PathVariable projectId: UUID, @RequestParam taskId: UUID, @RequestParam participantId: UUID) : TaskDeletedEvent {
        checkIfUserExists(participantId)

        return projectEsService.update(projectId) {
            it.deleteTask(taskId, participantId)
        }
    }

    @PostMapping("/change-status-order/{projectId}")
    fun changeStatusOrder(@PathVariable projectId: UUID, @RequestParam statusName: String, @RequestParam newOrder: Int, @RequestParam participantId: UUID) : StatusOrderChangedEvent {
        return projectEsService.update(projectId) {
            it.changeStatusOrder(statusName, newOrder, participantId)
        }
    }

    @PostMapping("/change-status-color/{projectId}")
    fun changeStatusColor(@PathVariable projectId: UUID, @RequestParam statusName: String, @RequestParam newColor: String, @RequestParam participantId: UUID) : StatusColorChangedEvent {
        checkIfUserExists(participantId)

        return projectEsService.update(projectId) {
            it.changeStatusColor(statusName, newColor, participantId)
        }
    }

    @DeleteMapping("/delete-status/{projectId}")
    fun deleteStatus(@PathVariable projectId: UUID, @RequestParam statusName: String,  @RequestParam participantId: UUID) : StatusDeletedEvent {
        checkIfUserExists(participantId)

        return projectEsService.update(projectId) {
            it.deleteStatus(statusName, participantId)
        }
    }

    private fun checkIfUserExists(userId: UUID) {
        if (userEsService.getState(usersAggregateId)?.users?.containsKey(userId) == false) {
            throw IllegalArgumentException("User with id $userId does not exists")
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