package com.openimgs.shared.data.database

import app.cash.sqldelight.db.SqlDriver
import com.openimgs.shared.database.OpenImgsDatabase

class DatabaseHelper(driverFactory: DatabaseDriverFactory) {
    val database: OpenImgsDatabase = OpenImgsDatabase(driverFactory.createDriver())

    val photoQueries get() = database.photosQueries
    val albumQueries get() = database.albumsQueries
    val duplicateQueries get() = database.duplicatesQueries
    val premiumQueries get() = database.premiumQueries
}
