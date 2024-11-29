package ru.quipy.projections.entities

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "project_participants")
data class ProjectParticipantEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "project_id", nullable = false)
    val projectId: UUID,
    @Column(name = "participant_id", nullable = false)
    val participantId: UUID
) : Serializable
