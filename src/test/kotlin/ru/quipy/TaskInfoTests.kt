package ru.quipy

import org.awaitility.Awaitility
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import ru.quipy.StatusesWithTasksTests.Companion
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import ru.quipy.projections.entity.StatusesWithTasksProjection
import ru.quipy.projections.entity.TaskInfoProjection
import ru.quipy.projections.repository.TaskInfoRepository
import ru.quipy.projections.statusesWithTasks.StatusesWithTasksServices
import ru.quipy.projections.taskInfo.TaskInfoService
import ru.quipy.projections.views.TaskInfoView
import java.util.*
import java.util.concurrent.TimeUnit

@SpringBootTest
class TaskInfoTests {
    companion object {
        private const val timeout_time : Long = 40
    }
    @Autowired
    lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    @Autowired
    lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    @Autowired
    lateinit var statusesWithTasksServices: StatusesWithTasksServices

    @Autowired
    lateinit var taskInfoService: TaskInfoService

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    fun cleanDatabase() {
        mongoTemplate.getCollection("aggregate-user").drop()
        mongoTemplate.getCollection("aggregate-project").drop()
    }

    @Test
    fun createTask_addParticipant() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val taskName = "task"
        val taskDescription = "taskDescription"
        val login = "testUser"
        val password = "testPassword"
        val username = "user"
        val userId = UUID.randomUUID()

        projectEsService.create { it.create(projectId, title, description) }
        projectEsService.update(projectId) { it.addTask(taskName, taskDescription) }
        val project = projectEsService.getState(projectId)
        userEsService.create { it.create(userId, login, password, username) }
        userEsService.update(userId) {it.addProject(project)}
        val user = userEsService.getState(userId)

        val taskId = project!!.tasks.values.first().id
        projectEsService.update(projectId) { it.assignTaskPerformer(taskId, user) }
        var taskProjection: TaskInfoView? = null
        Awaitility.await().timeout(TaskInfoTests.timeout_time, TimeUnit.SECONDS).untilAsserted {
            taskProjection = taskInfoService.getTaskInfoById(taskId)
            Assertions.assertNotNull(taskProjection)
            Assertions.assertEquals(1, taskProjection!!.performers.count())
        }

        Assertions.assertNotNull(taskProjection)
        Assertions.assertEquals(taskName, taskProjection!!.title)
        Assertions.assertEquals(taskDescription, taskProjection!!.description)
        Assertions.assertEquals(1, taskProjection!!.performers.count())
        val performer =  taskProjection!!.performers.first()
        Assertions.assertEquals(userId, performer.userId)
        Assertions.assertEquals(login, performer.login)
    }

}