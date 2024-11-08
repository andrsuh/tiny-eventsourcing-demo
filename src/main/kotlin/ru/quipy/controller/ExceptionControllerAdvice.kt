package ru.quipy.controller

import javassist.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.sql.Timestamp

@ControllerAdvice
class ExceptionControllerAdvice {
    @ExceptionHandler
    fun handleIllegalStateException(ex: IllegalStateException): ResponseEntity<ErrorMessageModel> {
        val errorMessage = ErrorMessageModel(
                timestamp = Timestamp(System.currentTimeMillis()),
                status = HttpStatus.NOT_FOUND.value(),
                message = ex.message
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage)
    }

    @ExceptionHandler
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorMessageModel> {
        val errorMessage = ErrorMessageModel(
                timestamp = Timestamp(System.currentTimeMillis()),
                status = HttpStatus.BAD_REQUEST.value(),
                message = ex.message
        )

        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage)
    }

    @ExceptionHandler
    fun handleNotFoundException(ex: NotFoundException): ResponseEntity<ErrorMessageModel> {
        val errorMessage = ErrorMessageModel(
                timestamp = Timestamp(System.currentTimeMillis()),
                status = HttpStatus.NOT_FOUND.value(),
                message = ex.message
        )

        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage)
    }
}