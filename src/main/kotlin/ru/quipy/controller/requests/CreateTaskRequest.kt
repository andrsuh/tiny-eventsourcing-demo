package ru.quipy.controller.requests

import java.util.*

data class CreateTaskRequest(val projectId: UUID, val taskTitle: String, val executorId: UUID, val statusId: UUID)
