package ru.quipy

import javassist.NotFoundException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.api.StatusCreatedEvent
import ru.quipy.api.UserCreatedEvent
import ru.quipy.controller.ProjectController
import ru.quipy.controller.TaskController
import ru.quipy.controller.UserController
import ru.quipy.entity.StatusEntity
import ru.quipy.enum.ColorEnum
import ru.quipy.logic.state.ProjectAggregateState
import java.lang.IllegalArgumentException
import java.util.UUID

@SpringBootTest
class TaskAndStatusControllerTests {
    private lateinit var projectId: UUID
    private lateinit var taskId: UUID
    private lateinit var ownerId: UUID
    private lateinit var userId: UUID
    private lateinit var defStatusId: UUID
    private lateinit var statusId2: UUID


    @Autowired
    private lateinit var userController: UserController

    @Autowired
    private lateinit var projectController: ProjectController


    @Autowired
    private lateinit var taskController: TaskController

    @Test
    fun All_createTaskAndStatuses_TaskAndStatusesCreatedWithCorrectFields() {

        val owner = createUser("Owner")
        ownerId = owner.userId
        val user = createUser("Participant")
        userId = user.userId

        val project = createProject(owner.userId)
        projectId = project!!.getId()

        val taskAggregate = taskController.getTaskStatusesAndTasks(project!!.getId())

        Assertions.assertNotNull(taskAggregate)
        Assertions.assertEquals(1, taskAggregate!!.getStatuses().size)
        Assertions.assertEquals("CREATED", taskAggregate.getStatuses()[0].name)
        Assertions.assertEquals(ColorEnum.GREEN, taskAggregate.getStatuses()[0].color)
        Assertions.assertEquals(1, taskAggregate.getStatuses()[0].position)
        Assertions.assertEquals(0, taskAggregate.getTasks().size)

        val defStatus = taskController.getStatus(projectId, taskAggregate.getStatuses()[0].id)
        defStatusId = defStatus!!.id

        Assertions.assertNotNull(defStatus)
        Assertions.assertEquals("CREATED", defStatus!!.name)
        Assertions.assertEquals(ColorEnum.GREEN, defStatus.color)
        Assertions.assertEquals(1, defStatus.position)

        val status2 = taskController.createStatus(
                projectId,
                "In progress",
                "YELLOW"
        )
        statusId2 = status2.statusId

        val statusAgg2 = taskController.getStatus(projectId, status2.statusId)
        Assertions.assertNotNull(statusAgg2)
        Assertions.assertEquals("In progress", statusAgg2!!.name)
        Assertions.assertEquals(ColorEnum.YELLOW, statusAgg2.color)
        Assertions.assertEquals(2, statusAgg2.position)

        val task = taskController.createTask(
                projectId,
                "Task",
                "Task d",
                status2.statusId,
        )
        taskId = task.taskId

        val taskAgg = taskController.getTask(projectId, task.taskId)
        Assertions.assertNotNull(taskAgg)
        Assertions.assertEquals("Task", taskAgg!!.name)
        Assertions.assertEquals("Task d", taskAgg.description)
        Assertions.assertEquals(0, taskAgg.executors.size)
        Assertions.assertEquals(status2.statusId, taskAgg.statusId)
        Assertions.assertEquals(project.getId(), taskAgg.projectId)

        Assertions.assertThrows(
                IllegalStateException::class.java
        ) {
            taskController.deleteStatus(projectId, status2.statusId)
        }

        ChangeTaskStatus_StatusChanged()
        UpdateTaskAndDeleteStatus_TaskChangedAndStatusDeleted()
        AddExecutors_ExecutorsAddedAndThrowsExceptionIfNotParticipant()
        AddExistingInProjectStatus_ThrowsException()
        ChangeStatusesSequence_SequenceChangedCorrectlyAndThrowsExceptionIfWrongPosition()
    }

    fun ChangeTaskStatus_StatusChanged() {
        val taskUpdatedStatus = taskController.changeStatus(
                projectId,
                taskId,
                defStatusId
        )

        val taskAggUpdatedStatus = taskController.getTask(projectId, taskUpdatedStatus.taskId)
        Assertions.assertNotNull(taskAggUpdatedStatus)
        Assertions.assertEquals("Task", taskAggUpdatedStatus!!.name)
        Assertions.assertEquals(defStatusId, taskAggUpdatedStatus.statusId)
    }

    fun UpdateTaskAndDeleteStatus_TaskChangedAndStatusDeleted() {
        val changedTask = taskController.updateTask(
                projectId,
                taskId,
                "Task new",
                "Task d new"
        )

        val changedTaskAggr = taskController.getTask(projectId, changedTask.taskId)
        Assertions.assertNotNull(changedTaskAggr)
        Assertions.assertEquals("Task new", changedTaskAggr!!.name)
        Assertions.assertEquals("Task d new", changedTaskAggr.description)

        taskController.deleteStatus(projectId, statusId2)
        val taskAgg = taskController.getTaskStatusesAndTasks(projectId)

        Assertions.assertNotNull(taskAgg)
        Assertions.assertEquals(1, taskAgg!!.getStatuses().size)
        Assertions.assertEquals("CREATED", taskAgg.getStatuses()[0].name)
        Assertions.assertEquals(ColorEnum.GREEN, taskAgg.getStatuses()[0].color)
        Assertions.assertEquals(1, taskAgg.getStatuses()[0].position)
        Assertions.assertEquals(1, taskAgg.getTasks().size)
    }

    fun AddExecutors_ExecutorsAddedAndThrowsExceptionIfNotParticipant() {
        val executorAdded = taskController.addExecutor(
                projectId,
                taskId,
                ownerId
        )

        val executors = taskController.getTask(projectId, taskId)!!.executors

        Assertions.assertEquals(1, executors.size)
        Assertions.assertEquals(ownerId, executors[0])

        Assertions.assertThrows(
                NotFoundException::class.java
        ) {
            taskController.addExecutor(
                    projectId,
                    taskId,
                    userId
            )
        }
    }

    fun AddExistingInProjectStatus_ThrowsException() {
        Assertions.assertThrows(
                IllegalArgumentException::class.java
        ) {
            taskController.createStatus(
                    projectId,
                    "CREATED",
                    "YELLOW"
            )
        }

        Assertions.assertEquals(1, taskController.getTask(projectId, taskId)!!.executors.size)
    }

    fun ChangeStatusesSequence_SequenceChangedCorrectlyAndThrowsExceptionIfWrongPosition() {
        val status2 = taskController.createStatus(
                projectId,
                "In progress",
                "YELLOW"
        )

        val status3 = taskController.createStatus(
                projectId,
                "Postronded",
                "ORANGE"
        )

        taskController.changeTaskStatusPosition(projectId, status3.statusId, 2)

        Assertions.assertEquals(1, taskController.getStatus(projectId, defStatusId)!!.position)
        Assertions.assertEquals(3, taskController.getStatus(projectId, status2.statusId)!!.position)
        Assertions.assertEquals(2, taskController.getStatus(projectId, status3.statusId)!!.position)

        Assertions.assertThrows(
                IllegalArgumentException::class.java
        ) {
            taskController.changeTaskStatusPosition(projectId, status3.statusId, 4)
        }

        Assertions.assertThrows(
                IllegalArgumentException::class.java
        ) {
            taskController.changeTaskStatusPosition(projectId, status3.statusId, -1)
        }

        Assertions.assertThrows(
                IllegalArgumentException::class.java
        ) {
            taskController.changeTaskStatusPosition(projectId, status3.statusId, 0)
        }
    }

    private fun createUser(name: String): UserCreatedEvent {
        return userController.createUser(
                "nick-$name",
                name,
                "password"
        )
    }

    private fun createProject(ownerId: UUID): ProjectAggregateState? {
        val response = projectController.createProject(
                "project",
                ownerId
        )

        return projectController.getProject(response.projectId)
    }
}