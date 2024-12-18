package ru.quipy.projections

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "project_participants")
data class ProjectProjection(
    val projectId: UUID,
    val projectTitle: String,
    val creatorId: ParticipantProjection,
    val members: Set<ParticipantProjection>
)

data class ParticipantProjection(
    val userId: UUID,
    val username: String
)