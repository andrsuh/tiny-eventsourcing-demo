package ru.quipy.projections.view

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import ru.quipy.domain.Unique
import ru.quipy.logic.TaskEntity
import java.util.*

class ProjectInfoViewDomain {

    @Document("project-info-view")
    data class ProjectInfo(
        @Id
        override val id: UUID,
        var projectTitle: String,
        val creatorId: UUID,
        var projectDescription: String = "",
        val participants: MutableSet<UUID> = mutableSetOf(),
        val tasks: MutableSet<UUID> = mutableSetOf(),
        val projectTags: MutableMap<String, UUID> = mutableMapOf()
    ): Unique<UUID>
}