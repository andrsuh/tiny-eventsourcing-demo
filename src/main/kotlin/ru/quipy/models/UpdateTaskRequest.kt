package ru.quipy.models

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateTaskRequest(
    @JsonProperty("taskName") val taskName: String?,
    @JsonProperty("taskDescription") val taskDescription: String?
)