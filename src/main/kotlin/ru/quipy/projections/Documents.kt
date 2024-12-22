package ru.quipy.projections

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("statuses")
data class Status (
        @Id
        val id: UUID,
        val name: String,
        val projectId: UUID,
        val colorRed: Int,
        val colorGreen: Int,
        val colorBlue: Int
)

@Document("tasks")
data class Task (
        @Id
        val id: UUID,
        var taskName: String,
        var status: UUID,
        val projectID: UUID,
        var taskAssignees: Set<UUID>
)

@Document("projects")
data class Project (
        @Id
        val id: UUID,
        val name: String,
        var members: Set<UUID>
)

@Document("users")
data class User (
        @Id
        val id: UUID,
        val name: String
)