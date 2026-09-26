package com.taylor.budgister

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.taylor.budgister.db.BudgisterDatabase
import com.taylor.budgister.db.NoteEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class NotesRepository(driverFactory: DatabaseDriverFactory) {
    private val database = BudgisterDatabase(driverFactory.createDriver())
    private val queries = database.notesQueries
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default

    fun getAllNotes(): Flow<List<NoteEntity>> {
        return queries.selectAllNotes().asFlow().mapToList(dispatcher)
    }

    fun getNoteById(id: Long): Flow<NoteEntity?> {
        return queries.selectNoteById(id).asFlow().mapToOneOrNull(dispatcher)
    }

    fun createNote(title: String, body: String): Long {
        val now = System.currentTimeMillis()
        queries.insertNote(title, body, now, now)
        return queries.transactionWithResult {
            queries.selectAllNotes().executeAsList().first().id
        }
    }

    fun updateNote(id: Long, title: String, body: String) {
        queries.updateNote(title, body, System.currentTimeMillis(), id)
    }

    fun deleteNote(id: Long) {
        queries.deleteNote(id)
    }
}