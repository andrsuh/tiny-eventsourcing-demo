//package ru.quipy.projections.entities
//import java.io.Serializable
//import java.util.*
//import javax.persistence.*
//
//@Entity
//@Table(name = "task_assignees")
//data class TaskAssigneeEntity(
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    val id: Long? = null,
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "task_id", nullable = false)
//    val task: ProjectTaskEntity,
//
//    @Column(name = "participant_id", nullable = false)
//    val participantId: UUID
//) : Serializable
