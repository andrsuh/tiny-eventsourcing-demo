package ru.quipy.projections

import org.springframework.stereotype.Service
import java.util.*

@Service
class ProjectionsService constructor(val userProjectionRepo: UserProjectionRepo,
                                     val projectTaskProjectionRepo: ProjectTaskProjectionRepo,
                                     val projectUserProjectionRepo: ProjectUserProjectionRepo,
                                     val statusProjectProjectionRepo: StatusProjectProjectionRepo,
                                     val projectProjectionRepository: ProjectProjectionRepo){

    fun isNickNameExist(nickName: String): Boolean {
        return userProjectionRepo.existsByNickName(nickName);
    }

    fun getUserByNickName(nickName: String): List<UserProjection>? {
        return userProjectionRepo.findUserByNickName(nickName);
    }

    fun getParticipantOfProjectByID(projectId : UUID) : List<ProjectUserProjection> {
        return projectUserProjectionRepo.findAllByProjectIdNotNull(projectId);
    }

    fun getAllProjectOfUserById(userId : UUID) : List<ProjectUserProjection> {
        return projectUserProjectionRepo.findAllByUserIdNotNull(userId);
    }

    fun getTasksOfProjectByID(projectId : UUID) : List<ProjectTasksProjection> {
        return projectTaskProjectionRepo.findAllByProjectIdNotNull(projectId);
    }

    fun getAllStatusOfProject(projectId : UUID) : List<StatusProjectProjection> {
        return statusProjectProjectionRepo.findAllByProjectIdNotNull(projectId);
    }

    fun getAllProjectInfor() : List<ProjectProjection> {
        return projectProjectionRepository.findAll();
    }

    fun getProjectById(projectId : UUID) : ProjectProjection {
        return projectProjectionRepository.findById(projectId).get();
    }
}