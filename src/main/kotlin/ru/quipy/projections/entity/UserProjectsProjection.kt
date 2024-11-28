package ru.quipy.projections.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "user-projects")
data class UserProjectsProjection (
    @Id
    val userId: UUID,
    @Indexed(unique = true)
    var userLogin: String,
    var username: String,
    val projects: MutableMap<UUID, UserProjectProjection> = mutableMapOf(),
)

data class UserProjectProjection (
    val projectId: UUID,
    val title: String,
    val description: String,
)