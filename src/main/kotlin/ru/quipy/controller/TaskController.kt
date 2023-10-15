package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.TaskAggregate
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.TaskAggregateState
import ru.quipy.logic.create
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController (
        val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>
) {
    @PostMapping("/{taskTitle")
    fun createTask(@PathVariable taskTitle: String, @RequestParam creatorId: String) : TaskCreatedEvent{
        return taskEsService.create { it.create(UUID.randomUUID(), taskTitle, creatorId, UUID.randomUUID())  }
    }

    @GetMapping("/{taskId}")
    fun getTask(@PathVariable taskId: UUID) : TaskAggregateState? {
        return taskEsService.getState(taskId)
    }
}