package com.taylor.budgister

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.taylor.budgister.db.BudgisterDatabase
import com.taylor.budgister.db.TransactionEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RegisterRepository(driverFactory: DatabaseDriverFactory) {
    private val database = BudgisterDatabase(driverFactory.createDriver())
    private val queries = database.registerQueries
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default

    fun getTransactions(): Flow<List<TransactionEntity>> {
        return queries.selectAllTransactions().asFlow().mapToList(dispatcher)
    }

    fun addTransaction(date: String, payee: String, category: String, amount: Double) {
        queries.insertTransaction(date, payee, category, amount, 0L)
    }

    fun updateTransaction(id: Long, date: String, payee: String, category: String, amount: Double) {
        queries.updateTransaction(date, payee, category, amount, id)
    }

    fun hasStartingBalance(): Flow<Boolean> {
        return queries.selectSettings().asFlow().mapToOneOrNull(dispatcher)
            .map { it != null }
    }

    fun setCleared(id: Long, cleared: Boolean) {
        queries.updateCleared(if (cleared) 1L else 0L, id)
    }

    fun deleteTransaction(id: Long) {
        queries.deleteTransaction(id)
    }

    fun getStartingBalance(): Flow<Double> {
        return queries.selectSettings().asFlow().mapToOneOrNull(dispatcher)
            .map { it?.startingBalance ?: 0.0 }
    }

    fun setStartingBalance(value: Double) {
        queries.upsertSettings(value)
    }
}