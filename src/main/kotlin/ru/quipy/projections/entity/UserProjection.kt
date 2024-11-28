package ru.quipy.projections.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "users")
data class UserProjection(
        @Id
        val userId: UUID,
        @Indexed(unique = true)
        var login: String,
        var username: String,
)
