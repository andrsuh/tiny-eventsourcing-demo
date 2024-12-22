package ru.quipy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.quipy.projections.Task
import ru.quipy.projections.TaskService
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskProjectionController(
        val taskService: TaskService
) {
    @GetMapping("/{projectId}/{userId}")
    fun getByProjectIdAndUserId(@PathVariable projectId: UUID, @PathVariable userId: UUID): List<Task> {
        return taskService.getAllByProjectIdAndUserId(projectId, userId)
    }

    @GetMapping("/{projectId}")
    fun getByProjectId(@PathVariable projectId: UUID): List<Task> {
        return taskService.getAllByProjectId(projectId)
    }
}
