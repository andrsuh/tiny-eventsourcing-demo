package ru.quipy.projections.views

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

data class TaskInfoView(
    val projectId: UUID,
    var title: String,
    var description: String,
    var performers: ArrayList<TaskPerformer> = ArrayList<TaskPerformer>(),
)

data class TaskPerformer(
    val userId: UUID,
    var login: String,
)