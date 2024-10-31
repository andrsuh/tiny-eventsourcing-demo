package ru.quipy.logic

import java.math.BigDecimal
import ru.quipy.api.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun AccountAggregateState.createNewAccount(id: UUID = UUID.randomUUID(), holderId: UUID): AccountCreatedEvent {
    return AccountCreatedEvent(id, holderId)
}

fun AccountAggregateState.createNewBankAccount(accountId: UUID): BankAccountCreatedEvent {
    if (bankAccounts.size >= 5)
        throw IllegalStateException("Account $accountId already has ${bankAccounts.size} bank accounts")

    return BankAccountCreatedEvent(accountId = this.getId(), bankAccountId = UUID.randomUUID())
}

fun AccountAggregateState.deleteBankAccount(BankAccountId: UUID): BankAccountDeletedEvent {
    val bankAccount = (bankAccounts[BankAccountId])
    if (bankAccount.balance != 0)
        throw IllegalStateException("You can't delete this bank account ${bankAccount.id}")
    return BankAccountDeletedEvent(
        accountId = this.getId(),
        bankAccountId = BankAccountId,
    )
}

fun AccountAggregateState.deposit(toBankAccountId: UUID, amount: BigDecimal): BankAccountDepositEvent {
    val bankAccount = (bankAccounts[toBankAccountId])   
    if (bankAccount.balance + amount > BigDecimal(10_000_000))
        throw IllegalStateException("You can't store more than 10.000.000 on account ${bankAccount.id}")

    if (bankAccounts.values.sumOf{it.balance} + amount > BigDecimal(25_000_000))
        throw IllegalStateException("You can't store more than 25.000.000 in total")

    return BankAccountDepositEvent(
        accountId = this.getId(),
        bankAccountId = toBankAccountId,
        amount = amount
    )
}

fun AccountAggregateState.withdraw(fromBankAccountId: UUID, amount: BigDecimal): BankAccountWithdrawalEvent {
    val fromBankAccount = bankAccounts[fromBankAccountId]
    if (amount > fromBankAccount.balance) 
        throw IllegalArgumentException("Cannot withdraw $amount. Not enough money: ${fromBankAccount.balance}")

    return BankAccountWithdrawalEvent(
        accountId = this.getId(),
        bankAccountId = fromBankAccountId,
        amount = amount
    )
}

fun AccountAggregateState.transferBetweenInternalAccounts(
    fromBankAccountId: UUID,
    toBankAccountId: UUID, 
    transferAmount: BigDecimal
    ): InternalAccountTransferEvent {
    val bankAccountFrom = bankAccounts[fromBankAccountId]
    if (transferAmount > bankAccountFrom.balance) 
        throw IllegalArgumentException("Cannot withdraw $transferAmount. Not enough money: ${bankAccountFrom.balance}")
    
    val bankAccountTo = (bankAccounts[toBankAccountId])
    
    if (bankAccountTo.balance + transferAmount > BigDecimal(10_000_000))
        throw IllegalStateException("You can't store more than 10.000.000 on account ${bankAccountTo.id}")
    return InternalAccountTransferEvent(
        accountId = this.getId(),
        bankAccountIdFrom = fromBankAccountId,
        bankAccountIdTo = toBankAccountId,
        amount = transferAmount
    )
}
