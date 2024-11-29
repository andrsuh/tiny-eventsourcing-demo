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
import ru.quipy.projections.entity.StatusesWithTasksProjection
import ru.quipy.projections.repository.TaskInfoRepository
import ru.quipy.projections.statusesWithTasks.StatusesWithTasksServices
import java.util.*
import java.util.concurrent.TimeUnit

@SpringBootTest
class StatusesWithTasksTests {
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
    lateinit var projectProjectionRepository: TaskInfoRepository

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    fun cleanDatabase() {
        mongoTemplate.getCollection("aggregate-user").drop()
        mongoTemplate.getCollection("aggregate-project").drop()
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

        var projectProjection: StatusesWithTasksProjection? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            projectProjection = statusesWithTasksServices.getStatusesByProjectById(projectId)
            Assertions.assertNotNull(projectProjection)
        }

        Assertions.assertNotNull(projectProjection)
        Assertions.assertEquals(1, projectProjection!!.statuses.count())
        val resultTask =  projectProjection!!.statuses.first().tasks.first()
        Assertions.assertEquals(taskName, resultTask.taskName)
    }

    @Test
    fun addStatus() {
        val projectId = UUID.randomUUID()
        val title = "title"
        val description = "description"
        val statusName = "Status"

        projectEsService.create { it.create(projectId, title, description) }
        projectEsService.update(projectId) { it.createStatus(statusName) }

        var projectProjection: StatusesWithTasksProjection? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            projectProjection = statusesWithTasksServices.getStatusesByProjectById(projectId)
            Assertions.assertNotNull(projectProjection)
            val resultStatus = projectProjection!!.statuses.find { it.statusName == statusName }
            Assertions.assertNotNull(resultStatus)
        }

        Assertions.assertNotNull(projectProjection)
        val resultStatus = projectProjection!!.statuses.find { it.statusName == statusName }
        Assertions.assertNotNull(resultStatus)
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

        var projectProjection: StatusesWithTasksProjection? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            projectProjection = statusesWithTasksServices.getStatusesByProjectById(projectId)
            Assertions.assertNotNull(projectProjection)
            if (projectProjection != null) {
                Assertions.assertEquals(null, projectProjection!!.statuses.find { it.statusName == statusName })
            }
        }

        Assertions.assertNotNull(projectProjection)
        Assertions.assertEquals(null, projectProjection!!.statuses.find { it.statusName == statusName })
    }

    @Test
    fun changeOrder() {
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
        val newOrderIndex = 2
        projectEsService.update(projectId) {it.changeStatusOrder(statusId3, newOrderIndex)}
        val statuses = projectEsService.getState(projectId)!!.projectStatuses.values.sortedBy { it.order }

        var projectProjection: StatusesWithTasksProjection? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            projectProjection = statusesWithTasksServices.getStatusesByProjectById(projectId)
            Assertions.assertNotNull(projectProjection)
            if (projectProjection != null) {
                Assertions.assertNotNull(projectProjection!!.statuses.find { it.statusName == statusName3 })
                val status = projectProjection!!.statuses.find { it.statusName == statusName3 }
                Assertions.assertEquals(newOrderIndex, status!!.statusOrder)
            }
        }
        Assertions.assertNotNull(projectProjection)
        if (projectProjection != null) {
            Assertions.assertNotNull(projectProjection!!.statuses.find { it.statusName == statusName3 })
            val status = projectProjection!!.statuses.find { it.statusName == statusName3 }
            Assertions.assertEquals(newOrderIndex, status!!.statusOrder)
        }
    }

}