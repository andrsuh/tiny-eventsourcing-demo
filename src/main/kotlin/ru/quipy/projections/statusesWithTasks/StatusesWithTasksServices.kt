package ru.quipy.projections.statusesWithTasks

import org.springframework.stereotype.Service
import ru.quipy.projections.entity.StatusesWithTasksProjection
import ru.quipy.projections.repository.StatusesWithTasksRepository
import ru.quipy.projections.repository.TaskInfoRepository
import ru.quipy.projections.repository.UserProjectionRepository
import ru.quipy.projections.views.TaskInfoView
import ru.quipy.projections.views.TaskPerformer
import java.util.*
import kotlin.collections.ArrayList

@Service
class StatusesWithTasksServices (
    private val statusesWithTasksRepository: StatusesWithTasksRepository, )
{
    fun getStatusesByProjectById(id: UUID): StatusesWithTasksProjection? {
        val project = statusesWithTasksRepository.findById(id)
        if (!project.isEmpty) {
            return project.get()
        }
        return null
    }
}