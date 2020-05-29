package me.danieluss.ubiquitous_systems2

import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import java.io.File


class SettingsActivity : AppCompatActivity() {

    companion object {
        val empty: String = "__EMPTY__"
    }

    private lateinit var dbPathString: String
    private lateinit var legoSetUrlString: String
    private lateinit var appNameString: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var defaultDbPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        dbPathString = resources.getString(R.string.dbPath)
        legoSetUrlString = resources.getString(R.string.legoSetUrl)
        appNameString = resources.getString(R.string.appName)
        defaultDbPath = {
            arrayOf(
                getExternalFilesDir(null).toString(),
                "data",
                appNameString
            )
                .joinToString(File.separator)
        }()

        initGUI()
        initPreferences()
    }

    private fun initGUI() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        var dbPath =
            sharedPreferences.getString(dbPathString, defaultDbPath)!!
        sharedPreferences.edit().putString(dbPathString, dbPath).apply()

        var legoUrl = sharedPreferences.getString(
            legoSetUrlString,
            "http://fcds.cs.put.poznan.pl/MyWeb/BL/"
        )
//        https://androidpedia.net/en/tutorial/150/storing-files-in-internal---external-storage
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val dbPathString = resources.getString(R.string.dbPath)
            val legoSetUrlString = resources.getString(R.string.legoSetUrl)

            for (string in arrayOf(dbPathString, legoSetUrlString)) {
                setPreferenceListener(string, OnPreferenceChange())
            }
        }

        private fun readPref(pref: String): String{
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            return sharedPreferences.getString(pref, empty)!!
        }

        private fun setPreferenceListener(
            pref: String,
            listener: Preference.OnPreferenceChangeListener
        ) {
            val preference: Preference? = findPreference(pref) as Preference?
            preference!!.onPreferenceChangeListener = listener
            preference.summary = readPref(pref)
        }

    }
}