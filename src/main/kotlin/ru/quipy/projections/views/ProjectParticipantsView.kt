package ru.quipy.projections.views

import org.springframework.data.annotation.Id
import java.util.*

data class ProjectParticipantsView (
    val projectId: UUID,
    val participants: ArrayList<Participants> = ArrayList<Participants>(),
)

data class Participants(
    val userId: UUID,
    val login: String,
)