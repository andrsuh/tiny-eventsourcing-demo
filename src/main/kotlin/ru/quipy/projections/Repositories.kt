package ru.quipy.projections

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StatusRepository : MongoRepository<Status, UUID> {
    fun findAllByProjectId(projectId: UUID): List<Status>
}

@Repository
interface TaskRepository : MongoRepository<Task, UUID> {
    fun findAllByProjectIDAndTaskAssigneesContaining(projectId: UUID, taskAssignee: UUID): List<Task>
    fun findAllByProjectID(projectId: UUID): List<Task>
}

@Repository
interface ProjectRepository : MongoRepository<Project, UUID> {
    fun findAllByMembersContaining(memberId: UUID): List<Project>
}

@Repository
interface UserRepository : MongoRepository<User, UUID> {
    override fun findAllById(ids: Iterable<UUID>): List<User>
}