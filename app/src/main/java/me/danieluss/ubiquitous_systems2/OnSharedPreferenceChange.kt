package me.danieluss.ubiquitous_systems2

import android.content.Context
import android.content.SharedPreferences
import me.danieluss.ubiquitous_systems2.data.DbHelper

class OnSharedPreferenceChange(private var context: Context): SharedPreferences.OnSharedPreferenceChangeListener {


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key in arrayOf(
                context.resources.getString(R.string.defaultDbPath),
                context.resources.getString(R.string.dbPath)
            )
        ) {
            DbHelper.Factory.get(context).refresh()
        }
    }

}