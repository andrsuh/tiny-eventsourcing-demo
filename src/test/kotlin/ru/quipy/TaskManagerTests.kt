package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.controller.ProjectController
import ru.quipy.controller.UserController

@SpringBootTest
class TaskManagerTests {

    @Autowired
    private lateinit var userController: UserController

    @Autowired
    private lateinit var projectController: ProjectController

    @Test
    fun registerUserAndCompareWithReceivedUser() {
        val createdUser = userController.registerUser(
                "krugarrr",
                "sashulkaterentulka",
                "ppoklassniypredmet)")
        val receivedUser = userController.getAccount(createdUser.userId);
        println()
        Assertions.assertNotNull(receivedUser)
        Assertions.assertEquals(createdUser.username, receivedUser?.username)
        Assertions.assertEquals(createdUser.userId, receivedUser?.getId())
    }

    @Test
    fun createProjectAndAddParticipant() {
        val project = projectController.createProject(
                "TaskManager",
                "krugarrr",
                "sashulkaterentulka",
                "Very cool and modern project made in USA, Omsk state")


		val receivedProject = projectController.getProject(project.projectId)
        Assertions.assertNotNull(receivedProject)
		Assertions.assertEquals(1, receivedProject?.participants?.values?.count())
		Assertions.assertEquals("krugarrr", receivedProject?.participants?.entries?.firstOrNull()?.value?.username)


		projectController.addParticipant(
				project.projectId,
				"pypkaed",
				"zarinkamandarinka"
		)

        val projectWithUpdatedParticipants = projectController.getProject(project.projectId)
		val newAddedParticipant = projectWithUpdatedParticipants?.participants?.values?.filter { it.username == "pypkaed" }?.first()
        Assertions.assertEquals("pypkaed", newAddedParticipant?.username)
        Assertions.assertEquals("zarinkamandarinka", newAddedParticipant?.fullName)
        Assertions.assertEquals(2, projectWithUpdatedParticipants?.participants?.values?.count())
    }

    @Test
    fun createTaskAndChangeStatus() {
        val project = projectController.createProject(
                "TaskManager",
                "krugarrr",
                "sashulkaterentulka",
                "Very cool and modern project made in USA, Omsk state")


        projectController.createTask(project.projectId, "Watch skibidi guide")
        val receivedProject = projectController.getProject(project.projectId)
        val task = receivedProject?.tasks?.entries?.first()?.value
        val defaultStatus = receivedProject?.taskStatuses?.entries?.first()?.value

        Assertions.assertEquals(1, receivedProject?.tasks?.values?.count())
        Assertions.assertEquals(1, receivedProject?.taskStatuses?.values?.count())
        Assertions.assertEquals(defaultStatus?.id, task?.taskStatusesAssigned?.first())

        projectController.createTaskStatus(project.projectId, "Done", "Red")
        val receivedProjectWithNewStatus = projectController.getProject(project.projectId)
        val newTaskStatus = receivedProjectWithNewStatus?.taskStatuses?.values?.filter { it.name == "Done" }?.first()

        Assertions.assertEquals(2, receivedProjectWithNewStatus?.taskStatuses?.values?.count())
        Assertions.assertNotNull(newTaskStatus)
        Assertions.assertEquals("Red", newTaskStatus?.colour)

        //ну, простите уж)
        if (newTaskStatus != null) {
            if (task != null) {
                projectController.createTaskStatus(project.projectId, newTaskStatus.id, task.id)
            }
        }

        val receivedProjectWithNewTaskStatus = projectController.getProject(project.projectId)
        val updatedTask = receivedProjectWithNewTaskStatus?.tasks?.entries?.first()?.value

        Assertions.assertEquals(newTaskStatus?.id, updatedTask?.taskStatusesAssigned?.last())

    }

    @Test
    fun createTaskAndAssignParticipant() {
        val project = projectController.createProject(
                "TaskManager",
                "krugarrr",
                "sashulkaterentulka",
                "Very cool and modern project made in USA, Omsk state")


        projectController.createTask(project.projectId, "Watch skibidi guide")
        val receivedProject = projectController.getProject(project.projectId)
        val task = receivedProject?.tasks?.entries?.first()?.value
        val participant = project.participants.entries.first().value;
        if (task != null) {
            projectController.addParticipant(project.projectId, task.id, participant.id)
        }
        val receivedProjectWithUpdatedTask = projectController.getProject(project.projectId)
        val updatedTask = receivedProjectWithUpdatedTask?.tasks?.entries?.first()?.value
        Assertions.assertEquals(1, updatedTask?.performersAssigned?.count())
        Assertions.assertNotEquals(task?.performersAssigned?.count(), updatedTask?.performersAssigned?.count())
        Assertions.assertEquals(participant.id, updatedTask?.performersAssigned?.first())
    }


}
