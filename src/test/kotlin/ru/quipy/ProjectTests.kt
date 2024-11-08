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
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.addProject
import ru.quipy.logic.create
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
        }
    }

}