package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.projections.repository.ProjectInfoRepository
import ru.quipy.projections.repository.TagInfoRepository
import ru.quipy.projections.repository.TaskInfoRepository
import ru.quipy.projections.repository.UserInfoRepository
import ru.quipy.projections.view.ProjectInfoViewDomain
import ru.quipy.projections.view.TagInfoViewDomain
import ru.quipy.projections.view.TaskInfoViewDomain
import ru.quipy.projections.view.UserInfoViewDomain
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.*

@Service
@AggregateSubscriber(
    aggregateClass = ProjectAggregate::class, subscriberName = "project-subscriber"
)
class AnnotationBasedProjectEventsSubscriber (
    private val projectInfoRepository: ProjectInfoRepository,
    private val tagInfoRepository: TagInfoRepository,
    private val taskInfoRepository: TaskInfoRepository,
    private val userInfoRepository: UserInfoRepository
) {

   val logger: Logger = LoggerFactory.getLogger(AnnotationBasedProjectEventsSubscriber::class.java)

    @SubscribeEvent
    fun projectCreatedSubscriber(event: ProjectCreatedEvent) {
        createProject(event)
        logger.info("Project {} with id {} was created by user with id {}",
            event.title, event.projectId, event.creatorId)
    }

    @SubscribeEvent
    fun projectUpdatedSubscriber(event: ProjectUpdatedEvent) {
        updateProject(event)
        logger.info("Project {} with id {} was updated, update description: {}",
            event.title, event.projectId, event.description)
    }

    @SubscribeEvent
    fun projectUserAddedSubscriber(event: ProjectMemberCreatedEvent) {
        addUserToProject(event)
        logger.info("User with id {} was added to the project with id {}",
            event.userId, event.projectId)
    }

    @SubscribeEvent
    fun projectUserRemovedSubscriber(event: ProjectMemberRemovedEvent) {
        removeUserFromProject(event)
        logger.info("User with id {} was removed from the project with id {}",
            event.userId, event.projectId)
    }

    @SubscribeEvent
    fun taskCreatedSubscriber(event: TaskCreatedEvent) {
        createTaskInProject(event)
        logger.info("Task {} with id {} was created in the project with id {}",
            event.taskName, event.taskId, event.projectId)
    }

    @SubscribeEvent
    fun taskUpdatedSubscriber(event: TaskUpdatedEvent) {
        updateTask(event)
        logger.info("Task {} with id {} in project with id {} was updated",
            event.taskName, event.taskId, event.projectId)
    }

    @SubscribeEvent
    fun taskExecutorAddedSubscriber(event: TaskAssignedEvent) {
        assignTask(event)
        logger.info("User with id {} became executor of task with id {} in project with id {}",
            event.userId, event.taskId, event.projectId)
    }

    @SubscribeEvent
    fun taskDeletedSubscriber(event: TaskDeletedEvent) {
        deleteTask(event)
        logger.info("Task with id {} was deleted from project with id {}",
            event.taskId, event.projectId)
    }

    @SubscribeEvent
    fun tagCreatedSubscriber(event: TagCreatedEvent) {
        createTag(event)
        logger.info("Tag {} with id {} was created in project with id {}",
            event.tagName, event.tagId, event.projectId)
    }

    @SubscribeEvent
    fun tagDeletedSubscriber(event: TagDeletedEvent) {
        deleteTag(event)
        logger.info("Tag with id {} was deleted from project with id {}",
            event.tagId, event.projectID)
    }

    @SubscribeEvent
    fun tagAssignedToTaskSubscriber(event: TagAddedToTaskEvent) {
        addTagToTask(event)
        logger.info("Tag with id {} was assigned to task with id {} in project with id {}",
            event.tagId, event.taskId, event.projectId)
    }


    fun createProject(event: ProjectCreatedEvent) {
        val projectInfo = ProjectInfoViewDomain.ProjectInfo(event.projectId, event.title, event.creatorId)
        projectInfo.participants.add(event.creatorId)
        projectInfoRepository.save(projectInfo)
    }

    fun updateProject(event: ProjectUpdatedEvent) {
        checkProjectUpdate(event)
        val projectInfo = projectInfoRepository.findById(event.projectId).get()
        if (event.title != null) projectInfo.projectTitle = event.title
        if (event.description != null) projectInfo.projectDescription = event.description
        projectInfoRepository.save(projectInfo)
    }

    fun addUserToProject(event: ProjectMemberCreatedEvent) {
        checkAddUserToProject(event)
        val projectInfo = projectInfoRepository.findById(event.projectId).get()
        projectInfo.participants.add(event.userId)
        projectInfoRepository.save(projectInfo)
    }

    fun removeUserFromProject(event: ProjectMemberRemovedEvent) {
        checkRemoveUserFromProject(event)
        val projectInfo = projectInfoRepository.findById(event.projectId).get()
        projectInfo.participants.remove(event.userId)
        projectInfoRepository.save(projectInfo)
    }

    fun createTaskInProject(event: TaskCreatedEvent) {
        checkCreateTaskInProject(event)
        val projectInfo = projectInfoRepository.findById(event.projectId).get()
        var tagId = projectInfo.projectTags["CREATED"]
        val task = TaskInfoViewDomain.TaskInfo(event.taskId, event.taskName)

        if (tagId == null) {
            tagId = UUID.randomUUID()
            val tag = TagInfoViewDomain.TagInfo(tagId)
            tagInfoRepository.save(tag)
            projectInfo.projectTags["CREATED"] = tagId
            task.tagsAssigned["CREATED"] = tagId
            taskInfoRepository.save(task)
            projectInfo.tasks.add(event.taskId)
        } else {
            task.tagsAssigned["CREATED"] = tagId
            taskInfoRepository.save(task)
            projectInfo.tasks.add(event.taskId)
        }
        projectInfoRepository.save(projectInfo)
    }

    fun updateTask(event: TaskUpdatedEvent) {
        checkUpdateTask(event)
        val taskInfo = taskInfoRepository.findById(event.taskId).get()
        if (event.taskName != null)  taskInfo.name = event.taskName
        if (event.taskDescription != null) taskInfo.description = event.taskDescription
        taskInfoRepository.save(taskInfo)
    }

    fun assignTask(event: TaskAssignedEvent) {
        checkAssignTask(event)
        val taskInfo = taskInfoRepository.findById(event.taskId).get()
        taskInfo.assigneeId = event.userId
        taskInfoRepository.save(taskInfo)
    }

    fun deleteTask(event: TaskDeletedEvent) {
        checkDeleteTask(event)
        val projectInfo = projectInfoRepository.findById(event.projectId).get()
        val task = taskInfoRepository.findById(event.taskId).get()
        projectInfo.tasks.remove(event.taskId)
        projectInfoRepository.save(projectInfo)
        taskInfoRepository.delete(task)
    }

    fun createTag(event: TagCreatedEvent) {
        checkCreateTag(event)
        val projectInfo = projectInfoRepository.findById(event.projectId).get()
        val tag = TagInfoViewDomain.TagInfo(event.tagId, event.tagName, TagInfoViewDomain.Color.valueOf(event.tagColor))
        tagInfoRepository.save(tag)
        projectInfo.projectTags[tag.name] = tag.id
        projectInfoRepository.save(projectInfo)
    }

    fun deleteTag(event: TagDeletedEvent) {
        checkDeleteTag(event)
        val projectInfo = projectInfoRepository.findById(event.projectID).get()
        val tag = tagInfoRepository.findById(event.tagId).get()
        projectInfo.projectTags.remove(tag.name)
        projectInfoRepository.save(projectInfo)
        tagInfoRepository.delete(tag)
    }

    fun addTagToTask(event: TagAddedToTaskEvent) {
        checkAddTag(event)
        val taskInfo = taskInfoRepository.findById(event.taskId).get()
        val tag = tagInfoRepository.findById(event.tagId).get()
        taskInfo.tagsAssigned[tag.name] = tag.id
        taskInfoRepository.save(taskInfo)
    }

    fun checkProjectUpdate(event: ProjectUpdatedEvent) {
        checkProject(event.projectId)
    }

    fun checkAddUserToProject(event: ProjectMemberCreatedEvent) {
        val projectInfo = checkProject(event.projectId)
        val userInfo = checkUser(event.userId)
        require(userInfo.id !in projectInfo.participants)
        {"User with id ${event.userId} already is a member of project!"}
    }

    fun checkRemoveUserFromProject(event: ProjectMemberRemovedEvent) {
        val projectInfo = checkProject(event.projectId)
        val userInfo = checkUser(event.userId)
        require(userInfo.id in projectInfo.participants)
        {"User with id ${event.userId} is not a member of project with id ${event.projectId}!"}
    }

    fun checkCreateTaskInProject(event: TaskCreatedEvent) {
        checkProject(event.projectId)
    }

    fun checkUpdateTask(event: TaskUpdatedEvent) {
        val projectInfo = checkProject(event.projectId)
        checkTask(event.taskId)
        require(event.taskId in projectInfo.tasks)
        {"Task with id ${event.taskId} is not in project with id ${event.projectId}!"}
    }

    fun checkAssignTask(event: TaskAssignedEvent) {
        val projectInfo = checkProject(event.projectId)
        checkTask(event.taskId)
        require(event.taskId in projectInfo.tasks)
        {"Task with id ${event.taskId} is not in project with id ${event.projectId}!"}
        checkUser(event.userId)
        require(event.userId in projectInfo.participants)
        {"User with id ${event.userId} is not a participant of project with id ${event.projectId}!"}
    }

    fun checkDeleteTask(event: TaskDeletedEvent) {
        val projectInfo = checkProject(event.projectId)
        checkTask(event.taskId)
        require(event.taskId in projectInfo.tasks)
        {"Task with id ${event.taskId} is not in project with id ${event.projectId}!"}
    }

    fun checkCreateTag(event: TagCreatedEvent) {
        val projectInfo = checkProject(event.projectId)
        checkUser(event.creatorId)
        require(event.creatorId in projectInfo.participants)
        {"User with id ${event.creatorId} is not a participant of project with id ${event.projectId}!"}
        require(event.tagName !in projectInfo.projectTags)
        {"Tag with name ${event.tagName} already exist!"}
    }

    fun checkDeleteTag(event: TagDeletedEvent) {
        val projectInfo = checkProject(event.projectID)
        val tag = checkTag(event.tagId)
        require(tag.name in projectInfo.projectTags)
        {"Tag with id ${tag.id} is not in project with id ${projectInfo.id}"}
        val tasks = projectInfo.tasks.mapNotNull { taskId ->  fetchTaskById(taskId)}
        require(tasks.none{task -> tag.name in task.tagsAssigned})
        {"Tag with id ${event.tagId} is used by some tasks! Can not delete it!"}
    }

    fun checkAddTag(event: TagAddedToTaskEvent) {
        val projectInfo = checkProject(event.projectId)
        val taskInfo = checkTask(event.taskId)
        require(event.taskId in projectInfo.tasks)
        {"Task with id ${event.taskId} is not in project with id ${event.projectId}!"}
        val tag = checkTag(event.tagId)
        require(tag.name in projectInfo.projectTags)
        {"Tag with id ${tag.id} is not in project with id ${projectInfo.id}"}
    }


    fun checkProject(projectId: UUID): ProjectInfoViewDomain.ProjectInfo {
        val projectInfo = projectInfoRepository.findById(projectId).orElse(null)
        require(projectInfo != null) {"Project with id $projectId does not exist!"}
        return projectInfo
    }

    fun checkTask(taskId: UUID): TaskInfoViewDomain.TaskInfo {
        val taskInfo = taskInfoRepository.findById(taskId).orElse(null)
        require(taskInfo != null) {"Task with id $taskId does not exist!"}
        return taskInfo
    }

    fun checkTag(tagId: UUID): TagInfoViewDomain.TagInfo {
        val tagInfo = tagInfoRepository.findById(tagId).orElse(null)
        require(tagInfo != null) {"Tag with id $tagId does not exist!"}
        return tagInfo
    }

    fun checkUser(userId: UUID): UserInfoViewDomain.UserInfo {
        val userInfo = userInfoRepository.findById(userId).orElse(null)
        require(userInfo != null) {"User with id $userId does not exist!"}
        return userInfo
    }

    fun getProject(projectId: UUID): ProjectInfoViewDomain.ProjectInfo {
        val project = projectInfoRepository.findById(projectId).orElse(null)
        require(project != null) {"Project with id $projectId does not exist!"}
        return project
    }

    fun getAllProjects(): List<ProjectInfoViewDomain.ProjectInfo> {
        return projectInfoRepository.findAll()
    }

    fun fetchUserById(userId: UUID?): UserInfoViewDomain.UserDtoData? {
        if (userId == null) return null
        val user = userInfoRepository.findById(userId).orElse(null) ?: return null
        return UserInfoViewDomain.UserDtoData(user.id, user.userCredentials)
    }

    fun fetchTaskById(taskId: UUID?): TaskInfoViewDomain.TaskInfo? {
        if (taskId == null) return null
        return taskInfoRepository.findById(taskId).orElse(null)
    }

    fun fetchTagById(tagId: UUID?): TagInfoViewDomain.TagInfo? {
        if (tagId == null) return null
        return tagInfoRepository.findById(tagId).orElse(null)
    }

    fun getProjectParticipants(projectId: UUID): List<UserInfoViewDomain.UserDtoData> {
        val project = getProject(projectId)
        val userIds = project.participants.toList()
        return userIds.mapNotNull { userId -> fetchUserById(userId) }
    }

    fun getProjectTasks(projectId: UUID): List<TaskInfoViewDomain.TaskInfo> {
        val project = getProject(projectId)
        val tasksIds = project.tasks.toList()
        return tasksIds.mapNotNull { taskId ->  fetchTaskById(taskId)}
    }

    fun getProjectTags(projectId: UUID): List<TagInfoViewDomain.TagInfo> {
        val project = getProject(projectId)
        val tags = project.projectTags.values.toList()
        return tags.mapNotNull { tagId ->  fetchTagById(tagId)}
    }

    fun getProjectCreator(projectId: UUID): UserInfoViewDomain.UserDtoData? {
        val project = getProject(projectId)
        return fetchUserById(project.creatorId)
    }

    fun getTask(taskId: UUID): TaskInfoViewDomain.TaskInfo {
        val task = taskInfoRepository.findById(taskId).orElse(null)
        require(task != null) {"Task with id $taskId does not exist!"}
        return task
    }

    fun getTaskAssignee(taskId: UUID): UserInfoViewDomain.UserDtoData? {
        val task = getTask(taskId)
        return fetchUserById(task.assigneeId)
    }

    fun getTaskTags(taskId: UUID): List<TagInfoViewDomain.TagInfo> {
        val task = getTask(taskId)
        val tags = task.tagsAssigned.values.toList()
        return tags.mapNotNull { tagId -> fetchTagById(tagId)}
    }

    fun getTag(tagId: UUID): TagInfoViewDomain.TagInfo {
        val tag = tagInfoRepository.findById(tagId).orElse(null)
        require(tag != null) {"Tag with id $tagId does not exist!"}
        return tag
    }


}
