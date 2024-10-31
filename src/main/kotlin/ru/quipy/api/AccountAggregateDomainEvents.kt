package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.math.BigDecimal
import java.util.*

const val ACCOUNT_CREATED = "ACCOUNT_CREATED_EVENT"
const val BANK_ACCOUNT_CREATED = "BANK_ACCOUNT_CREATED_EVENT"
const val BANK_ACCOUNT_DELETED = "BANK_ACCOUNT_DELETED_EVENT"
const val BANK_ACCOUNT_DEPOSIT = "BANK_ACCOUNT_DEPOSIT_EVENT"
const val BANK_ACCOUNT_WITHDRAWAL = "BANK_ACCOUNT_WITHDRAWAL_EVENT"
const val INTERNAL_ACCOUNT_TRANSFER = "INTERNAL_ACCOUNT_TRANSFER_EVENT"

// API
@DomainEvent(name = ACCOUNT_CREATED_EVENT)
class AccountCreatedEvent(
    val accountId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<AccountAggregate>(
    name = ACCOUNT_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = BANK_ACCOUNT_CREATED_EVENT)
class BankAccountCreatedEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<AccountAggregate>(
    name = BANK_ACCOUNT_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = BANK_ACCOUNT_DELETED_EVENT)
class BankAccountDeletedEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<AccountAggregate>(
    name = BANK_ACCOUNT_DELETED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = BANK_ACCOUNT_DEPOSIT_EVENT)
class BankAccountDepositEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
    val amount: BigDecimal,
    createdAt: Long = System.currentTimeMillis(),
) : Event<AccountAggregate>(
    name = BANK_ACCOUNT_DEPOSIT_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = BANK_ACCOUNT_WITHDRAWAL_EVENT)
class BankAccountWithdrawalEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
    val amount: BigDecimal,
    createdAt: Long = System.currentTimeMillis(),
) : Event<AccountAggregate>(
    name = BANK_ACCOUNT_WITHDRAWAL_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = INTERNAL_ACCOUNT_TRANSFER_EVENT)
class InternalAccountTransferEvent(
    val accountId: UUID,
    val bankAccountIdFrom: UUID,
    val bankAccountIdTo: UUID,
    val amount: BigDecimal,
    createdAt: Long = System.currentTimeMillis(),
) : Event<AccountAggregate>(
    name = INTERNAL_ACCOUNT_TRANSFER_EVENT,
    createdAt = createdAt
)