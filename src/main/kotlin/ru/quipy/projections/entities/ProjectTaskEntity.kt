package ru.quipy.projections.entities

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "project_tasks")
data class ProjectTaskEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "task_id", nullable = false)
    val taskId: UUID,
    @Column(name = "project_id", nullable = false)
    val projectId: UUID,
    @Column(name = "name", nullable = false)
    var name: String,
    @Column(name = "description", nullable = false)
    val description: String,
    @Column(name = "status_name", nullable = false)
    var statusName: String,

    @ElementCollection
    @CollectionTable(name = "task_assignees", joinColumns = [JoinColumn(name = "task_id")])
    @Column(name = "participant_id")
    val assignees: MutableSet<UUID> = mutableSetOf()
) : Serializable

