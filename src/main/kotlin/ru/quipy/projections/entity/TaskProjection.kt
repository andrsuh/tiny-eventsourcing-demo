package ru.quipy.projections.entity

import java.util.*

data class TaskProjection(
        val taskId: UUID,
        var name: String,
        var description: String,
        var statusId: UUID,
        var performers: MutableList<UUID> = mutableListOf()
)
