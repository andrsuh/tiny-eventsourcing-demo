package ru.quipy.projections.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.projections.view.ProjectInfoViewDomain
import ru.quipy.projections.view.TagInfoViewDomain
import java.util.*

interface TagInfoRepository: MongoRepository<TagInfoViewDomain.TagInfo, UUID>