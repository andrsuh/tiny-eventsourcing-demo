package ru.quipy.projections.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "task-info")
data class TaskInfoProjection (
    @Id
    val taskId: UUID,
    val taskName: String,
    val taskDescription: String,
    val performers: MutableList<PerformerProjection> = mutableListOf(),
)

data class PerformerProjection (
    val userId: UUID,
    val userLogin: String,
)