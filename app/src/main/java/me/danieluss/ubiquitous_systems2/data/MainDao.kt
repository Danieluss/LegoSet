package me.danieluss.ubiquitous_systems2.data

import android.database.Cursor
import android.database.CursorWrapper
import android.database.sqlite.SQLiteDatabase

class MainDao(private var database: SQLiteDatabase) {

    fun fetch(tableName: String): CursorWrapper {
        val cursor: Cursor =
            database.query(tableName, arrayOf("*"), null, null, null, null, null)
        cursor.moveToFirst()
        val cursorWrapper = CursorWrapper(cursor)
        return cursorWrapper
    }

}