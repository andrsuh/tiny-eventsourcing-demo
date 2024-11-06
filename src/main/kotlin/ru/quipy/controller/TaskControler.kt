package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.task.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.task.*
import ru.quipy.projections.ProjectionsService
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>,
    val projectionsService: ProjectionsService,
){
    @PostMapping("/{projectId}/task")
    fun createTask(@PathVariable projectId: UUID, @RequestParam taskName: String) : TaskCreatedEvent {
        return taskEsService.create { it.createTask(projectId, UUID.randomUUID(), taskName) }
    }

    @GetMapping("/{taskId}")
    fun getTask (@PathVariable taskId: UUID): TaskAggregateState? {
        return taskEsService.getState(taskId)
    }

    @PostMapping("/{taskId}/changeTitle/")
    fun changeTaskTitle(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @RequestParam newName: String) : TaskNameChangeEvent {
        return taskEsService.update(taskId){
            it.changeTaskTitle(newName)
        }
    }

    @PostMapping("/{taskId}/addUser")
    fun addExecutorToTask(@PathVariable taskId: UUID, @RequestParam userId: UUID) : TaskExecutorAssignedEvent?{
        return taskEsService.update(taskId){
            it.addExecutorToTask(userId)
        }
    }

    @PostMapping("/{taskId}/changeStatus/")
    fun changeTaskStatus(@PathVariable taskId: UUID,@RequestParam newStatusId: UUID) : TaskStatusChangeEvent {
        return taskEsService.update(taskId){
            it.changeTaskStatus(newStatusId, projectionsService.statusProjectProjectionRepo)
        }
    }
}