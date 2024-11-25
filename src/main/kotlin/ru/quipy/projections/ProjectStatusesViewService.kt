package ru.quipy.projections
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.StatusColorChangedEvent
import ru.quipy.api.StatusCreatedEvent
import ru.quipy.api.StatusDeletedEvent
import ru.quipy.projections.entities.ProjectsStatusEntity
import ru.quipy.projections.repositories.ProjectStatusRepository
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
@AggregateSubscriber(aggregateClass = ProjectAggregate::class, subscriberName = "project-statuses-subscriber")
class ProjectStatusesViewService {

    @Autowired
    lateinit var projectStatusRepository: ProjectStatusRepository

    @SubscribeEvent
    fun onStatusCreated(event: StatusCreatedEvent) {
        val status = ProjectsStatusEntity(
            projectId = event.projectId,
            name = event.statusName,
            color = event.color

        )

        projectStatusRepository.save(status)
        println("Я сохранил!!!")
    }

    @SubscribeEvent
    fun onStatusColorChanged(event: StatusColorChangedEvent) {
        val existingStatus = projectStatusRepository.findByName(event.statusName, event.projectId)
        val updatedStatus = ProjectsStatusEntity(
            id = existingStatus.id,
            projectId = existingStatus.projectId,
            name = existingStatus.name,
            color = event.newColor
        )
        projectStatusRepository.save(updatedStatus)
    }


    @SubscribeEvent
    fun onStatusDeleted(event: StatusDeletedEvent) {
        val existingStatus = projectStatusRepository.findByName(event.statusName, event.projectId)

        projectStatusRepository.delete(existingStatus)
    }


    fun getStatuses(projectId: UUID): List<ProjectsStatusEntity> {
        return projectStatusRepository.findStatusesByRepository(projectId)
    }
}
