package com.taylor.budgister

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.taylor.budgister.db.BudgisterDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(BudgisterDatabase.Schema, "budgister.db")
    }
}