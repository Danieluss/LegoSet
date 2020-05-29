package me.danieluss.ubiquitous_systems2.data

import android.content.Context
import android.content.ContextWrapper
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.util.Log
import java.io.File


class DbContext(context: Context, var path: String) : ContextWrapper(context) {
    override fun getDatabasePath(name: String): File {
        Log.d("DB","Accessing db path: $path for name: $name exists: ${File(path).exists()}")
        return File(path)
    }

    override fun openOrCreateDatabase(
        name: String,
        mode: Int,
        factory: CursorFactory,
        errorHandler: DatabaseErrorHandler?
    ): SQLiteDatabase {
        Log.d("DB","Trying to create database")
        val result: SQLiteDatabase = openOrCreateDatabase(name, mode, factory)
        Log.d("DB","Created database")
        if (Log.isLoggable(DEBUG_CONTEXT, Log.WARN)) {
            Log.w(
                DEBUG_CONTEXT,
                "openOrCreateDatabase(" + name + ",,) = " + result.path
            )
        }
        return result
    }

    companion object {
        private const val DEBUG_CONTEXT = "DatabaseContext"
    }
}