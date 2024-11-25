package ru.quipy.projections.entities

import ru.quipy.api.Color
import java.io.Serializable
import java.util.*
import javax.persistence.*



@Entity
@Table(name= "project_statuses")
class ProjectsStatusEntity (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "project_id", nullable = false)
    val projectId: UUID,
    @Column(name = "name", nullable = false)
    val name: String,
    @Column(name = "color", nullable = false)
    val color: Color
    ): Serializable{
    constructor() : this(null, UUID.randomUUID(), "", Color.RED)
}