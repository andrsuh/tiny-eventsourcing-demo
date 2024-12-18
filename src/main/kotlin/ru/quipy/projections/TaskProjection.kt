package ru.quipy.projections

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "task_executors")
data class TaskProjection(
    val taskId: UUID,
    val name: String,
    val executorsAssigned: MutableSet<ExecutorProjection>
    )

data class ExecutorProjection(
    val userId: UUID,
    val fio: String,
)