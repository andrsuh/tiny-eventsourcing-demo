package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.status.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.status.*
import java.util.*

@RestController
@RequestMapping("/statuses")
class StatusController(
         val statusEsService: EventSourcingService<UUID, StatusAggregate, StatusAggregateState>
) {
    @PostMapping("/create")
    fun createStatus(
            @RequestBody request: CreateStatusRequest
    ): StatusCreatedEvent {
        return statusEsService.create { it.createStatus(request.name, request.color) }
    }

    @PostMapping("/updateTitle")
    fun updateTitle(
            @RequestBody request: UpdateStatusTitleRequest
    ): StatusTitleUpdatedEvent {
        return statusEsService.update(request.statusId) { it.updateTitle(request.statusId, request.newTitle) }
    }

    @PostMapping("/updateColor")
    fun updateColor(
            @RequestBody request: UpdateStatusColorRequest
    ): StatusColorUpdatedEvent {
        return statusEsService.update(request.statusId) { it.updateColor(request.statusId, request.newColor) }
    }
}

data class CreateStatusRequest(
        val name: String,
        val color: String
)

data class UpdateStatusTitleRequest(
        val statusId: UUID,
        val newTitle: String
)

data class UpdateStatusColorRequest(
        val statusId: UUID,
        val newColor: String
)