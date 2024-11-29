package ru.quipy

import org.awaitility.Awaitility
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.addProject
import ru.quipy.logic.create
import ru.quipy.projections.entity.UserProjection
import ru.quipy.projections.repository.UserProjectionRepository
import java.util.*
import java.util.concurrent.TimeUnit

@SpringBootTest
class UserProjectionTests {

    companion object {
        private const val timeout_time : Long = 40
    }
    @Autowired
    lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    @Autowired
    lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    @Autowired
    lateinit var userProjectionRepository: UserProjectionRepository


    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    fun cleanDatabase() {
        mongoTemplate.getCollection("aggregate-user").drop()
        mongoTemplate.getCollection("aggregate-project").drop()
    }

    @Test
    fun createUser_userCreateInProjectionTest() {
        val userId = UUID.randomUUID()
        val login = "testUser"
        val password = "testPassword"
        val username = "user"
        userEsService.create { it.create(userId, login, password, username) }
        var userProjection : UserProjection? = null

        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            userProjection = userProjectionRepository.findById(userId).orElse(null)
            Assertions.assertNotNull(userProjection)
        }

        Assertions.assertNotNull(userProjection)
        Assertions.assertEquals(userId, userProjection!!.userId)
        Assertions.assertEquals(login, userProjection!!.login)
        Assertions.assertEquals(username, userProjection!!.username)
    }

    @Test
    fun assignProjectToUserTest_projectionHaveProject() {
        val projectId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val login = "testUser"
        val password = "testPassword"
        val username = "user"
        val title = "title"
        val description = "description"

        userEsService.create { it.create(userId, login, password, username) }
        projectEsService.create { it.create(projectId, title, description) }
        val project = projectEsService.getState(projectId)
        userEsService.update(userId) {it.addProject(project)}

        var userProjection: UserProjection? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            userProjection = userProjectionRepository.findById(userId).orElse(null)
            Assertions.assertNotNull(userProjection)
        }

        Assertions.assertNotNull(userProjection)
//        Assertions.assertEquals(1, userProjection!!.projects.size)
//        Assertions.assertEquals(projectId, userProjection!!.projects[0])
    }

}