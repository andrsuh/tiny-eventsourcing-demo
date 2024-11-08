package ru.quipy.service

import org.springframework.stereotype.Service
import ru.quipy.projections.*

@Service
class ProjectionsService constructor(val userProjectionRepo: UserProjectionRepo,
                                     val taskStatusProjectionRepo: TaskStatusProjectionRepo,
                                     val projectUserProjectionRepo: ProjectUserProjectionRepo,
                                     val projectTaskUserRepo: ProjectTaskUserRepo){
    fun getAllUserProjection(): List<UserProjection> {
        return userProjectionRepo.findAll()
    }

    fun getAllTaskStatusProjection(): List<TaskStatusProjection> {
        return taskStatusProjectionRepo.findAllByTaskIdNotNull();
    }

    fun getAllProjectUserProjection(): List<ProjectUserProjection> {
        return projectUserProjectionRepo.findAll();
    }

    fun getAllProjectTaskUserProjection(): List<ProjectTasksUserProjection> {
        return projectTaskUserRepo.findAll()
    }
}