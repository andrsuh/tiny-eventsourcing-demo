package ru.quipy.dto.project

import java.util.*

data class CreateProjectDto(
        val projectName: String,
        val creatorId: UUID
)
