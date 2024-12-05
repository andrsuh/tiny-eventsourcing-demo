package ru.quipy.projections.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.projections.view.TaskInfoViewDomain
import ru.quipy.projections.view.UserInfoViewDomain
import java.util.*

interface UserInfoRepository: MongoRepository<UserInfoViewDomain.UserInfo, UUID> {
    fun findByLogin(login: String): UserInfoViewDomain.UserInfo?
}