package com.openimgs.shared.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.openimgs.shared.database.OpenImgsDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(OpenImgsDatabase.Schema, "openimgs.db")
    }
}
