package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@SpringBootTest
class ProjectTests {
    @Autowired
    lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    @Autowired
    lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    fun cleanDatabase() {
        mongoTemplate.getCollection("aggregate-user").drop()
        mongoTemplate.getCollection("aggregate-project").drop()
    }

    @Test
    fun createProject() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"

        projectEsService.create { it.create(projectId, title, description) }
        val project = projectEsService.getState(projectId)

        Assertions.assertNotNull(project)
        if (project != null) {
            Assertions.assertEquals(projectId, project.getId())
            Assertions.assertEquals(title, project.projectTitle)
            Assertions.assertEquals(description, project.projectDescription)
            Assertions.assertEquals(0, project.tasks.size)
            Assertions.assertEquals(1, project.projectStatuses.size)
        }
    }

    @Test
    fun updateProject() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val newTitle = "newTitle"
        val newDescription = "newDescription"

        projectEsService.create { it.create(projectId, title, description) }
        projectEsService.update(projectId) {it.update(newTitle, newDescription)}
        val project = projectEsService.getState(projectId)

        Assertions.assertNotNull(project)
        if (project != null) {
            Assertions.assertEquals(projectId, project.getId())
            Assertions.assertEquals(newTitle, project.projectTitle)
            Assertions.assertEquals(newDescription, project.projectDescription)
            Assertions.assertEquals(0, project.tasks.size)
            Assertions.assertEquals(1, project.projectStatuses.size)
        }
    }

    @Test
    fun createProject_addTask() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val taskName = "task"
        val taskDescription = "taskDescription"

        projectEsService.create { it.create(projectId, title, description) }
        projectEsService.update(projectId) { it.addTask(taskName, taskDescription) }
        val project = projectEsService.getState(projectId)

        Assertions.assertNotNull(project)
        if (project != null) {
            Assertions.assertEquals(1, project.tasks.size)
            Assertions.assertEquals(taskName, project.tasks.values.first().name)
            Assertions.assertEquals(taskDescription, project.tasks.values.first().description)
        }
    }

    @Test
    fun addStatus() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val statusName = "Status"

        projectEsService.create { it.create(projectId, title, description) }
        projectEsService.update(projectId) { it.createStatus(statusName) }
        val project = projectEsService.getState(projectId)

        Assertions.assertNotNull(project)
        if (project != null) {
            Assertions.assertEquals(2, project.projectStatuses.size)
            Assertions.assertEquals(statusName, project.projectStatuses.values.find { it.name == statusName }!!.name)
        }
    }

    @Test
    fun addStatusTwice() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val statusName = "Status"

        projectEsService.create { it.create(projectId, title, description) }
        projectEsService.update(projectId) { it.createStatus(statusName) }

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            projectEsService.update(projectId) { it.createStatus(statusName) }
        }
    }

    @Test
    fun deleteStatus() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val statusName = "Status"

        projectEsService.create { it.create(projectId, title, description) }
        val statusId = projectEsService.update(projectId) { it.createStatus(statusName) }.statusId
        projectEsService.update(projectId) {it.deleteStatus(statusId) }

        val project = projectEsService.getState(projectId)

        Assertions.assertNotNull(project)
        if (project != null) {
            Assertions.assertEquals(1, project.projectStatuses.size)
            Assertions.assertEquals(null, project.projectStatuses.values.find { it.name == statusName })
        }
    }

    @Test
    fun deleteStatus_statusDoesntExists() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val statusName = "Status"

        projectEsService.create { it.create(projectId, title, description) }
        val statusId = UUID.randomUUID()

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            projectEsService.update(projectId) {it.deleteStatus(statusId) }
        }
    }

    @Test
    fun createStatus_assignTaskToStatus() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val statusName = "Status"
        val taskName = "task"
        val taskDescription = "taskDescription"

        projectEsService.create { it.create(projectId, title, description) }
        val statusId = projectEsService.update(projectId) { it.createStatus(statusName) }.statusId
        val taskId = projectEsService.update(projectId) { it.addTask(taskName, taskDescription) }.taskId

        projectEsService.update(projectId) {it.assignStatusToTask(statusId=statusId, taskId=taskId) }
        val task = projectEsService.getState(projectId)!!.tasks.values.find { it.id == taskId }

        Assertions.assertNotNull(task)
        if (task != null) {
            Assertions.assertEquals(statusId, task.statusAssigned)
        }
    }

    @Test
    fun createStatus_assignTaskToNotExistStatus() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val taskName = "task"
        val taskDescription = "taskDescription"

        projectEsService.create { it.create(projectId, title, description) }
        val statusId = UUID.randomUUID()
        val taskId = projectEsService.update(projectId) { it.addTask(taskName, taskDescription) }.taskId

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            projectEsService.update(projectId) {it.assignStatusToTask(statusId=statusId, taskId=taskId) }
        }
    }

    @Test
    fun createProject_deleteStatusWithTasks() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val statusName = "Status"
        val taskName = "task"
        val taskDescription = "taskDescription"

        projectEsService.create { it.create(projectId, title, description) }
        val statusId = projectEsService.update(projectId) { it.createStatus(statusName) }.statusId
        val taskId = projectEsService.update(projectId) { it.addTask(taskName, taskDescription) }.taskId
        projectEsService.update(projectId) {it.assignStatusToTask(statusId=statusId, taskId=taskId) }

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            projectEsService.update(projectId) {it.deleteStatus(statusId) }
        }
    }

    @Test
    fun createProject_changeStatusOrderFrom1ToLast() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val statusName1 = "Status1"
        val statusName2 = "Status2"
        val statusName3 = "Status3"

        projectEsService.create { it.create(projectId, title, description) }
        val statusId1 = projectEsService.update(projectId) { it.createStatus(statusName1) }.statusId
        val statusId2 = projectEsService.update(projectId) { it.createStatus(statusName2) }.statusId
        val statusId3 = projectEsService.update(projectId) { it.createStatus(statusName3) }.statusId
        val newOrderIndex = 4
        projectEsService.update(projectId) {it.changeStatusOrder(statusId1, newOrderIndex)}
        val statuses = projectEsService.getState(projectId)!!.projectStatuses.values.sortedBy { it.order }

        Assertions.assertEquals(4, statuses.size)
        Assertions.assertEquals(statusId2, statuses[1].id)
        Assertions.assertEquals(statusId3, statuses[2].id)
        Assertions.assertEquals(statusId1, statuses[3].id)
    }

    @Test
    fun updateTask() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val taskName = "task"
        val taskDescription = "taskDescription"
        val newTaskName = "JoJo"
        val newTaskDescription = "D4C"

        projectEsService.create { it.create(projectId, title, description) }
        val taskId = projectEsService.update(projectId) { it.addTask(taskName, taskDescription) }.taskId
        projectEsService.update(projectId) { it.updateTask(taskId, newTaskName, newTaskDescription) }

        val task = projectEsService.getState(projectId)!!.tasks.values.find { it.id == taskId }

        Assertions.assertNotNull(task)
        if (task != null) {
            Assertions.assertEquals(newTaskName, task.name)
            Assertions.assertEquals(newTaskDescription, task.description)
        }
    }

    @Test
    fun updateTask_taskDoesntExist() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val newTaskName = "JoJo"
        val newTaskDescription = "D4C"

        projectEsService.create { it.create(projectId, title, description) }
        val taskId = UUID.randomUUID()

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            projectEsService.update(projectId) { it.updateTask(taskId, newTaskName, newTaskDescription) }
        }
    }

    @Test
    fun assignTaskPerformer() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val newTaskName = "JoJo"
        val newTaskDescription = "D4C"
        val userId = UUID.randomUUID()
        val login = "testUser"
        val password = "testPassword"
        val username = "user"
        val taskName = "task"
        val taskDescription = "taskDescription"

        userEsService.create { it.create(userId, login, password, username) }
        val user = userEsService.getState(userId)
        projectEsService.create { it.create(projectId, title, description) }
        val taskId = projectEsService.update(projectId) { it.addTask(taskName, taskDescription) }.taskId
        projectEsService.update(projectId) {it.assignTaskPerformer(taskId=taskId, user=user) }

        val task = projectEsService.getState(projectId)!!.tasks.values.find { it.id == taskId }

        Assertions.assertNotNull(task)
        if (task != null) {
            Assertions.assertEquals(1, task.performers.size)
            Assertions.assertEquals(userId, task.performers[0])
        }

    }

}