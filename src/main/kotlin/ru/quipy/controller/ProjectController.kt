package ru.quipy.controller

//import org.springframework.web.bind.annotation.*
//import ru.quipy.api.ProjectAggregate
//import ru.quipy.api.ProjectCreatedEvent
//import ru.quipy.api.TaskAndStatusAggregate
//import ru.quipy.api.TaskCreatedEvent
//import ru.quipy.core.EventSourcingService
//import ru.quipy.logic.ProjectAggregateState
//import ru.quipy.logic.addTask
//import ru.quipy.logic.create
//import java.util.*
import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
        val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {

    @PostMapping("")
    fun createProject(@RequestBody body: CreateProjectDto) : ProjectCreatedEvent {
        return projectEsService.create { it.createProject(UUID.randomUUID(), body.projectName, body.creatorId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

//    @PatchMapping("/{projectId}")
//    fun updateTask(@PathVariable projectId: UUID, @RequestBody body: UpdateProjectDto): ProjectUpdatedEvent {
//        return projectEsService.update(projectId) { it.updateProject(body.name) }
//    }
//
//    @PostMapping("/{projectId}/participants")
//    fun addParticipant(@PathVariable projectId: UUID, @RequestBody body: AddParticipantDto) : ParticipantAddedEvent {
//        return projectEsService.update(projectId) { it.addParticipantById(userId = body.userId) }
//    }

//    @PostMapping("/{projectId}/statuses")
//    fun createStatus(@PathVariable projectId: UUID, @RequestBody body: CreateStatusDto) : StatusCreatedEvent {
//        return tasksEsService.update(projectId) { it.createStatus(UUID.randomUUID(), body.statusName, body.colour) }
//    }
//
//    @DeleteMapping("/{projectId}/statuses/{statusId}")
//    fun deleteStatus(@PathVariable projectId: UUID, @PathVariable statusId:UUID) : StatusDeletedEvent{
//        return tasksEsService.update(projectId) { it.deleteStatus(statusId) }
//    }

}

data class CreateProjectDto (
        val projectName: String,
        val creatorId: UUID
)

data class UpdateProjectDto(
        val name: String
)

data class AddParticipantDto (
        val userId: UUID
)

data class CreateStatusDto (
        val statusName: String,
        val colour: String
)