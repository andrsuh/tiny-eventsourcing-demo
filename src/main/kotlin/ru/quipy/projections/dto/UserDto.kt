package ru.quipy.projections.dto

import java.util.*

data class UserDto (
    val userId: UUID,
    val name: String?,
    val nickname: String?
)