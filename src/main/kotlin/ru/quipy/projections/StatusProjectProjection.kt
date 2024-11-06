package ru.quipy.projections

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*

@Component
class StatusProjectRelation(
    private val statusProjectProjectionRepo: StatusProjectProjectionRepo
) {

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager


}

@Document("status-project-projection")
data class StatusProjectProjection(
    var statusId: UUID,
    var statusName: String?,
    var projectId: UUID,
)
@Repository
interface StatusProjectProjectionRepo : MongoRepository<StatusProjectProjection, UUID>{
    fun findAllByProjectIdNotNull(projectId: UUID): List<StatusProjectProjection>
}