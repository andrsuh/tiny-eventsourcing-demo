package ru.quipy.logic

import java.util.*

data class StatusEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String
)