package ru.quipy.projections.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "projects")
data class ProjectProjection(
        @Id
        val projectId: UUID,
        var title: String,
        var description: String,
        var tasks: MutableList<TaskProjection> = mutableListOf(),
        var statuses: MutableList<StatusProjection> = mutableListOf(),
        var updatedAt: Long
)
