package ru.quipy.projections

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

interface UsersRepository : MongoRepository<UserViewDomain.User, UUID> {
    fun findByName(name: String) : UserViewDomain.User?
    fun findByNickname(nickname: String) : UserViewDomain.User?
    fun findAllByNameRegex(nicknameRegex: String) : List<UserViewDomain.User>
    fun findAllByNicknameRegex(nicknameRegex: String) : List<UserViewDomain.User>
}