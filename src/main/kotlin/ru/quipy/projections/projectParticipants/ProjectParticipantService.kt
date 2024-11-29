package ru.quipy.projections.projectParticipants

import org.springframework.stereotype.Service
import ru.quipy.projections.repository.ProjectParticipantsRepository
import ru.quipy.projections.repository.UserProjectRepository
import ru.quipy.projections.repository.UserProjectionRepository
import ru.quipy.projections.repository.UserProjectsRepository
import ru.quipy.projections.views.Participants
import ru.quipy.projections.views.ProjectParticipantsView
import ru.quipy.projections.views.ProjectView
import ru.quipy.projections.views.UserProjectsView
import java.util.*

@Service
class ProjectParticipantService (
    private val projectParticipants: ProjectParticipantsRepository,
    private val userRepository: UserProjectionRepository,)
{
    fun findUsersByProject(projectId: UUID): ProjectParticipantsView? {

        val project = projectParticipants.findById(projectId)
        if (!project.isEmpty) {
            val usersView = ArrayList<Participants>()
            project.get().participants.forEach {
                user ->
                val userEntity = userRepository.findById(user)
                if (!userEntity.isEmpty) {
                    val participant = Participants(userEntity.get().userId, userEntity.get().login)
                    usersView.add(participant)
                }

            }
                return ProjectParticipantsView(projectId, usersView)
            }
        return null
    }
}