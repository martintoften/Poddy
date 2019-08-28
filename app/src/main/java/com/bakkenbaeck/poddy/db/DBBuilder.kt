package com.bakkenbaeck.poddy.db

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import db.PoddyDB

const val DB_NAME = "poddy.db"

fun buildDatabase(context: Context, dbName: String = DB_NAME): PoddyDB {
    val driver = AndroidSqliteDriver(PoddyDB.Schema, context, dbName)
    return PoddyDB(driver)
}
