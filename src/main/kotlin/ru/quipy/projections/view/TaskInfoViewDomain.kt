package ru.quipy.projections.view

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import ru.quipy.domain.Unique
import java.util.*

class TaskInfoViewDomain {
    @Document("task-info-view")
    data class TaskInfo (
        @Id
        override val id: UUID,
        var name: String,
        var description: String = "",
        var assigneeId: UUID? = null,
        val tagsAssigned: MutableMap<String, UUID> = mutableMapOf()
    ): Unique<UUID>
}