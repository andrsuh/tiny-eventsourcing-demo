package ru.quipy.projections.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.quipy.projections.entities.ProjectParticipantEntity
import ru.quipy.projections.entities.ProjectTaskEntity
import java.util.*

@Repository
interface ProjectTaskRepository : JpaRepository<ProjectTaskEntity, UUID> {

    @Query("SELECT p.taskId FROM ProjectTaskEntity p WHERE p.projectId = :projectId")
    fun findTasksIdsByProjectId(@Param("projectId") projectId: UUID): List<UUID>
}
