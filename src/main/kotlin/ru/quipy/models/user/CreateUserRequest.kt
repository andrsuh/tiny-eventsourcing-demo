package ru.quipy.models.user

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateUserRequest(
    @JsonProperty("username") val username: UsernameDto,
    @JsonProperty("login") val login: String,
    @JsonProperty("password") val password: String
)

data class UsernameDto(
    @JsonProperty("firstName") val firstName: String,
    @JsonProperty("lastName") val lastName: String,
    @JsonProperty("middleName") val middleName: String?
)