package com.taylor.budgister

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.taylor.budgister.db.BudgetCategoryEntity
import com.taylor.budgister.db.BudgisterDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class BudgetPeriod {
    WEEKLY, BI_MONTHLY, MONTHLY
}

class BudgetRepository(driverFactory: DatabaseDriverFactory) {
    private val database = BudgisterDatabase(driverFactory.createDriver())
    private val queries = database.budgetQueries
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default

    fun getCategoriesForSection(period: BudgetPeriod, sectionIndex: Int): Flow<List<BudgetCategoryEntity>> {
        return queries.selectCategoriesForSection(period.name, sectionIndex.toLong())
            .asFlow().mapToList(dispatcher)
    }

    fun addCategory(period: BudgetPeriod, sectionIndex: Int, name: String, amount: Double, position: Int) {
        queries.insertCategory(period.name, sectionIndex.toLong(), name, amount, position.toLong())
    }

    fun updateCategory(id: Long, name: String, amount: Double) {
        queries.updateCategory(name, amount, id)
    }

    fun deleteCategory(id: Long) {
        queries.deleteCategory(id)
    }

    fun getLabel(period: BudgetPeriod, sectionIndex: Int): Flow<String> {
        return queries.selectLabel(period.name, sectionIndex.toLong())
            .asFlow().mapToOneOrNull(dispatcher)
            .map { it?.label ?: "" }
    }

    fun setLabel(period: BudgetPeriod, sectionIndex: Int, label: String) {
        queries.upsertLabel(period.name, sectionIndex.toLong(), label)
    }

    fun getPeriod(): Flow<BudgetPeriod> {
        return queries.selectSettings().asFlow().mapToOneOrNull(dispatcher).map { settings ->
            settings?.period?.let { BudgetPeriod.valueOf(it) } ?: BudgetPeriod.MONTHLY
        }
    }

    fun setPeriod(period: BudgetPeriod) {
        queries.upsertSettings(period.name)
    }
}