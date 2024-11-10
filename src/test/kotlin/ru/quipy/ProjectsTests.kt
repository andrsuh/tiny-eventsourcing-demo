package ru.quipy

import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.UserCreatedEvent
import java.util.*

class ProjectComponentTests: AbstractSlowTest() {

    @Test
    fun `create user`() {
        val userNickname = "user-${UUID.randomUUID()}"
        val requestBody = """
        {
            "name": "User Name",
            "secret": "password123"
        }
    """.trimIndent()

        mockMvc.perform(
            post("/users/$userNickname")
                .contentType("application/json")
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.userId").value(UUID.nameUUIDFromBytes(userNickname.toByteArray()).toString()))
            .andExpect(jsonPath("$.nickname").value(userNickname))
    }

    @Test
    fun `create project`() {
        val userNickname = "user-${UUID.randomUUID()}"
        val requestBody = """
        {
            "name": "User Name",
            "secret": "password123"
        }
    """.trimIndent()

        val userResponse = mockMvc.perform(
            post("/users/$userNickname")
                .contentType("application/json")
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val userId = objectMapper.readValue(userResponse.response.contentAsString, UserCreatedEvent::class.java).userId

        val projectTitle = "Project-${UUID.randomUUID()}"
        val projectRequestBody = """
        {
            "projectTitle": "$projectTitle"
        }
    """.trimIndent()

        mockMvc.perform(
            post("/projects?creatorId=$userId")
                .contentType("application/json")
                .content(projectRequestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.projectId").value(UUID.nameUUIDFromBytes(projectTitle.toByteArray()).toString()))
            .andExpect(jsonPath("$.title").value(projectTitle))
    }


    @Test
    fun `create task`() {
        val userNickname = "user-${UUID.randomUUID()}"
        val requestBody = """
        {
            "name": "User Name",
            "secret": "password123"
        }
    """.trimIndent()

        val userResponse = mockMvc.perform(
            post("/users/$userNickname")
                .contentType("application/json")
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val userId = objectMapper.readValue(userResponse.response.contentAsString, UserCreatedEvent::class.java).userId

        val projectTitle = "Project-${UUID.randomUUID()}"
        val projectRequestBody = """
        {
            "projectTitle": "$projectTitle"
        }
    """.trimIndent()

        val projectResponse = mockMvc.perform(
            post("/projects?creatorId=$userId")
                .contentType("application/json")
                .content(projectRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val projectId = objectMapper.readValue(projectResponse.response.contentAsString, ProjectCreatedEvent::class.java).projectId
 // Extract actual projectId from response if needed

        val statusName = "To Do"
        val statusRequestBody = """
        {
            "statusColor": "#FFFFFF",
            "statusName": "$statusName"
        }
    """.trimIndent()

        mockMvc.perform(
            post("/projects/$projectId/statuses?userId=$userId")
                .contentType("application/json")
                .content(statusRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val taskName = "Task-${UUID.randomUUID()}"
        val taskRequestBody = """
        {
            "taskName": "$taskName"
        }
    """.trimIndent()

        mockMvc.perform(
            post("/projects/$projectId/tasks")
                .param("userId", userId.toString())
                .contentType("application/json")
                .content(taskRequestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.taskName").value(taskName))
    }

    @Test
    fun `set task status`() {
        val userNickname = "user-${UUID.randomUUID()}"
        val requestBody = """
        {
            "name": "User Name",
            "secret": "password123"
        }
    """.trimIndent()

        val userResponse = mockMvc.perform(
            post("/users/$userNickname")
                .contentType("application/json")
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val userId = objectMapper.readValue(userResponse.response.contentAsString, UserCreatedEvent::class.java).userId

        val projectTitle = "Project-${UUID.randomUUID()}"
        val projectRequestBody = """
        {
            "projectTitle": "$projectTitle"
        }
    """.trimIndent()

        val projectResponse = mockMvc.perform(
            post("/projects?creatorId=$userId")
                .contentType("application/json")
                .content(projectRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val projectId = objectMapper.readValue(projectResponse.response.contentAsString, ProjectCreatedEvent::class.java).projectId

        val statusName = "In Progress"
        val statusRequestBody = """
        {
            "statusColor": "#FFFFFF",
            "statusName": "$statusName"
        }
    """.trimIndent()

        mockMvc.perform(
            post("/projects/$projectId/statuses?userId=$userId")
                .contentType("application/json")
                .content(statusRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val taskName = "Task-${UUID.randomUUID()}"
        val taskRequestBody = """
        {
            "taskName": "$taskName"
        }
    """.trimIndent()

        val taskResponse = mockMvc.perform(
            post("/projects/$projectId/tasks")
                .param("userId", userId.toString())
                .contentType("application/json")
                .content(taskRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val taskId =  objectMapper.readValue(taskResponse.response.contentAsString, TaskCreatedEvent::class.java).taskId// taskId for the task being updated
        val updatedStatus = "In Progress"
        val statusUpdateRequestBody = """
        {
            "statusName": "$updatedStatus"
        }
    """.trimIndent()

        mockMvc.perform(
            put("/projects/$projectId/tasks/$taskId/status?userId=$userId")
                .contentType("application/json")
                .content(statusUpdateRequestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.statusName").value(updatedStatus))
    }

    @Test
    fun `set task performer`() {
        val userNickname = "user-${UUID.randomUUID()}"
        val requestBody = """
        {
            "name": "User Name",
            "secret": "password123"
        }
    """.trimIndent()

        val userResponse = mockMvc.perform(
            post("/users/$userNickname")
                .contentType("application/json")
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val userId = objectMapper.readValue(userResponse.response.contentAsString, UserCreatedEvent::class.java).userId

        val projectTitle = "Project-${UUID.randomUUID()}"
        val projectRequestBody = """
        {
            "projectTitle": "$projectTitle"
        }
    """.trimIndent()

        val projectResponse = mockMvc.perform(
            post("/projects?creatorId=$userId")
                .contentType("application/json")
                .content(projectRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val projectId = objectMapper.readValue(projectResponse.response.contentAsString, ProjectCreatedEvent::class.java).projectId

        val statusName = "To Do"
        val statusRequestBody = """
        {
            "statusColor": "#FFFFFF",
            "statusName": "$statusName"
        }
    """.trimIndent()

        mockMvc.perform(
            post("/projects/$projectId/statuses?userId=$userId")
                .contentType("application/json")
                .content(statusRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val taskName = "Task-${UUID.randomUUID()}"
        val taskRequestBody = """
        {
            "taskName": "$taskName"
        }
    """.trimIndent()

        val taskResponse = mockMvc.perform(
            post("/projects/$projectId/tasks")
                .param("userId", userId.toString())
                .contentType("application/json")
                .content(taskRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val taskId =  objectMapper.readValue(taskResponse.response.contentAsString, TaskCreatedEvent::class.java).taskId// taskId for the task being updated

        val performerRequestBody = """
        {
            "performerId": "$userId"
        }
    """.trimIndent()

        mockMvc.perform(
            put("/projects/$projectId/tasks/$taskId/performer?userId=$userId")
                .contentType("application/json")
                .content(performerRequestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.performer").value(userId.toString()))
    }

    @Test
    fun `edit task name`() {
        val userNickname = "user-${UUID.randomUUID()}"
        val requestBody = """
        {
            "name": "User Name",
            "secret": "password123"
        }
    """.trimIndent()

        val userResponse = mockMvc.perform(
            post("/users/$userNickname")
                .contentType("application/json")
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val userId = objectMapper.readValue(userResponse.response.contentAsString, UserCreatedEvent::class.java).userId

        val projectTitle = "Project-${UUID.randomUUID()}"
        val projectRequestBody = """
        {
            "projectTitle": "$projectTitle"
        }
    """.trimIndent()

        val projectResponse = mockMvc.perform(
            post("/projects?creatorId=$userId")
                .contentType("application/json")
                .content(projectRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val projectId = objectMapper.readValue(projectResponse.response.contentAsString, ProjectCreatedEvent::class.java).projectId
 // Extract actual projectId from response if needed

        val statusName = "To Do"
        val statusRequestBody = """
        {
            "statusColor": "#FFFFFF",
            "statusName": "$statusName"
        }
    """.trimIndent()

        mockMvc.perform(
            post("/projects/$projectId/statuses?userId=$userId")
                .contentType("application/json")
                .content(statusRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val taskName = "Task-${UUID.randomUUID()}"
        val taskRequestBody = """
        {
            "taskName": "$taskName"
        }
    """.trimIndent()

        val taskResponse = mockMvc.perform(
            post("/projects/$projectId/tasks")
                .param("userId", userId.toString())
                .contentType("application/json")
                .content(taskRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val taskId =  objectMapper.readValue(taskResponse.response.contentAsString, TaskCreatedEvent::class.java).taskId// Extract actual taskId from response if needed
        val newTaskName = "Updated Task Name-${UUID.randomUUID()}"

        val updateRequestBody = """
        {
            "newTaskName": "$newTaskName"
        }
    """.trimIndent()

        mockMvc.perform(
            put("/projects/$projectId/tasks/$taskId/name?userId=$userId")
                .contentType("application/json")
                .content(updateRequestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.newTaskName").value(newTaskName))
    }

    @Test
    fun `edit project name`() {
        val userNickname = "user-${UUID.randomUUID()}"
        val requestBody = """
        {
            "name": "User Name",
            "secret": "password123"
        }
    """.trimIndent()

        val userResponse = mockMvc.perform(
            post("/users/$userNickname")
                .contentType("application/json")
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val userId = objectMapper.readValue(userResponse.response.contentAsString, UserCreatedEvent::class.java).userId

        val projectTitle = "Project-${UUID.randomUUID()}"
        val projectRequestBody = """
        {
            "projectTitle": "$projectTitle"
        }
    """.trimIndent()

        val projectResponse = mockMvc.perform(
            post("/projects?creatorId=$userId")
                .contentType("application/json")
                .content(projectRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val projectId = objectMapper.readValue(projectResponse.response.contentAsString, ProjectCreatedEvent::class.java).projectId

        val newProjectName = "Updated Project Name-${UUID.randomUUID()}"
        val projectUpdateRequestBody = """
        {
            "newName": "$newProjectName"
        }
    """.trimIndent()

        mockMvc.perform(
            put("/projects/$projectId/name?userId=$userId")
                .contentType("application/json")
                .content(projectUpdateRequestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.newProjectName").value(newProjectName))
    }

    @Test
    fun `delete task`() {
        val userNickname = "user-${UUID.randomUUID()}"
        val requestBody = """
        {
            "name": "User Name",
            "secret": "password123"
        }
    """.trimIndent()

        val userResponse = mockMvc.perform(
            post("/users/$userNickname")
                .contentType("application/json")
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val userId = objectMapper.readValue(userResponse.response.contentAsString, UserCreatedEvent::class.java).userId

        val projectTitle = "Project-${UUID.randomUUID()}"
        val projectRequestBody = """
        {
            "projectTitle": "$projectTitle"
        }
    """.trimIndent()

        val projectResponse = mockMvc.perform(
            post("/projects?creatorId=$userId")
                .contentType("application/json")
                .content(projectRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val projectId = objectMapper.readValue(projectResponse.response.contentAsString, ProjectCreatedEvent::class.java).projectId

        val statusName = "To Do"
        val statusRequestBody = """
        {
            "statusColor": "#FFFFFF",
            "statusName": "$statusName"
        }
    """.trimIndent()

        mockMvc.perform(
            post("/projects/$projectId/statuses?userId=$userId")
                .contentType("application/json")
                .content(statusRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val taskName = "Task-${UUID.randomUUID()}"
        val taskRequestBody = """
        {
            "taskName": "$taskName"
        }
    """.trimIndent()

        val taskResponse = mockMvc.perform(
            post("/projects/$projectId/tasks")
                .param("userId", userId.toString())
                .contentType("application/json")
                .content(taskRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()
        
        val taskId =  objectMapper.readValue(taskResponse.response.contentAsString, TaskCreatedEvent::class.java).taskId

        mockMvc.perform(
            delete("/projects/$projectId/tasks/$taskId?userId=$userId")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `delete task performer`() {
        val userNickname = "user-${UUID.randomUUID()}"
        val requestBody = """
        {
            "name": "User Name",
            "secret": "password123"
        }
    """.trimIndent()

        val userResponse = mockMvc.perform(
            post("/users/$userNickname")
                .contentType("application/json")
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val userId = objectMapper.readValue(userResponse.response.contentAsString, UserCreatedEvent::class.java).userId

        val projectTitle = "Project-${UUID.randomUUID()}"
        val projectRequestBody = """
        {
            "projectTitle": "$projectTitle"
        }
    """.trimIndent()

        val projectResponse = mockMvc.perform(
            post("/projects?creatorId=$userId")
                .contentType("application/json")
                .content(projectRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val projectId = objectMapper.readValue(projectResponse.response.contentAsString, ProjectCreatedEvent::class.java).projectId

        val statusName = "To Do"
        val statusRequestBody = """
        {
            "statusColor": "#FFFFFF",
            "statusName": "$statusName"
        }
    """.trimIndent()

        mockMvc.perform(
            post("/projects/$projectId/statuses?userId=$userId")
                .contentType("application/json")
                .content(statusRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val taskName = "Task-${UUID.randomUUID()}"
        val taskRequestBody = """
        {
            "taskName": "$taskName"
        }
    """.trimIndent()

        val taskResponse = mockMvc.perform(
            post("/projects/$projectId/tasks")
                .param("userId", userId.toString())
                .contentType("application/json")
                .content(taskRequestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val taskId =  objectMapper.readValue(taskResponse.response.contentAsString, TaskCreatedEvent::class.java).taskId// Extract actual taskId from response if needed

        val removePerformerRequestBody = """
        {
            "performerId": "$userId"
        }
    """.trimIndent()

        val performerRequestBody = """
        {
            "performerId": "$userId"
        }
    """.trimIndent()

        mockMvc.perform(
            put("/projects/$projectId/tasks/$taskId/performer?userId=$userId")
                .contentType("application/json")
                .content(performerRequestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.performer").value(userId.toString()))

        mockMvc.perform(
            delete("/projects/$projectId/tasks/$taskId/performer/$userId?userId=$userId")
                .contentType("application/json")
                .content(removePerformerRequestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.performer").value(userId.toString()))
    }

}

