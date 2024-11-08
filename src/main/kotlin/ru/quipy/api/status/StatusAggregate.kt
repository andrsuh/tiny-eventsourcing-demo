package ru.quipy.api.status

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

@AggregateType(aggregateEventsTableName = "aggregate-status")
class StatusAggregate : Aggregate
