package ru.quipy

import liquibase.hub.model.Project
import org.junit.Before
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.dropCollection
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.event.annotation.BeforeTestExecution
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.addProject
import ru.quipy.logic.create
import java.util.*

@SpringBootTest
class UserTests {
	companion object {
		private val projectId = UUID.randomUUID()
		private val userId = UUID.randomUUID()
		private val login = "testUser"
		private val password = "testPassword"
		private val username = "user"
	}

	@Autowired
	lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

	@Autowired
	lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

	@Autowired
	lateinit var mongoTemplate: MongoTemplate

//	@Before
//	fun init()
//	{
//		cleanDatabase()
//	}

	@BeforeTestExecution
	fun cleanDatabase() {
		mongoTemplate.dropCollection<UserAggregate>()
		mongoTemplate.dropCollection(UserAggregate::class.java)
		mongoTemplate.dropCollection(ProjectAggregate::class.java)
		mongoTemplate.getCollection("aggregate-user").drop()
		mongoTemplate.getCollection("aggregate-project").drop()
//		mongoTemplate.getCollection("snapshots").drop()
		mongoTemplate.remove(Query.query(Criteria.where("aggregateId").`is`(userId)), "aggregate-user")
		mongoTemplate.remove(Query.query(Criteria.where("_id").`is`(projectId)), "aggregate-project")
	}

	@Test
	fun createUserTest() {
		 /** User creation with credentials - user has right credentials */
		userEsService.create { it.create(userId, login, password, username) }

		val state = userEsService.getState(userId)

		Assertions.assertNotNull(state)
		if (state != null) {
			Assertions.assertEquals(userId, state.getId())
			Assertions.assertEquals(login, state.login)
			Assertions.assertEquals(password, state.password)
			Assertions.assertEquals(username, state.username)
		}
	}

	@Test
	fun assignProjectToUserTest() {
		val title = "title"
		val description = "description"
		userEsService.create { it.create(userId, login, password, username) }
		projectEsService.create { it.create(projectId, title, description) }
		val project = projectEsService.getState(projectId)

		userEsService.update(userId) {it.addProject(project)}
		val state = userEsService.getState(userId)

		Assertions.assertNotNull(state)
		if (state != null) {
			Assertions.assertEquals(1, state.projects.size)
			Assertions.assertEquals(projectId, state.projects[0])
		}
	}

	@Test
	fun assignProjectToUserTest_NoSuchProject() {
		userEsService.create { it.create(userId, login, password, username) }

		Assertions.assertThrows(IllegalArgumentException::class.java) {
			userEsService.update(userId) {it.addProject(null)}
		}
	}

	@Test
	fun assignProjectToUserTest_AssignTwice() {
		val title = "title"
		val description = "description"
		userEsService.create { it.create(userId, login, password, username) }
		projectEsService.create { it.create(projectId, title, description) }
		val project = projectEsService.getState(projectId)
		userEsService.update(userId) {it.addProject(project)}

		val state = userEsService.getState(userId)

		Assertions.assertThrows(IllegalArgumentException::class.java) {
			userEsService.update(userId) {it.addProject(project)}
		}
	}

}
