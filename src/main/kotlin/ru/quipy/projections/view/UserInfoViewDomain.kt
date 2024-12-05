package ru.quipy.projections.view

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import ru.quipy.domain.Unique
import java.util.*

class UserInfoViewDomain {
    @Document("user-info-view")
    data class UserInfo (
        @Id
        override val id: UUID,
        val userCredentials: UserCredentials,
        val login: String,
        val password: String
    ): Unique<UUID>

    data class UserCredentials(
        var firstName: String,
        var lastName: String,
        var middleName: String?
    )

    data class UserDtoData(
        val id: UUID,
        val userCredentials: UserCredentials,
    )
}