package ru.quipy

import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import ru.quipy.utils.*
import java.lang.System.currentTimeMillis
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CountDownLatch

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AggregateClientTest {

    @Autowired
    private lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    @Autowired
    private lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    var numberOfSuccess = 0;
    var time = 0.0;

    val logger: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(AggregatesTest::class.java)

    @BeforeEach
    fun init() {
        userEsService.create {
            it.createUser(userId, "k", "k", "k")
        }
        projectEsService.create {
            it.create(projectId, "Project", userId)
        }
    }

    suspend fun test(num: Int, taskId: UUID) {
        logger.info("Started $num")
        val start = currentTimeMillis()
        try {
            projectEsService.update(projectId) { it.changeTaskTitle(taskId, "New title $num") }
        } catch (e: Exception) {
            numberOfSuccess--
        }
        numberOfSuccess++
        val end = currentTimeMillis()

        time += (end - start)
        logger.info("Ended $num")
    }

    @Test
    fun loadTestRequestsPerSeconds() = runBlocking {
        val numberOfCoroutines = 50

        logger.info(numberOfCoroutines.toString())

        val statusId = createStatus(projectEsService, projectId, "Status", "Color")
        val taskId = createTask(projectEsService, projectId,"Status", statusId)

        var jobs = mutableListOf<Job>()
        for (i in 1..numberOfCoroutines) {
            val job = launch {
                test(i, taskId)
            }
            jobs.add(job)
        }

        jobs.joinAll()
        logger.info("Results:")
        logger.info("Success rate: $numberOfSuccess of $numberOfCoroutines")
        logger.info("Average time: " + (time / numberOfCoroutines).toString() + " ms")

        logger.info(projectEsService.getState(projectId)!!.tasks[taskId]!!.name)
    }

    companion object {
        var userId = UUID.randomUUID()
        var projectId = UUID.randomUUID()
    }
}