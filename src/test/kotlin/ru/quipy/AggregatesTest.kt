package ru.quipy

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import ru.quipy.utils.createProject
import ru.quipy.utils.createStatus
import ru.quipy.utils.createTask
import ru.quipy.utils.createUser
import java.lang.IllegalArgumentException
import java.util.*


@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AggregatesTest {
    @Autowired
    private lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
    @Autowired
    private lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    val logger: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(AggregatesTest::class.java)

    @Test
    fun createUser_assertOk() {
        val username = "name"
        val realname = "real"
        val password = "12345"
        val uuid = createUser(userEsService, username, realname, password)
        val user = userEsService.getState(uuid)
        Assertions.assertNotNull(user)

        Assertions.assertEquals(username, user!!.username)
        Assertions.assertEquals(realname, user.realname)
        Assertions.assertEquals(password, user.password)
    }

    @Test
    fun createUser_createProject_assertOk() {
        val username = "name"
        val realname = "real"
        val password = "12345"
        val userId = createUser(userEsService, username, realname, password)

        val title = "Project 1"
        val data = createProject(projectEsService, title, userId)

        val uuid = data.first
        val userMemberId = data.second
        val defaultStatusId = data.third

        val project = projectEsService.getState(uuid)

        Assertions.assertNotNull(project)

        Assertions.assertEquals(title, project!!.projectTitle)
        Assertions.assertEquals(userMemberId, project.members[userMemberId]!!.id)
        Assertions.assertEquals(userId, project.members[userMemberId]!!.userId)
        Assertions.assertNotNull(project.statuses[defaultStatusId])
    }

    @Test
    fun createStatus_assertOk() {
        val username = "name"
        val realname = "real"
        val password = "12345"
        val userId = createUser(userEsService, username, realname, password)

        val title = "Project 1"
        val data = createProject(projectEsService, title, userId)

        val projectId = data.first

        val statusText = "Status 1"
        val statusColor = "Black"
        val statusId = createStatus(projectEsService, projectId, statusText, statusColor)

        val project = projectEsService.getState(projectId)

        Assertions.assertNotNull(project!!.statuses[statusId])
        Assertions.assertEquals(statusColor, project.statuses[statusId]!!.statusColor)
        Assertions.assertEquals(statusText, project.statuses[statusId]!!.statusText)
    }

    @Test
    fun createTask_assertOk() {
        val username = "name"
        val realname = "real"
        val password = "12345"
        val userId = createUser(userEsService, username, realname, password)

        val title = "Project 1"
        val data = createProject(projectEsService, title, userId)

        val projectId = data.first
        val defaultStatusId = data.third

        val taskName = "Task 1"
        val taskId = createTask(projectEsService, projectId, taskName, defaultStatusId)

        val project = projectEsService.getState(projectId)

        Assertions.assertNotNull(project!!.tasks[taskId])

        Assertions.assertEquals(defaultStatusId, project.tasks[taskId]!!.status)
        Assertions.assertEquals(taskName, project.tasks[taskId]!!.name)
        Assertions.assertEquals(0, project.tasks[taskId]!!.executors.size)
    }

    @Test
    fun tryToAssignOnTaskFromOtherProject_ThrowException() {
        val userId1 = createUser(userEsService, "k", "k", "k")
        val userId2 = createUser(userEsService, "l", "k", "k")

        val data1 = createProject(projectEsService, "k", userId1)
        val data2 = createProject(projectEsService, "k", userId2)

        val projectId1 = data1.first
        val defaultStatusId = data1.third

        val userMemberId2 = data2.second

        val taskName = "Task 1"
        val taskId = createTask(projectEsService, projectId1, taskName, defaultStatusId)

        Assertions.assertThrows(IllegalArgumentException::class.java) {projectEsService.update(projectId1) { it.assignUser(UUID.randomUUID(), taskId, userMemberId2) }}
    }

    @Test
    fun tryToAddExistingUserMember_ThrowException() {
        val userId1 = createUser(userEsService, "k", "k", "k")

        val data1 = createProject(projectEsService, "k", userId1)

        val projectId1 = data1.first

        Assertions.assertThrows(IllegalArgumentException::class.java) {projectEsService.update(projectId1) { it.addUser(userId1, UUID.randomUUID()) }}
    }

    @Test
    fun assignTask_assertOK() {
        val userId1 = createUser(userEsService, "k", "k", "k")

        val data1 = createProject(projectEsService, "k", userId1)

        val projectId1 = data1.first
        val projectMemberId1 = data1.second
        val defaultStatusId = data1.third

        val taskName = "Task 1"
        val taskId = createTask(projectEsService, projectId1, taskName, defaultStatusId)

        val executorId = UUID.randomUUID()
        projectEsService.update(projectId1) { it.assignUser(executorId, taskId, projectMemberId1) }

        val project = projectEsService.getState(projectId1)

        Assertions.assertNotNull(project!!.tasks[taskId]!!.executors[executorId])
        Assertions.assertEquals(projectMemberId1, project.tasks[taskId]!!.executors[executorId]!!.projectMemberId)
    }


    @Test
    fun addTaskToProject1WithProject2Default_ThrowException() {
        val userId = createUser(userEsService, "k", "k", "k")
        val dataProject1 = createProject(projectEsService, "Project 1",  userId)
        val dataProject2 = createProject(projectEsService, "Project 2", userId)

        val projectId1 = dataProject1.first
        val statusIdDefault2 = dataProject2.third

        val taskId = UUID.randomUUID()
        Assertions.assertThrows(IllegalArgumentException::class.java) {projectEsService.update(projectId1) { it.createTask(taskId, "testTask", statusIdDefault2)}}
    }

    @Test
    fun changeTaskTitle_assertOk() {
        val userId1 = createUser(userEsService, "k", "k", "k")

        val data1 = createProject(projectEsService, "k", userId1)

        val projectId1 = data1.first
        val defaultStatusId = data1.third

        val taskName = "Task 1"
        val taskId = createTask(projectEsService, projectId1, taskName, defaultStatusId)

        projectEsService.update(projectId1) { it.changeTaskTitle(taskId, "New Title")}
        val project = projectEsService.getState(projectId1)

        Assertions.assertNotNull(project!!.tasks[taskId])
        Assertions.assertEquals("New Title", project.tasks[taskId]!!.name)
    }

    @Test
    fun changeTitleOfOtherProjectTask_throwException() {
        val userId1 = createUser(userEsService, "k", "k", "k")

        val data1 = createProject(projectEsService, "k", userId1)
        val data2 = createProject(projectEsService, "k", userId1)

        val projectId1 = data1.first
        val defaultStatusId1 = data1.third

        val projectId2 = data2.first
        val defaultStatusId2 = data2.third

        val taskName = "Task project 1"
        createTask(projectEsService, projectId1, taskName, defaultStatusId1)

        val taskProject2Id = createTask(projectEsService, projectId2, "Task project 2", defaultStatusId2)

        Assertions.assertThrows(IllegalArgumentException::class.java) { projectEsService.update(projectId1) { it.changeTaskTitle(taskProject2Id, "New Title") } }
    }

    @Test
    fun addTaskToProject1WithProject1Default_tryToAssignDefault2_ThrowException_tryToAssignInProject2_ThrowException() {
        val userId = createUser(userEsService, "k", "k", "k")
        val dataProject1 = createProject(projectEsService, "Project 1",  userId)
        val dataProject2 = createProject(projectEsService, "Project 2", userId)

        val projectId1 = dataProject1.first
        val statusIdDefault1 = dataProject1.third

        val statusIdDefault2 = dataProject2.third

        val taskId = createTask(projectEsService, projectId1, "k", statusIdDefault1)
        Assertions.assertThrows(IllegalArgumentException::class.java) {projectEsService.update(projectId1) { it.changeTaskStatus(taskId, statusIdDefault2)}}
    }

    @Test
    fun addTaskToProject1WithProject1Default_tryToAssignUserMember22_ThrowException() {
        val userId = createUser(userEsService, "k", "k", "k")
        val userId2 = createUser(userEsService, "k", "k", "k")
        val dataProject1 = createProject(projectEsService, "Project 1",  userId)
        val dataProject2 = createProject(projectEsService, "Project 2", userId2)

        val projectId1 = dataProject1.first
        val statusIdDefault1 = dataProject1.third

        val userMemberId22 = dataProject2.second

        val taskId = createTask(projectEsService, projectId1, "k", statusIdDefault1)
        Assertions.assertThrows(IllegalArgumentException::class.java) {projectEsService.update(projectId1) { it.assignUser(UUID.randomUUID(), taskId, userMemberId22)}}
    }

    @Test
    fun addTaskToProject1WithProject2Default_throwException() {
        val userId = createUser(userEsService, "k", "k", "k")
        val userId2 = createUser(userEsService, "k", "k", "k")
        val dataProject1 = createProject(projectEsService, "Project 1",  userId)
        val dataProject2 = createProject(projectEsService, "Project 2", userId2)

        val projectId1 = dataProject1.first
        val statusIdDefault2 = dataProject2.third
        Assertions.assertThrows(IllegalArgumentException::class.java) {createTask(projectEsService, projectId1, "k", statusIdDefault2)}
    }

    @Test
    fun addTaskToProject1_addStatus_assignNewStatusOnTask() {
        val userId = createUser(userEsService, "k", "k", "k")
        val dataProject1 = createProject(projectEsService, "Project 1",  userId)

        val projectId1 = dataProject1.first
        val defaultStatusId = dataProject1.third

        val taskId = createTask(projectEsService, projectId1, "k", defaultStatusId)
        val statusId = createStatus(projectEsService, projectId1, "k", "k")

        projectEsService.update(projectId1) { it.changeTaskStatus(taskId, statusId)}
        val project = projectEsService.getState(projectId1)
        Assertions.assertEquals(statusId, project!!.tasks[taskId]!!.status)
    }

    @Test
    fun addTaskToProject1_addStatus1_addStatus2_assignTaskToStatus1_tryToDeleteStatus1_ThrowException() {
        val userId = createUser(userEsService, "k", "k", "k")
        val dataProject1 = createProject(projectEsService, "Project 1",  userId)

        val projectId1 = dataProject1.first

        val statusId1 = createStatus(projectEsService, projectId1, "k", "k")
        val statusId2 = createStatus(projectEsService, projectId1, "k", "k")
        val taskId = createTask(projectEsService, projectId1, "k", statusId1)

        Assertions.assertThrows(IllegalArgumentException::class.java) { projectEsService.update(projectId1) { it.deleteStatus(statusId1) } }
    }

    @Test
    fun deleteEmptyStatus_assertDeleted() {
        val userId = createUser(userEsService, "k", "k", "k")
        val dataProject1 = createProject(projectEsService, "Project 1",  userId)

        val projectId1 = dataProject1.first

        val statusId1 = createStatus(projectEsService, projectId1, "k", "k")
        val statusId2 = createStatus(projectEsService, projectId1, "k", "k")
        val taskId = createTask(projectEsService, projectId1, "k", statusId1)

        projectEsService.update(projectId1) { it.changeTaskStatus(taskId, statusId2)}
        projectEsService.update(projectId1) { it.deleteStatus(statusId1) }

        val project = projectEsService.getState(projectId1)
        Assertions.assertNotNull(project!!.statuses[statusId2])
        Assertions.assertNull(project.statuses[statusId1])
    }
}