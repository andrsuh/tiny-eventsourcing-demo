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
import ru.quipy.logic.*
import ru.quipy.projections.entity.ProjectProjection
import ru.quipy.projections.repository.TaskInfoRepository
import java.util.*
import java.util.concurrent.TimeUnit

@SpringBootTest
class ProjectProjectionTests {
    companion object {
        private const val timeout_time : Long = 40
    }
    @Autowired
    lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    @Autowired
    lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    @Autowired
    lateinit var projectProjectionRepository: TaskInfoRepository

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

        var projectProjection: ProjectProjection? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            projectProjection = projectProjectionRepository.findById(projectId).orElse(null)
            Assertions.assertNotNull(projectProjection)
        }

        Assertions.assertNotNull(projectProjection)
        Assertions.assertEquals(projectId, projectProjection!!.projectId)
        Assertions.assertEquals(title, projectProjection!!.title)
        Assertions.assertEquals(description, projectProjection!!.description)
        Assertions.assertEquals(0, projectProjection!!.tasks.count())
    }

    @Test
    fun createTask() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val taskName = "task"
        val taskDescription = "taskDescription"

        projectEsService.create { it.create(projectId, title, description) }
        projectEsService.update(projectId) { it.addTask(taskName, taskDescription) }

        var projectProjection: ProjectProjection? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            projectProjection = projectProjectionRepository.findById(projectId).orElse(null)
            Assertions.assertNotNull(projectProjection)
        }

        Assertions.assertNotNull(projectProjection)
        Assertions.assertEquals(1, projectProjection!!.tasks.count())
        val resultTask = projectProjection!!.tasks.first()
        Assertions.assertEquals(taskName, resultTask.name)
        Assertions.assertEquals(taskDescription, resultTask.description)
    }

    @Test
    fun addStatus() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val statusName = "Status"

        projectEsService.create { it.create(projectId, title, description) }
        projectEsService.update(projectId) { it.createStatus(statusName) }

        var projectProjection: ProjectProjection? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            projectProjection = projectProjectionRepository.findById(projectId).orElse(null)
            Assertions.assertNotNull(projectProjection)
        }

        Assertions.assertNotNull(projectProjection)
        Assertions.assertNotNull(projectProjection)
        val resultStatus = projectProjection!!.statuses.find { it.name == statusName }
        Assertions.assertNotNull(resultStatus)
        Assertions.assertEquals(statusName, resultStatus!!.name)
    }

    @Test
    fun deleteStatus() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val statusName = "Status2"

        projectEsService.create { it.create(projectId, title, description) }
        val statusId = projectEsService.update(projectId) { it.createStatus(statusName) }.statusId
        projectEsService.update(projectId) {it.deleteStatus(statusId) }

        var projectProjection: ProjectProjection? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            projectProjection = projectProjectionRepository.findById(projectId).orElse(null)
            Assertions.assertNotNull(projectProjection)
            if (projectProjection != null) {
                Assertions.assertEquals(null, projectProjection!!.statuses.find { it.name == statusName })
            }
        }

        Assertions.assertNotNull(projectProjection)
        Assertions.assertEquals(null, projectProjection!!.statuses.find { it.name == statusName })
    }

}


