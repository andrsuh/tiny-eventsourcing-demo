package ru.quipy.controller

import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import java.util.*

class UserController(val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>) {
}