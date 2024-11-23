package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.auth.UserAggregate
import ru.quipy.api.auth.UserCreatedEvent
import ru.quipy.projections.dto.UserDto
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class UserEventsProcessingSubscriber {

    val logger: Logger = LoggerFactory.getLogger(UserEventsProcessingSubscriber::class.java)
    @Autowired
    val userService: UserProjectionService = UserProjectionService()

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "user-processing-subscriber") {

            `when`(UserCreatedEvent::class) { event ->
                userService.onUserCreated(event)
                logger.info("User created: {}", event.userId)
            }
        }
    }

    fun getUsersBySubstr(substring: String): List<UserDto> {
        val users = userService.searchUsers(substring)
        val newUsers = mutableListOf<UserDto>()
        users.forEach{
            val dto = UserDto(userId = it.userId, name = it.name, nickname = it.nickname)
            newUsers.add(dto)
        }
        return newUsers.toList()
    }
}