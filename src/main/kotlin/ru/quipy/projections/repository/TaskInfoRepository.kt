package ru.quipy.projections.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.projections.view.TagInfoViewDomain
import ru.quipy.projections.view.TaskInfoViewDomain
import java.util.*

interface TaskInfoRepository: MongoRepository<TaskInfoViewDomain.TaskInfo, UUID>