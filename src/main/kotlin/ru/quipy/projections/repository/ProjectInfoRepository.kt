package ru.quipy.projections.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import ru.quipy.projections.view.ProjectInfoViewDomain
import java.util.UUID

interface ProjectInfoRepository: MongoRepository<ProjectInfoViewDomain.ProjectInfo, UUID>