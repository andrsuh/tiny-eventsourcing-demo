package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.create
import java.util.*

@SpringBootTest
class UserTests {
	companion object {
		private val testId = UUID.randomUUID()
		private val userId = UUID.randomUUID()
		private val login = "D4C"
		private val password = "DirtyDeedsDoneDirtCheap"
		private val username = "Valentine"

		private val projectCreator_userId = UUID.randomUUID()
		private val projectCreatorlogin = "F2M"
		private val projectCreatorpassword = "MotherMaryMouthsMore"
		private val projectCreatorusername = "Maria"
	}

	@Autowired
	lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

	@Autowired
	lateinit var mongoTemplate: MongoTemplate

	@BeforeEach
	fun init()
	{
		cleanDatabase()
	}

	fun cleanDatabase() {
		mongoTemplate.remove(Query.query(Criteria.where("aggregateId").`is`(userId)), "aggregate-user")
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
	fun assighProjectToUserTest() {
		/** Creating project and assigning another user to it */
		userEsService.create { it.create(projectCreator_userId, projectCreator_login, projectCreator_password, projectCreator_username) }
		
		
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
	fun assighProjectToUserTest_NoSuchProject() {
		/**  */
		userEsService.create { it.create(userId, login, password, username) }
		val state = userEsService.getState(userId)

		userEsService.create { it.create(userId, login, password, username) }
		val state = userEsService.getState(userId)

		Assertions.assertNotNull(state)
		if (state != null) {
			Assertions.assertEquals(userId, state.getId())
			Assertions.assertEquals(login, state.login)
			Assertions.assertEquals(password, state.password)
			Assertions.assertEquals(username, state.username)
		}
		val exception: Exception = assertThrows(IllegalArgumentException::class.java, {
            throw IllegalArgumentException("exception message")
        })
        assertEquals("exception message", exception.message)
	}
	@Test
	fun assighProjectToUserTest_NoSuchUser() {
		/**  */
		val exception: Exception = assertThrows(IllegalArgumentException::class.java, {
            throw IllegalArgumentException("exception message")
        })
        assertEquals("exception message", exception.message)
	}
	@Test
	fun editProjectTest_UserNotPerformer() {
		/**  */
		val exception: Exception = assertThrows(IllegalArgumentException::class.java, {
            throw IllegalArgumentException("exception message")
        })
        assertEquals("exception message", exception.message)
	}
	@Test
	fun assighProjectToUserTest() {
		/**  */
	}
	@Test
	fun assighProjectToUserTest() {
		/** User creation with credentials - user has right credentials */
	}

}
