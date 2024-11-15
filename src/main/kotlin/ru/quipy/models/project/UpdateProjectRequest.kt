package ru.quipy.models.project

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateProjectRequest(
    @JsonProperty("title") val title: String?,
    @JsonProperty("description") val description: String?
)