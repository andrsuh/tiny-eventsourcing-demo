package ru.quipy.projections.entity

import java.util.*

data class StatusProjection(
        val statusId: UUID,
        var name: String,
        var order: Int
)
