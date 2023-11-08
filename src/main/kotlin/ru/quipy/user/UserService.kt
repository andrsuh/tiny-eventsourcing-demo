package ru.quipy.user

import com.google.common.eventbus.EventBus
import javassist.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException.Forbidden
import org.springframework.web.server.ResponseStatusException
import ru.quipy.user.dto.UserLogin
import ru.quipy.user.dto.UserModel
import ru.quipy.user.dto.UserRegister
import java.lang.Exception

interface UserService {
    fun createOne(data: UserRegister): UserModel
    fun getOne(username: String): UserModel
    fun logIn(data: UserLogin): UserModel
}



@Service
class UserServiceImpl(
        private val userRepository: UserRepository,
): UserService {

    override fun createOne(data: UserRegister): UserModel {
        var foundUser: UserModel? = null
        try {
            foundUser = getOne(data.username)
        } catch (e: Exception) {
            // skip if exists
        }
        if (foundUser != null) throw ResponseStatusException(HttpStatus.CONFLICT, "user already exists")

        val userEntity = userRepository.save(data.toEntity())
        EventBus().post(UserCreatedEvent(userEntity.toModel()))
        return userEntity.toModel()
    }

    override fun getOne(username: String): UserModel {
        val userEntity: UserEntity =
                userRepository.findByUsername(username) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")
        return userEntity.toModel()
    }

    override fun logIn(data: UserLogin): UserModel {
        val userEntity: UserEntity =
                userRepository.findByUsername(data.username) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")
        if (userEntity.password?.let { comparePassword(it, data.password) } == true) {
            return userEntity.toModel()
        }
        throw ResponseStatusException(HttpStatus.CONFLICT, "password does not match")
    }

    fun UserRegister.toEntity(): UserEntity =
            UserEntity(
                    username = this.username,
                    name = this.name,
                    password = BCryptPasswordEncoder().encode(this.password)
            )

    fun UserEntity.toModel(): UserModel = kotlin.runCatching {
        UserModel(
                id = this.id,
                username = this.username!!,
                name = this.name!!,
                password = this.password!!
        )
    }.getOrElse { exception -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "some fields are missing") }

    fun comparePassword(encodedPassword: String, newPassword: String): Boolean = BCryptPasswordEncoder().matches(newPassword, encodedPassword)
}