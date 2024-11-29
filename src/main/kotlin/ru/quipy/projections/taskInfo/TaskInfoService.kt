package ru.quipy.projections.taskInfo

import org.springframework.stereotype.Service
import ru.quipy.projections.repository.TaskInfoRepository
import ru.quipy.projections.repository.UserProjectionRepository
import ru.quipy.projections.views.TaskInfoView
import ru.quipy.projections.views.TaskPerformer
import java.util.*
import kotlin.collections.ArrayList


@Service
class TaskInfoService (
    private val taskRepository: TaskInfoRepository,
    private val userRepository: UserProjectionRepository,)
{
    fun getTaskInfoById(id: UUID): TaskInfoView? {
        val task = taskRepository.findById(id)
        if (!task.isEmpty) {
            var users = ArrayList<TaskPerformer>()
            for (userId in task.get().performers)
            {
                val user = userRepository.findById(userId)
                if (!user.isEmpty) {
                    users.add(TaskPerformer(user.get().userId, user.get().login))
                }
            }
            return TaskInfoView(task.get().taskId, task.get().taskName, task.get().taskDescription, users)
        }
        return null
    }
}