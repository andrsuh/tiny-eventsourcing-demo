package ru.quipy.logic

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectTitle: String
    lateinit var creatorId: String
    var statuses = mutableMapOf<UUID, StatusEntity>()
    var users = mutableListOf<UUID>()

    companion object{
        val defaultStatus = StatusEntity(color = "black", name = "default", id = UUID.fromString("b2b0f014-984a-4cd9-8f9b-01ae022d100a"))
    }


    override fun getId() = projectId


    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun projectExecutorAddedApply(event: ProjectExecutorAddedEvent) {
        users.add(event.userId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent){
        val status = StatusEntity(color = event.color, name = event.statusName, id = event.statusId)
        statuses.put(status.id, status)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent){
        if (statuses[event.statusId]?.count == 0){
            statuses.remove(event.statusId)
        }
    }

    @StateTransitionFunc
    fun statusesCountChangedApply(event: StatusesCountChangedEvent){
        if(event.oldStatusId != defaultStatus.id)
            statuses.get(event.oldStatusId)!!.count--
        statuses.get(event.newStatusId)!!.count++
//        logger.info("Status changed: from {} to {}", statuses.get(event.oldStatusId)!!.count,
//            statuses.get(event.newStatusId)!!.count)

    }
}


data class StatusEntity(
    val id: UUID,
    val color: String,
    val name: String,
    var count: Int = 0
)


