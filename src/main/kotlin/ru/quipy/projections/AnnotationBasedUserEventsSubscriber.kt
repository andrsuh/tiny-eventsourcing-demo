package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.projections.repository.TaskInfoRepository
import ru.quipy.projections.repository.UserInfoRepository
import ru.quipy.projections.view.UserInfoViewDomain
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.*

@Service
@AggregateSubscriber(
    aggregateClass = UserAggregate::class, subscriberName = "user-subscriber"
)
class AnnotationBasedUserEventsSubscriber(
    private val userInfoRepository: UserInfoRepository
) {

   val logger: Logger = LoggerFactory.getLogger(AnnotationBasedUserEventsSubscriber::class.java)

    @SubscribeEvent
    fun userCreatedSubscriber(event: UserCreatedEvent) {
        createUser(event)
        logger.info("User with id {}, username {}, login {}, password {}",
            event.userId, event.username, event.login, event.password)
    }

    fun createUser(event: UserCreatedEvent) {
        checkCreateUser(event)
        val userCredentials = UserInfoViewDomain.UserCredentials(event.username.firstName, event.username.lastName,
            event.username.middleName)
        val user = UserInfoViewDomain.UserInfo(event.userId, userCredentials, event.login, event.password)
        userInfoRepository.save(user)
    }

    fun checkCreateUser(event: UserCreatedEvent) {
        val userWithSameLogin = userInfoRepository.findByLogin(event.login)
        require(userWithSameLogin == null) {"User with login ${event.login} already exists!"}
    }

    fun getUser(userId: UUID): UserInfoViewDomain.UserDtoData {
        val user = userInfoRepository.findById(userId).orElse(null)
        require(user != null) {"User with id $userId does not exist!"}
        return UserInfoViewDomain.UserDtoData(user.id, user.userCredentials)
    }

    fun getAllUsers(): List<UserInfoViewDomain.UserDtoData> {
        val users = userInfoRepository.findAll()
        return users.mapNotNull { userInfo -> UserInfoViewDomain.UserDtoData(userInfo.id, userInfo.userCredentials)}
    }
}
