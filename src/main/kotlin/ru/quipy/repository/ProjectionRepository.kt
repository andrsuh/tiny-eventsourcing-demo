package ru.quipy.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.projections.ProjectProjection
import ru.quipy.projections.TaskProjection
import java.util.*

interface TaskProjectionRepository
    : MongoRepository<TaskProjection, UUID> {
        fun findAllByExecutorIdContains(executorId: UUID): List<TaskProjection>
    }

interface ProjectProjectionRepository :
    MongoRepository<ProjectProjection, UUID> {
        fun findAllByOwnerId(ownerId: UUID): List<ProjectProjection>

        fun findAllByParticipantIdContains(participantId: UUID): List<ProjectProjection>
    }