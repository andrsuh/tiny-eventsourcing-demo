package ru.quipy

import javassist.NotFoundException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.api.UserCreatedEvent
import ru.quipy.controller.ProjectController
import ru.quipy.controller.UserController
import ru.quipy.enum.ColorEnum
import java.util.*

@SpringBootTest
class ProjectControllerTests {

    @Autowired
    private lateinit var userController: UserController

    @Autowired
    private lateinit var projectController: ProjectController

    @Test
    fun createProject_ProjectCreatedWithCorrectFieldsAndCreatedAsParticipant() {
        val owner = createUser("Owner")
        val user = createUser("Participant")
        val project = projectController.createProject(
                "project",
                owner.userId
        )
        Assertions.assertEquals(1, project.version)
        Assertions.assertEquals("project", project.projectName)

        var gotProject = projectController.getProject(project.projectId)
        Assertions.assertNotNull(gotProject)

        // Creator is added to project participants check
        val ownerInProject = gotProject!!.getParticipantById(owner.userId)
        Assertions.assertNotNull(ownerInProject)
        Assertions.assertEquals("project", gotProject.getName())
        Assertions.assertEquals(owner.userId, ownerInProject!!)
        Assertions.assertEquals(1, gotProject.getParticipants().size)


        //User is not in participants list check
        Assertions.assertEquals(null, gotProject!!.getParticipantById(user.userId))
    }

    @Test
    fun createAddParticipant_ParticipantAdded() {
        val owner = createUser("Owner")
        val user = createUser("Participant")
        val project = projectController.createProject(
                "project",
                owner.userId
        )

        var gotProject = projectController.getProject(project.projectId)
        Assertions.assertNotNull(gotProject)

        projectController.addParticipant(
                gotProject!!.getId(),
                user.userId
        )

        gotProject = projectController.getProject(gotProject.getId())

        val userInProject = gotProject!!.getParticipantById(user.userId)

        Assertions.assertNotNull(userInProject)
        Assertions.assertEquals(user.userId, userInProject!!)
        Assertions.assertEquals(2, gotProject.getParticipants().size)
    }

    @Test
    fun AddNotExistingUserAsParticipantToProject_ThrowsException() {
        val owner = createUser("Owner")
        val user = createUser("Participant")
        val project = projectController.createProject(
                "project",
                owner.userId
        )
        Assertions.assertEquals(1, project.version)
        Assertions.assertEquals("project", project.projectName)

        var gotProject = projectController.getProject(project.projectId)
        Assertions.assertEquals(null, gotProject!!.getParticipantById(user.userId))

        Assertions.assertThrows(
                NotFoundException::class.java
        ) {
            projectController.addParticipant(
                    gotProject.getId(),
                    UUID.randomUUID()
            )

        }
    }

    private fun createUser(name: String): UserCreatedEvent {
        return userController.createUser(
                "nick-$name",
                name,
                "password"
        )
    }
}