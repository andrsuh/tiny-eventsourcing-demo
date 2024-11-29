package ru.quipy.projections.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "task-info")
data class TaskInfoProjection (
    @Id
    var taskId: UUID,
    var taskName: String,
    var taskDescription: String,
    val performers: ArrayList<UUID> = ArrayList<UUID>()
)