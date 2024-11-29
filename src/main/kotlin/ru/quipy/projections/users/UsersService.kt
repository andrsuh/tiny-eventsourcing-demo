package ru.quipy.projections.users

import org.springframework.stereotype.Service
import ru.quipy.projections.entity.StatusesWithTasksProjection
import ru.quipy.projections.entity.UserProjection
import ru.quipy.projections.repository.StatusesWithTasksRepository
import ru.quipy.projections.repository.UserProjectionRepository
import java.util.*

@Service
class UsersService (
    private val userProjectionRepository: UserProjectionRepository, )
{
    fun getStatusesByProjectById(id: UUID): UserProjection? {
        val user = userProjectionRepository.findById(id)
        if (!user.isEmpty) {
            return user.get()
        }
        return null
    }
}