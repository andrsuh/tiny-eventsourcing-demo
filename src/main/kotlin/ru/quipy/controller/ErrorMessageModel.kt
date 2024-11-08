package ru.quipy.controller

import java.sql.Timestamp

data class ErrorMessageModel(
        val timestamp: Timestamp? = null,
        val status: Int? = null,
        val message: String? = null
)