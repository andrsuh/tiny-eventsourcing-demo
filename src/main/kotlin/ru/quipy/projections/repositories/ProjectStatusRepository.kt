package ru.quipy.projections.repositories
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.quipy.projections.entities.ProjectParticipantEntity
import ru.quipy.projections.entities.ProjectsStatusEntity
import java.util.*


@Repository
interface ProjectStatusRepository : JpaRepository<ProjectsStatusEntity, UUID> {
    @Query("SELECT p FROM ProjectsStatusEntity p WHERE p.projectId = :projectId")
    fun findStatusesByRepository(@Param("projectId") projectId: UUID): List<ProjectsStatusEntity>


    @Query("SELECT p FROM ProjectsStatusEntity p WHERE p.name = :name and p.projectId = :projectId")
    fun findByName(@Param("name") name: String, @Param("projectId") projectId: UUID): ProjectsStatusEntity

    @Query("SELECT MAX(statusOrder) FROM ProjectsStatusEntity p")
    fun findMaxOrder(): Int?

    @Modifying
    @Query("UPDATE ProjectsStatusEntity SET statusOrder = statusOrder - 1 WHERE statusOrder > :statusOrder")
    fun updateOrderAfterDelete(@Param("statusOrder") statusOrder: Int)

    @Modifying
    @Query("UPDATE ProjectsStatusEntity SET statusOrder = statusOrder + 1 WHERE statusOrder BETWEEN :start AND :end")
    fun incrementOrderBetween(@Param("start") start: Int, @Param("end") end: Int)

    @Modifying
    @Query("UPDATE ProjectsStatusEntity SET statusOrder = statusOrder - 1 WHERE statusOrder BETWEEN :start AND :end")
    fun decrementOrderBetween(@Param("start") start: Int, @Param("end") end: Int)
}
