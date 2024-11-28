package ru.quipy.projections.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "project-participants")
data class ProjectParticipantProjection (
    @Id
    val projectId: UUID,
    val participants: MutableMap<UUID, ParticipantProjection> = mutableMapOf(),
)

data class ParticipantProjection (
    val userId: UUID,
    val userLogin: String,
)