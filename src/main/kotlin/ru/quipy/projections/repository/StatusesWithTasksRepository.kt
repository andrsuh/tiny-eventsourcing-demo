package ru.quipy.projections.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.projections.entity.ProjectProjection
import ru.quipy.projections.entity.StatusesWithTasksProjection
import java.util.*

interface StatusesWithTasksRepository : MongoRepository<StatusesWithTasksProjection, UUID>