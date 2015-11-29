package com.abdodaoud.merlin.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.abdodaoud.merlin.ui.App
import com.abdodaoud.merlin.util.Constants
import org.jetbrains.anko.db.*

class FactDbHelper(ctx: Context = App.instance) : ManagedSQLiteOpenHelper(ctx,
        FactDbHelper.DB_NAME, null, FactDbHelper.DB_VERSION) {

    companion object {
        val DB_NAME = Constants.DATABASE_FILE_NAME
        val DB_VERSION = 2
        val instance: FactDbHelper by lazy { FactDbHelper() }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(MainFactsTable.NAME, true,
                MainFactsTable.ID to INTEGER + PRIMARY_KEY,
                MainFactsTable.NAME to TEXT)

        db.createTable(DayFactTable.NAME, true,
                DayFactTable.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                DayFactTable.DATE to INTEGER,
                DayFactTable.TITLE to TEXT,
                DayFactTable.FACTS_ID to TEXT,
                DayFactTable.URL to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(MainFactsTable.NAME, true)
        db.dropTable(DayFactTable.NAME, true)
        onCreate(db)
    }
}