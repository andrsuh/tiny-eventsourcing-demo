package ru.quipy.models.project

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateProjectRequest(
    @JsonProperty("projectTitle") val projectTitle: String
)