package ru.quipy.models

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateProjectRequest(
    @JsonProperty("title") val title: String?,
    @JsonProperty("description") val description: String?
)