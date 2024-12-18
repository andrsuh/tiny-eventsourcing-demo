package ru.quipy.service

import org.springframework.stereotype.Service
import ru.quipy.projections.ProjectProjection
import ru.quipy.projections.TaskProjection
import ru.quipy.repository.ProjectProjectionRepository
import ru.quipy.repository.TaskProjectionRepository
import java.util.*

@Service
class ProjectionService (
    private val projectProjectionRepository
            : ProjectProjectionRepository,

    private val taskProjectionRepository
            : TaskProjectionRepository
) {
    fun findAllProjectsByOwnerId(ownerId : UUID) : List<ProjectProjection> {
        return projectProjectionRepository.findAllByOwnerId(ownerId)
    }

    fun findAllProjectByParticipantIdContains(participantId : UUID): List<ProjectProjection> {
        return projectProjectionRepository.findAllByParticipantIdContains(participantId)
    }

    fun findAllTasksByExecutorIdContains(executorId : UUID): List<TaskProjection> {
        return taskProjectionRepository.findAllByExecutorIdContains(executorId)
    }
}