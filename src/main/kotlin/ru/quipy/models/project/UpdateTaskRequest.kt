package ru.quipy.models.project

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateTaskRequest(
    @JsonProperty("taskName") val taskName: String?,
    @JsonProperty("taskDescription") val taskDescription: String?
)