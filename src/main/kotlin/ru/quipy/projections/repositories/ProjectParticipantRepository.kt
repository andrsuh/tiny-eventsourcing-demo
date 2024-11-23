package ru.quipy.projections.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.quipy.projections.entities.ProjectParticipantEntity
import java.util.*

@Repository
interface ProjectParticipantRepository : JpaRepository<ProjectParticipantEntity, UUID> {

    @Query("SELECT p.participantId FROM ProjectParticipantEntity p WHERE p.projectId = :projectId")
    fun findParticipantIdsByProjectId(@Param("projectId") projectId: UUID): List<UUID>
}
