package ru.quipy.projections.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.projections.entity.UserProjectProjection
import ru.quipy.projections.entity.UserProjection
import ru.quipy.projections.entity.UserProjectsProjection
import java.util.*

interface UserProjectsRepository : MongoRepository<UserProjectsProjection, UUID>

interface UserProjectRepository : MongoRepository<UserProjectProjection, UUID>