package ru.quipy.projections.entities

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user_table")
data class UserEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "user_id", nullable = false)
    val userId: UUID = UUID.randomUUID(),
    @Column(name = "nickname", nullable = false)
    val nickname: String = "",
    @Column(name = "name", nullable = false)
    val name: String = "",
    @Column(name = "password", nullable = false)
    val password: String = ""
    ) : Serializable