package ru.quipy.projections

import org.springframework.stereotype.Service
import java.util.UUID
@Service
class StatusService (
        private val statusRepository: StatusRepository
) {
    fun getAllByProjectId(projectId: UUID): List<Status> {
        return statusRepository.findAllByProjectId(projectId)
    }
}

@Service
class TaskService (
        private val taskRepository: TaskRepository
) {
    fun getAllByProjectIdAndUserId(projectId: UUID, userId: UUID): List<Task> {
        return taskRepository.findAllByProjectIDAndTaskAssigneesContaining(projectId, userId)
    }

    fun getAllByProjectId(projectId: UUID): List<Task> {
        return taskRepository.findAllByProjectID(projectId)
    }
}

@Service
class UserAndProjectService (
        private val projectRepository: ProjectRepository,
        private val userRepository: UserRepository
) {
    fun getProjectsByUserId(userId: UUID): List<Project> {
        return projectRepository.findAllByMembersContaining(userId)
    }

    fun getUsersByProjectId(projectId: UUID): List<User> {
        val usersByProjectId = projectRepository.findById(projectId)
        if (usersByProjectId.isEmpty) {
            return listOf()
        }
        return userRepository.findAllById(usersByProjectId.get().members)
    }
}