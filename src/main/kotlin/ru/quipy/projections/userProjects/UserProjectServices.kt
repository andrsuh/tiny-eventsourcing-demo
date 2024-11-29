package ru.quipy.projections.userProjects

import org.springframework.stereotype.Service
import ru.quipy.projections.repository.UserProjectRepository
import ru.quipy.projections.repository.UserProjectsRepository
import ru.quipy.projections.views.ProjectView
import ru.quipy.projections.views.UserProjectsView
import java.util.ArrayList

@Service
class UserProjectServices(
    private val userProjectsRepository: UserProjectsRepository,
    private val projectRepository: UserProjectRepository,)
{
    fun findProjectsByLogin(login: String): UserProjectsView? {
        val users = userProjectsRepository.findAll()
        val user = users.find { it.userLogin == login }
        if (user != null) {
            val projects = ArrayList<ProjectView>()
            user.projects.forEach { project ->
                val projectEntity = projectRepository.findById(project)
                if (!projectEntity.isEmpty) {
                    val projectView = ProjectView(
                        projectEntity.get().projectId,
                        projectEntity.get().title,
                        projectEntity.get().description
                    )
                    projects.add(projectView)
                }
            return UserProjectsView(user.userId, user.userLogin, user.username, projects)
            }
        }
        return null
    }
}