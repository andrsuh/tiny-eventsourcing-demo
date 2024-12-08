package ru.quipy.projections

import org.springframework.stereotype.Service
import ru.quipy.aggregate.user.UserAggregate
import ru.quipy.api.user.UserChangedNameEvent
import ru.quipy.api.user.UserCreatedEvent
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(
    aggregateClass = UserAggregate::class, subscriberName = "user-data-sub"
)
class UserViewService(
    private val userRepository: UsersRepository
) {
    @SubscribeEvent
    fun saveUser(event: UserCreatedEvent) {
        userRepository.save(
            UserViewDomain.User(event.id, event.firstname, event.nickname)
        )
    }

    @SubscribeEvent
    fun updateNameUser(event: UserChangedNameEvent) {
        val userView = userRepository.findById(event.id).get()
        userView.name = event.newName
        userRepository.save(userView)
    }

    fun findByName(name: String): UserViewDomain.User? {
        return userRepository.findByName(name)
    }

    fun findByNameSubs(nameSubs: String): List<UserViewDomain.User> {
        return userRepository.findAllByNameRegex(nameSubs)
    }

    fun findByNickName(nickname: String): UserViewDomain.User? {
        return userRepository.findByNickname(nickname)
    }
}