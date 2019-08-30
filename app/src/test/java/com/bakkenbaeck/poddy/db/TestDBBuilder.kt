package com.bakkenbaeck.poddy.db

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import db.PoddyDB

fun buildTestDB(): PoddyDB {
    val inMemorySqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
        PoddyDB.Schema.create(this)
    }

    return PoddyDB(inMemorySqlDriver)
}

