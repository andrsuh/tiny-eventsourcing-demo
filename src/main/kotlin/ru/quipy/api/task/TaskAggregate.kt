package ru.quipy.api.task

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

@AggregateType(aggregateEventsTableName = "aggregate-task")
class TaskAggregate : Aggregate