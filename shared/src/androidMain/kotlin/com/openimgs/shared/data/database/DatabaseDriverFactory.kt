package com.openimgs.shared.data.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.openimgs.shared.database.OpenImgsDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(OpenImgsDatabase.Schema, context, "openimgs.db")
    }
}
