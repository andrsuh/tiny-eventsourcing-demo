package ru.quipy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication


@SpringBootApplication
//@ComponentScan("ru.quipy")

class DemoApplication

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}
