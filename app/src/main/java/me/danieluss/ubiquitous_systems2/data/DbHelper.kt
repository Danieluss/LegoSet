package me.danieluss.ubiquitous_systems2.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.room.Room
import me.danieluss.ubiquitous_systems2.R
import java.io.File
import java.io.FileOutputStream

fun getDbHelper(context: Context? = null): DbHelper {
    return DbHelper.Factory.get(context).get()
}

class DbHelper private constructor(path: String, context: Context) {
//    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    var database: AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, path)
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    companion object {
        const val ASSETS_PATH = "databases"
        const val DATABASE_NAME = "bricks"
        const val DATABASE_VERSION = 1

        fun getDefaultDbPath(context: Context): String {
            return context.getDatabasePath(DATABASE_NAME).absolutePath + ".sqlite3"
        }
    }

    class Factory private constructor(context: Context) {

        companion object {
            private var instance: Factory? = null

            //            lock on first context, atp it doesnt matter
            @Synchronized
            fun get(context: Context? = null): Factory {
                if (context == null) {
                    return instance!!
                } else if (instance == null) {
                    instance = Factory(context)
                }
                return instance!!
            }

        }

        private var newPath = false
        private var cached: DbHelper? = null
        private var mainContext: Context = context

        private fun dbPath(): String {
            val path: String
            val preferences: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(mainContext)
            path =
                if (preferences.getBoolean(
                        mainContext.resources.getString(R.string.defaultDbPath),
                        false
                    )
                )
                    getDefaultDbPath(mainContext)
                else
                    preferences.getString(
                        mainContext.resources.getString(R.string.dbPath),
                        getDefaultDbPath(mainContext)
                    )!!
            return path
        }

        private fun shouldInstall(): Boolean {
            Log.d("DbHelper", "${dbPath()} ${!File(dbPath()).exists()}")
            return !File(dbPath()).exists()
        }

        private fun installDb() {
            val inputStream = mainContext.assets.open("$DATABASE_NAME.sqlite3")

            try {
                val outputFile = File(dbPath())
                outputFile.parentFile!!.mkdirs()
                outputFile.createNewFile()
                val outputStream = FileOutputStream(outputFile)

                inputStream.copyTo(outputStream)
                inputStream.close()

                outputStream.flush()
                outputStream.close()
            } catch (exception: Throwable) {
                throw RuntimeException(
                    "The $DATABASE_NAME database couldn't be installed.",
                    exception
                )
            }
        }

        private fun checkInstall() {
            if (shouldInstall()) {
                File(dbPath()).delete()
                installDb()
            }
        }

        @Synchronized
        fun get(): DbHelper {
            checkInstall()
            if (newPath || cached == null || !cached!!.database.isOpen) {
                cached = DbHelper(
                    dbPath(),
                    DbContext(mainContext, dbPath())
                )
                newPath = false
            }
            return cached!!
        }

        @Synchronized
        fun refresh() {
            newPath = true
        }
    }

}