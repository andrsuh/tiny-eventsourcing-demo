package ru.quipy.projections

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.auth.UserAggregate
import ru.quipy.api.auth.UserCreatedEvent
import ru.quipy.projections.entities.UserEntity
import ru.quipy.projections.repositories.UserRepository
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(aggregateClass = UserAggregate::class, subscriberName = "user-projection-subscriber")
class UserProjectionService {

    @Autowired
    lateinit var userRepository: UserRepository

    @SubscribeEvent
    fun onUserCreated(event: UserCreatedEvent) {
        val userEntity = UserEntity(
            userId = event.userId,
            nickname = event.nickName,
            name = event.personName,
            password = event.password
        )
        userRepository.save(userEntity)
    }

    // Method to search users
    fun searchUsers(query: String): List<UserEntity> {
        return userRepository.findUserBySubstr(query)
    }

    fun getAllUsers(): List<UserEntity> {
        return userRepository.findAll();
    }
}
   
