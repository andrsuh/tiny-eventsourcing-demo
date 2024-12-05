package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.projections.Task
import ru.quipy.services.TaskProjectionsService
import java.util.UUID

@RestController
@RequestMapping("/tasks")
class TaskProjectionController(
    private val taskService: TaskProjectionsService
) {
    @GetMapping("/{taskId}")
    fun getByTaskId(@PathVariable taskId: UUID): Task {
        return taskService.getById(taskId)
    }

    @GetMapping("/name/{name}")
    fun getByTaskName(@PathVariable name: String): Task {
        return taskService.getByName(name)
    }
}