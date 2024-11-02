package ru.quipy.logic.command

import ru.quipy.api.*
import ru.quipy.logic.state.ProjectAggregateState
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.createProject(
        id: UUID,
        tasksAggregateId: UUID,
        name: String
): ProjectCreatedEvent {
    return ProjectCreatedEvent(
            projectId = id,
            tasksAndStatusAggregateId = tasksAggregateId,
            projectName = name
    )
}

//fun ProjectAggregateState.updateProject(id: UUID, name: String): ProjectUpdatedEvent {
//    return ProjectUpdatedEvent(
//            projectId = id,
//            projectName = name
//    )
//}

fun ProjectAggregateState.addParticipantById(userId: UUID): ParticipantAddedEvent {
    return ParticipantAddedEvent(projectId = this.getId(), userId = userId)
}




//fun ProjectAggregateState.createProject(id: UUID, title: String, creatorId: UUID): ProjectCreatedEvent {
//    return ProjectCreatedEvent(
//        projectId = id,
//        title = title,
//        creatorId = creatorId,
//    )
//}

//fun ProjectAggregateState.addTask(name: String): TaskCreatedEvent {
//    return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name)
//}

//fun ProjectAggregateState.createTag(name: String): TagCreatedEvent {
//    if (projectTags.values.any { it.name == name }) {
//        throw IllegalArgumentException("Tag already exists: $name")
//    }
//    return TagCreatedEvent(projectId = this.getId(), tagId = UUID.randomUUID(), tagName = name)
//}
//
//fun ProjectAggregateState.assignTagToTask(tagId: UUID, taskId: UUID): TagAssignedToTaskEvent {
//    if (!projectTags.containsKey(tagId)) {
//        throw IllegalArgumentException("Tag doesn't exists: $tagId")
//    }
//
//    if (!tasks.containsKey(taskId)) {
//        throw IllegalArgumentException("Task doesn't exists: $taskId")
//    }
//
//    return TagAssignedToTaskEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
//}