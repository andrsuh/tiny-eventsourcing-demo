package ru.quipy.models

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateTagRequest(
    @JsonProperty("name") val name: String,
    @JsonProperty("color") val color: String
)