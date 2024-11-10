package ru.quipy

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
abstract class AbstractSlowTest {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    companion object {
        @Container
        private val postgresContainer = PostgreSQLContainer<Nothing>("postgres:14.9-alpine").apply {
            withDatabaseName("tiny_es")
            withUsername("tiny_es")
            withPassword("tiny_es")
        }

        @Container
        private val mongoContainer = MongoDBContainer("mongo:4.4.6").apply {
            withExposedPorts(27017)
        }

        @JvmStatic
        @BeforeAll
        fun startContainers() {
            postgresContainer.start()
            mongoContainer.start()
        }

        @JvmStatic
        @AfterAll
        fun stopContainers() {
            postgresContainer.stop()
            mongoContainer.stop()
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.hikari.jdbc-url") { postgresContainer.jdbcUrl }
            registry.add("spring.datasource.hikari.username") { postgresContainer.username }
            registry.add("spring.datasource.hikari.password") { postgresContainer.password }
            registry.add("spring.data.mongodb.host") { mongoContainer.replicaSetUrl }
            registry.add("spring.data.mongodb.port") { mongoContainer.firstMappedPort.toString() }
        }
    }

}