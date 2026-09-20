package com.taylor.budgister

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.taylor.budgister.db.BudgisterDatabase
import com.taylor.budgister.db.ListEntity
import com.taylor.budgister.db.ListItemEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class ListsRepository(driverFactory: DatabaseDriverFactory) {
    private val database = BudgisterDatabase(driverFactory.createDriver())
    private val queries = database.listsQueries
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default

    fun getAllLists(): Flow<List<ListEntity>> {
        return queries.selectAllLists().asFlow().mapToList(dispatcher)
    }

    fun getItemsForList(listId: Long): Flow<List<ListItemEntity>> {
        return queries.selectItemsForList(listId).asFlow().mapToList(dispatcher)
    }

    fun createList(title: String) {
        queries.insertList(title, System.currentTimeMillis())
    }

    fun deleteList(listId: Long) {
        queries.deleteList(listId)
    }

    fun addItem(listId: Long, content: String, position: Int) {
        queries.insertListItem(listId, content, 0, position.toLong())
    }

    fun toggleItemChecked(itemId: Long, isChecked: Boolean) {
        queries.updateItemChecked(if (isChecked) 1 else 0, itemId)
    }

    fun deleteItem(itemId: Long) {
        queries.deleteListItem(itemId)
    }
}