package ru.quipy.utils

import ru.quipy.api.ProjectAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

fun createUser(service: EventSourcingService<UUID, UserAggregate, UserAggregateState>, username: String, realname:String, password: String): UUID {
    val uuid = UUID.randomUUID()
    service.create { it.createUser(uuid, username, realname, password) }
    return uuid
}
fun createProject(service: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>, title: String, creatorId: UUID): Triple<UUID, UUID, UUID> {
    val uuid = UUID.randomUUID()
    val event = service.create { it.create(uuid, title, creatorId) }
    val userMemberUuid = event.creatorMemberId
    val statusIdDefaultUuid = event.defaultStatusId
    return Triple<UUID, UUID, UUID>(uuid, userMemberUuid, statusIdDefaultUuid)
}
fun createTask(service: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>, projectId: UUID, taskName: String, statusId: UUID): UUID {
    val uuid = UUID.randomUUID()
    val event = service.update(projectId) { it.createTask(uuid, taskName, statusId) }
    return uuid
}
fun createStatus(service: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>, projectId: UUID, statusText: String, statusColor: String): UUID {
    val uuid = UUID.randomUUID()
    val event = service.update(projectId) { it.createStatus(uuid, statusText, statusColor) }
    return uuid
}