package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.ParticipantAddedToProjectEvent
import ru.quipy.api.ParticipantAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ParticipantAggregateState
import java.util.*

@RestController
@RequestMapping("/participants")
class ParticipantController(
    val participantEsService: EventSourcingService<UUID, ParticipantAggregate, ParticipantAggregateState>,
) {
    @PostMapping("/{participantId}/add-project")
    fun createParticipant(@PathVariable participantId: UUID, @RequestParam projectId: UUID): ParticipantAddedToProjectEvent {
        return participantEsService.update(participantId) {
            it.addProject(projectId)
        }
    }

    @GetMapping("/{participantId}")
    fun getParticipant(@PathVariable participantId: UUID) : ParticipantAggregateState? {
        return participantEsService.getState(participantId)
    }
}