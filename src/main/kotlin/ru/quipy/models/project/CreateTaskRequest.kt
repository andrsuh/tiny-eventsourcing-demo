package ru.quipy.models.project

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateTaskRequest(
    @JsonProperty("taskName") val taskName: String
)