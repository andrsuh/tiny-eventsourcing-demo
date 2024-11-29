//package ru.quipy.projections
//
//import org.slf4j.LoggerFactory
//import org.springframework.stereotype.Service
//import ru.quipy.api.*
//import ru.quipy.projections.repository.TaskInfoRepository
//import ru.quipy.streams.annotation.AggregateSubscriber
//import ru.quipy.streams.annotation.SubscribeEvent
//
//@Service
//@AggregateSubscriber(aggregateClass = ProjectAggregate::class, subscriberName = "project-projection-subscriber")
//class ProjectProjectionSubscriber(
//        private val projectProjectionRepository: TaskInfoRepository
//) {
//    private val logger = LoggerFactory.getLogger(ProjectProjectionSubscriber::class.java)
//
//    @SubscribeEvent
//    fun onProjectCreated(event: ProjectCreatedEvent) {
//        logger.info("Handling ProjectCreatedEvent for projectId: {}", event.projectId)
//        val projectProjection = ProjectProjection(
//                projectId = event.projectId,
//                title = event.title,
//                description = event.description,
//                updatedAt = event.createdAt
//        )
//        projectProjectionRepository.save(projectProjection)
//    }
//
//    @SubscribeEvent
//    fun onProjectUpdated(event: ProjectUpdatedEvent) {
//        logger.info("Handling ProjectUpdatedEvent for projectId: {}", event.projectId)
//        val project = projectProjectionRepository.findById(event.projectId).orElse(null)
//        if (project != null) {
//            project.title = event.title
//            project.description = event.description
//            project.updatedAt = event.createdAt
//            projectProjectionRepository.save(project)
//        } else {
//            logger.warn("ProjectProjection not found for projectId: {}", event.projectId)
//        }
//    }
//
//    @SubscribeEvent
//    fun onTaskCreated(event: TaskCreatedEvent) {
//        logger.info("Handling TaskCreatedEvent for projectId: {}, taskId: {}", event.projectId, event.taskId)
//        val project = projectProjectionRepository.findById(event.projectId).orElse(null)
//        if (project != null) {
//            val task = TaskProjection(
//                    taskId = event.taskId,
//                    name = event.taskName,
//                    description = event.taskDescription,
//                    statusId = event.statusId
//            )
//            project.tasks.add(task)
//            project.updatedAt = event.createdAt
//            projectProjectionRepository.save(project)
//        } else {
//            logger.warn("ProjectProjection not found for projectId: {}", event.projectId)
//        }
//    }
//
//    @SubscribeEvent
//    fun onTaskUpdated(event: TaskUpdatedEvent) {
//        logger.info("Handling TaskUpdatedEvent for projectId: {}, taskId: {}", event.projectId, event.taskId)
//        val project = projectProjectionRepository.findById(event.projectId).orElse(null)
//        if (project != null) {
//            val task = project.tasks.find { it.taskId == event.taskId }
//            if (task != null) {
//                task.name = event.taskName
//                task.description = event.taskDescription
//                project.updatedAt = event.createdAt
//                projectProjectionRepository.save(project)
//            } else {
//                logger.warn("TaskProjection not found for taskId: {}", event.taskId)
//            }
//        } else {
//            logger.warn("ProjectProjection not found for projectId: {}", event.projectId)
//        }
//    }
//
//    @SubscribeEvent
//    fun onStatusCreated(event: StatusCreatedEvent) {
//        logger.info("Handling StatusCreatedEvent for projectId: {}, statusId: {}", event.projectId, event.statusId)
//        val project = projectProjectionRepository.findById(event.projectId).orElse(null)
//        if (project != null) {
//            val status = StatusProjection(
//                    statusId = event.statusId,
//                    name = event.statusName,
//                    order = event.order
//            )
//            project.statuses.add(status)
//            project.updatedAt = event.createdAt
//            projectProjectionRepository.save(project)
//        } else {
//            logger.warn("ProjectProjection not found for projectId: {}", event.projectId)
//        }
//    }
//
//    @SubscribeEvent
//    fun onStatusDeleted(event: StatusDeletedEvent) {
//        logger.info("Handling StatusDeletedEvent for projectId: {}, statusId: {}", event.projectId, event.statusId)
//        val project = projectProjectionRepository.findById(event.projectId).orElse(null)
//        if (project != null) {
//            val removed = project.statuses.removeIf { it.statusId == event.statusId }
//            if (removed) {
//                project.updatedAt = event.createdAt
//                projectProjectionRepository.save(project)
//            } else {
//                logger.warn("StatusProjection not found for statusId: {}", event.statusId)
//            }
//        } else {
//            logger.warn("ProjectProjection not found for projectId: {}", event.projectId)
//        }
//    }
//
//    @SubscribeEvent
//    fun onStatusAssignedToTask(event: StatusAssignedToTaskEvent) {
//        logger.info(
//                "Handling StatusAssignedToTaskEvent for projectId: {}, taskId: {}, statusId: {}",
//                event.projectId, event.taskId, event.statusId
//        )
//        val project = projectProjectionRepository.findById(event.projectId).orElse(null)
//        if (project != null) {
//            val task = project.tasks.find { it.taskId == event.taskId }
//            if (task != null) {
//                task.statusId = event.statusId
//                project.updatedAt = event.createdAt
//                projectProjectionRepository.save(project)
//            } else {
//                logger.warn("TaskProjection not found for taskId: {}", event.taskId)
//            }
//        } else {
//            logger.warn("ProjectProjection not found for projectId: {}", event.projectId)
//        }
//    }
//
//    @SubscribeEvent
//    fun onStatusOrderChanged(event: StatusOrderChangedEvent) {
//        logger.info("Handling StatusOrderChangedEvent for projectId: {}", event.projectId)
//        val project = projectProjectionRepository.findById(event.projectId).orElse(null)
//        if (project != null) {
//            event.order.forEach { (statusId, newOrder) ->
//                val status = project.statuses.find { it.statusId == statusId }
//                if (status != null) {
//                    status.order = newOrder
//                } else {
//                    logger.warn("StatusProjection not found for statusId: {}", statusId)
//                }
//            }
//            project.updatedAt = event.createdAt
//            projectProjectionRepository.save(project)
//        } else {
//            logger.warn("ProjectProjection not found for projectId: {}", event.projectId)
//        }
//    }
//
//    @SubscribeEvent
//    fun onPerformerAssignedToTask(event: PerformerAssignedToTaskEvent) {
//        logger.info(
//                "Handling PerformerAssignedToTaskEvent for projectId: {}, taskId: {}, userId: {}",
//                event.projectId, event.taskId, event.userId
//        )
//        val project = projectProjectionRepository.findById(event.projectId).orElse(null)
//        if (project != null) {
//            val task = project.tasks.find { it.taskId == event.taskId }
//            if (task != null) {
//                if (!task.performers.contains(event.userId)) {
//                    task.performers.add(event.userId)
//                    project.updatedAt = event.createdAt
//                    projectProjectionRepository.save(project)
//                } else {
//                    logger.warn("UserId: {} already assigned to taskId: {}", event.userId, event.taskId)
//                }
//            } else {
//                logger.warn("TaskProjection not found for taskId: {}", event.taskId)
//            }
//        } else {
//            logger.warn("ProjectProjection not found for projectId: {}", event.projectId)
//        }
//    }
//}
