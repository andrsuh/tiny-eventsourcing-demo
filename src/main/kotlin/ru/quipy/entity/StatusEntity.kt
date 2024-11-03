package ru.quipy.entity

import ru.quipy.enum.ColorEnum
import java.util.*

data class StatusEntity(
        val id: UUID,
        val name: String,
        val projectId: UUID,
        val color: ColorEnum,
        var position: Int,
)

