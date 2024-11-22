package ru.quipy.projections.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.projections.entity.UserProjection
import java.util.*

interface UserProjectionRepository : MongoRepository<UserProjection, UUID>
