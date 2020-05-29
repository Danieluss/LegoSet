package me.danieluss.ubiquitous_systems2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.danieluss.ubiquitous_systems2.AddProjectActivity.Companion.NEW_NAME
import me.danieluss.ubiquitous_systems2.data.DbHelper
import me.danieluss.ubiquitous_systems2.data.getDbHelper


class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var onSharedPreferenceChange = OnSharedPreferenceChange(this)
    val projects = mutableListOf<String>()
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSharedPreferences()

        initDb()

        initListData()

        initGUI()

        val db = getDbHelper(this).database
        val itemTypeDao = db.itemTypeDao()
        GlobalScope.launch {
            var long = itemTypeDao.getId("M")
            Log.d("DB", long.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChange)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.action_settings) {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initDb() {
        runBlocking {
            coroutineScope {
                Log.d(
                    "DB",
                    getDbHelper(this@MainActivity).database.inventoryDao().getAll().toString()
                )
            }
        }
    }

    private fun initGUI() {
        fab!!.setOnClickListener { showAddProjectActivity() }
        projectsList!!.setOnItemClickListener { _, _, _, id ->
            Log.d("X", "Show: " + projects[id.toInt()])
            showDetailsActivity(projects[id.toInt()])
        }
    }

    private fun initListData() {
        var db = getDbHelper(this).database
        runBlocking {
            coroutineScope {
                val inventories =
                    if (sharedPreferences.getBoolean(
                            resources.getString(R.string.showArchive),
                            false
                        )
                    ) {
                        db.inventoryDao().getAll()
                    } else {
                        db.inventoryDao().getActive()
                    }
                val dbProjects = inventories.map { inv -> inv.name }
                projects.addAll(dbProjects)
                adapter = ArrayAdapter<String>(
                    this@MainActivity,
                    android.R.layout.simple_list_item_1,
                    projects
                )
                val dataView: ListView = projectsList
                dataView.adapter = adapter
            }
        }
    }

    private fun initSharedPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChange)
        val dbPath = sharedPreferences.getString(
            resources.getString(R.string.dbPath),
            resources.getString(R.string._empty)
        )
        if (dbPath == resources.getString(R.string._empty)) {
            val res = sharedPreferences.edit().putString(
                resources.getString(R.string.dbPath),
                DbHelper.getDefaultDbPath(this)
            ).commit()
        }
    }

    private fun showDetailsActivity(project: String) {
        val intent = Intent(this@MainActivity, DetailsActivity::class.java)
        val b = Bundle()
        b.putString(DetailsActivity.PROJECT, project)
        intent.putExtras(b)
        val projectId = getDbHelper(this).database.inventoryDao().getId(project)
        if (projectId != null && projectId != 0L) {
            startActivity(intent)
        } else {
            val toast = Toast.makeText(applicationContext, "Project file not saved yet...", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    private fun showAddProjectActivity() {
        val intent = Intent(this@MainActivity, AddProjectActivity::class.java)
        startActivityForResult(intent, IntentCodes.ADD_PROJECT.code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IntentCodes.ADD_PROJECT.code && data != null) {
            val newName = data.getStringExtra(NEW_NAME)
            if (newName != null) {
                projects.add(newName)
            }
            val toast = Toast.makeText(applicationContext, "Project added!", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

}