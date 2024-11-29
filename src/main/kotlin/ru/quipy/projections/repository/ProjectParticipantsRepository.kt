package ru.quipy.projections.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.projections.entity.ProjectParticipantProjection
import java.util.*

interface ProjectParticipantsRepository : MongoRepository<ProjectParticipantProjection, UUID>
