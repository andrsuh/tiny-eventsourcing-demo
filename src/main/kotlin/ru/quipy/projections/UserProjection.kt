package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.*

@Service
@AggregateSubscriber(
    aggregateClass = UserAggregate::class,
    subscriberName = "user-projection"
)
class UserProjection(
    val userProjectionRepository: UserProjectionRepository
) {

    val logger: Logger = LoggerFactory.getLogger(UserProjection::class.java)

    @SubscribeEvent
    fun userCreatedSubscriber(event: UserCreatedEvent) {
        val user = UserProjectionUser(event.userId, event.realname, event.username)
        userProjectionRepository.save(user)
        logger.info("User created: {}", event.userId)
    }
}

@Document("user-projection")
data class UserProjectionUser(
    @Id
    var userId: UUID,
    val name: String,
    val nickname: String
)

@Repository
interface UserProjectionRepository : MongoRepository<UserProjectionUser, UUID>