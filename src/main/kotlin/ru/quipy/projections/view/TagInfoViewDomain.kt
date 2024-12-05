package ru.quipy.projections.view

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import ru.quipy.domain.Unique
import ru.quipy.logic.Color
import ru.quipy.logic.TaskEntity
import java.util.*

class TagInfoViewDomain {
    @Document("tag-info-view")
    data class TagInfo (
        @Id
        override val id: UUID,
        val name: String = "CREATED",
        val color: Color = Color.GREEN
    ): Unique<UUID>

    enum class Color {
        GREEN, BLUE, YELLOW, RED, PURPLE
    }
}