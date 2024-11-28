package ru.quipy.projections
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.projections.entities.ProjectsStatusEntity
import ru.quipy.projections.repositories.ProjectStatusRepository
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.*
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
@AggregateSubscriber(aggregateClass = ProjectAggregate::class, subscriberName = "project-statuses-subscriber")
class ProjectStatusesViewService {

    @Autowired
    lateinit var projectStatusRepository: ProjectStatusRepository

    @SubscribeEvent
    @Transactional
    fun onStatusCreated(event: StatusCreatedEvent) {
        val maxOrder = projectStatusRepository.findMaxOrder(event.projectId) ?: 0
        val status = ProjectsStatusEntity(
            projectId = event.projectId,
            name = event.statusName,
            color = event.color,
            statusOrder = maxOrder + 1
        )

        projectStatusRepository.save(status)
    }

    @SubscribeEvent
    @Transactional
    fun onStatusColorChanged(event: StatusColorChangedEvent) {
        val existingStatus = projectStatusRepository.findByName(event.statusName, event.projectId)
        val updatedStatus = ProjectsStatusEntity(
            id = existingStatus.id,
            projectId = existingStatus.projectId,
            name = existingStatus.name,
            color = event.newColor,
            statusOrder = existingStatus.statusOrder
        )
        projectStatusRepository.save(updatedStatus)
    }

    @SubscribeEvent
    @Transactional
    fun onStatusOrderChanged(event: StatusOrderChangedEvent) {
        val existingStatus = projectStatusRepository.findByName(event.statusName, event.projectId)

        val currentOrder = existingStatus.statusOrder

        val newOrder = event.newOrder

        if (currentOrder < newOrder) {
            projectStatusRepository.decrementOrderBetween(currentOrder + 1, newOrder, event.projectId)
        } else if (currentOrder > newOrder) {
            projectStatusRepository.incrementOrderBetween(newOrder, currentOrder - 1, event.projectId)
        }

        val updatedStatus = ProjectsStatusEntity(
            id = existingStatus.id,
            projectId = existingStatus.projectId,
            name = existingStatus.name,
            color = existingStatus.color,
            statusOrder = newOrder
        )
        projectStatusRepository.save(updatedStatus)
    }


    @SubscribeEvent
    @Transactional
    fun onStatusDeleted(event: StatusDeletedEvent) {
        val existingStatus = projectStatusRepository.findByName(event.statusName, event.projectId)
        val order = existingStatus.statusOrder
        projectStatusRepository.delete(existingStatus)
        projectStatusRepository.updateOrderAfterDelete(order, event.projectId)
    }


    fun getStatuses(projectId: UUID): List<ProjectsStatusEntity> {
        return projectStatusRepository.findStatusesByRepository(projectId)
    }
}
