package ru.quipy.projections

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID


@Document("user.name")
data class UserWithName (
    @Id
    val userId: UUID,
    val name: String
)

@Document("task")
data class Task (
    @Id
    val taskId: UUID,
    val projectId: UUID,
    val creatorId : UUID,
    val status: String,
    val executors: MutableList<UUID>,
    val name: String,
)

@Document("project.name")
data class ProjectWithName (
    @Id
    val projectId: UUID,
    val projectName: String
)

@Document("project")
data class Project (
    @Id
    val projectId: UUID,
    val ownerId: UUID,
    val name: String,
    val participants: MutableList<UUID>,
    val statuses: MutableList<UUID>
)

@Document("status")
data class Status (
    @Id
    val statusId: UUID,
    val name: String
)