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
import ru.quipy.projections.projectParticipants.ProjectParticipantService
import ru.quipy.projections.repository.TaskInfoRepository
import ru.quipy.projections.statusesWithTasks.StatusesWithTasksServices
import ru.quipy.projections.views.ProjectParticipantsView
import java.util.*
import java.util.concurrent.TimeUnit

@SpringBootTest
class ProjectParticipantsProjection {
    companion object {
        private const val timeout_time : Long = 40
    }
    @Autowired
    lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    @Autowired
    lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    @Autowired
    lateinit var projectParticipantService: ProjectParticipantService


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

        var projectProjection: ProjectParticipantsView? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            projectProjection = projectParticipantService.findUsersByProject(projectId)
            Assertions.assertNotNull(projectProjection)
        }

        Assertions.assertNotNull(projectProjection)
        Assertions.assertEquals(projectId, projectProjection!!.projectId)
        Assertions.assertEquals(0, projectProjection!!.participants.count())
    }

    @Test
    fun addUserToProject() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        var userId1 = UUID.randomUUID()
        val login1 = "testUser"
        val password1 = "testPassword"
        val username1 = "user"


        userEsService.create { it.create(userId1, login1, password1, username1) }
        projectEsService.create { it.create(projectId, title, description) }
        val project = projectEsService.getState(projectId)
        userEsService.update(userId1) {it.addProject(project)}

        projectEsService.create { it.create(projectId, title, description) }

        var projectProjection: ProjectParticipantsView? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            projectProjection = projectParticipantService.findUsersByProject(projectId)
            Assertions.assertNotNull(projectProjection)
            Assertions.assertEquals(1, projectProjection!!.participants.count())
        }

        Assertions.assertNotNull(projectProjection)
        Assertions.assertEquals(1, projectProjection!!.participants.count())
        val user = projectProjection!!.participants.first()
        Assertions.assertEquals(login1, user.login)
    }
}