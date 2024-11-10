package ru.quipy.controller

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class CreateProjectRequest(
    @JsonProperty("projectTitle") val projectTitle: String
)

data class AddParticipantRequest(
    @JsonProperty("userId") val userId: UUID
)

data class EditProjectNameRequest(
    @JsonProperty("newName") val newName: String
)

data class CreateTaskRequest(
    @JsonProperty("taskName") val taskName: String
)

data class CreateTaskStatusRequest(
    @JsonProperty("statusColor") val statusColor: String,
    @JsonProperty("statusName") val statusName: String
)

data class SetTaskStatusRequest(
    @JsonProperty("statusName") val statusName: String
)

data class EditTaskNameRequest(
    @JsonProperty("newTaskName") val newTaskName: String
)

data class SetTaskPerformerRequest(
    @JsonProperty("performerId") val performerId: UUID
)
