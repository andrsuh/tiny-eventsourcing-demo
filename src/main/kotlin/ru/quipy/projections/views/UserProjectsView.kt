package ru.quipy.projections.views

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

data class UserProjectsView (
    val userId: UUID,
    var userLogin: String,
    var username: String,
    val projects: ArrayList<ProjectView> = ArrayList<ProjectView>(),
)

@Document(collection = "projects")
data class ProjectView (
    @Id
    val projectId: UUID,
    val title: String,
    val description: String,
)