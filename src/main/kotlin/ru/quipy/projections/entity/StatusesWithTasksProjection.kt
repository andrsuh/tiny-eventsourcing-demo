package ru.quipy.projections.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "statuses")
data class StatusesWithTasksProjection (
    @Id
    val projectId: UUID,
    val statuses: MutableList<StatusesProjection> = mutableListOf(),
)

data class StatusesProjection (
    val statusId: UUID,
    val statusName: String,
    var statusOrder: Int,
    val tasks: MutableList<TasksProjection> = mutableListOf(),
)

data class TasksProjection (
    val taskId: UUID,
    var taskName: String,
)