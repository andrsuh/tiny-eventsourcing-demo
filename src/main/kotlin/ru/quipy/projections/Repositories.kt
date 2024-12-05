package ru.quipy.projections

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UsersNamesRepository : MongoRepository<UserWithName, UUID> {
    fun findUserWithNameByUserId(userId: UUID): UserWithName
    fun findAllByUserIdIn(userIds: List<UUID>): List<UserWithName>
}

@Repository
interface ProjectsNamesRepository : MongoRepository<ProjectWithName, UUID> {
    fun findAllByProjectIdIn(projectIds: List<UUID>): List<ProjectWithName>
}

@Repository
interface ProjectsRepository : MongoRepository<Project, UUID> {
    fun findAllByParticipantsContains(usersId: UUID): List<Project>
    fun findByProjectId(projectId: UUID) : Project
}

@Repository
interface StatusesRepository : MongoRepository<Status, UUID> {
    fun findAllByStatusIdIn(statusIds: List<UUID>) : List<Status>
    fun findByStatusId(statusId: UUID) : Status
}

@Repository
interface TasksRepository : MongoRepository<Task, UUID> {
    fun findAllByProjectId(projectId: UUID) : List<Task>
    fun findByTaskId(projectId: UUID): Task
    fun findByName(name: String): Task
}