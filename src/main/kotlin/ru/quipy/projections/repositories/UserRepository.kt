package ru.quipy.projections.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.quipy.projections.entities.UserEntity
import java.util.*

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {

    @Query("""SELECT u FROM UserEntity u WHERE u.name 
        LIKE LOWER(CONCAT('%', :substring, '%')) or u.nickname LIKE LOWER(CONCAT('%', :substring, '%'))""")
    fun findUserBySubstr(@Param("substring") substring: String): List<UserEntity>
    fun findAllByUserIdIn(uuids: List<UUID>): List<UserEntity>
    fun findByUserId(uuis: UUID): UserEntity?
}
