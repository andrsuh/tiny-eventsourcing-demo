package ru.quipy.models

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateProjectRequest(
    @JsonProperty("projectTitle") val projectTitle: String
)