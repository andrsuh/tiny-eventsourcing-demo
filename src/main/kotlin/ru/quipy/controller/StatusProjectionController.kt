package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.projections.Status
import ru.quipy.projections.StatusService
import java.util.*

@RestController
@RequestMapping("/statuses")
class StatusProjectionController(
        val statusService: StatusService
) {
    @GetMapping("/{projectId}")
    fun getByProjectId(@PathVariable projectId: UUID): List<Status> {
        return statusService.getAllByProjectId(projectId)
    }
}