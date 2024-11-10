package ru.quipy.controller

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateUserRequest(
    @JsonProperty("name") val name: String,
    @JsonProperty("secret") val secret: String
)