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

}
