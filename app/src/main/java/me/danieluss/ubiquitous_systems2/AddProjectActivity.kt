package me.danieluss.ubiquitous_systems2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.room.withTransaction
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import kotlinx.android.synthetic.main.activity_add_project.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.danieluss.ubiquitous_systems2.data.dto.InventoryXml
import me.danieluss.ubiquitous_systems2.data.entities.Inventory
import me.danieluss.ubiquitous_systems2.data.entities.InventoryItem
import me.danieluss.ubiquitous_systems2.data.entities.Item
import me.danieluss.ubiquitous_systems2.data.getDbHelper
import java.util.*

class AddProjectActivity : AppCompatActivity() {

    private var mapper: XmlMapper = JacksonConfig.xmlMapper()
    private lateinit var queue: RequestQueue

    companion object {
        const val NEW_NAME = "newName"
        const val UNAVAILABLE_IN_DB = "Unavailable in database"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_project)

        queue = Volley.newRequestQueue(this)
        submit.setOnClickListener { validateAndAdd() }
    }


    private fun validateAndAdd() {
        val name: String = projectNameTextField.text.toString().trim()
        val code: String = codeTextField.text.toString().trim()

        var err = false
        if (name.isBlank() || name.isEmpty()) {
            projectNameTextField.error = "Please specify the name!"
            err = true
        }

        val inv = getDbHelper(this).database.inventoryDao().get(name)
        if (inv != null) {
            projectNameTextField.error = "This project already exists!"
            err = true
        }

        if (!code.all { it.isLetterOrDigit() || it in ",_-" }) {
            codeTextField.error = "Code should be alphanumeric!"
            err = true
        }

        if (err) {
            return
        }

        val url = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(resources.getString(R.string.legoSetUrl), null)

        Log.d("NET", "GET $url$code.xml")
        val stringRequest = StringRequest(
            Request.Method.GET, "$url$code.xml",
            Response.Listener { response ->
                insertResponseToDb(response, name)
                val intent = Intent()
                intent.putExtra(NEW_NAME, projectNameTextField.text.toString())
                setResult(IntentCodes.ADD_PROJECT.code, intent)
                finish()
            },
            Response.ErrorListener { error ->
                Log.e("NET", error.toString())
                codeTextField.error = "Lego set not found! :("
            })

        queue.add(stringRequest)
    }

    private fun insertResponseToDb(response: String, name: String) {
        val toast = Toast.makeText(applicationContext, "Saving...", Toast.LENGTH_LONG)
        toast.show()
        val inventory = mapper.readValue(response, InventoryXml::class.java)
        Log.d("DB", inventory.toString())

        val db = getDbHelper(this@AddProjectActivity).database
        val inventoryDao = db.inventoryDao()
        val inventoryItemDao = db.inventoryItemDao()
        val itemTypeDao = db.itemTypeDao()
        val itemDao = db.itemDao()
        val colorDao = db.colorDao()
        val categoryDao = db.categoryDao()

        val date = Date()
        runBlocking {
            db.withTransaction {
                inventory.items.forEach { item ->
                    launch {
                        var itemId = itemDao.getId(item.itemID)
                        val itemTypeId = itemTypeDao.getId(item.itemType)
                        if (itemId == 0L) {
                            itemDao.insert(
                                Item(
                                    0L,
                                    item.itemID,
                                    UNAVAILABLE_IN_DB,
                                    null,
                                    itemTypeId,
                                    categoryDao.getId("NO-CATEGORY")
                                )
                            )
                        }
                    }
                }
            }
        }

        runBlocking {
            db.withTransaction {
                val id = inventoryDao.insert(Inventory(0, 1, date.time, name))
                inventory.items.forEach { item ->
                    launch {
                        var itemId = itemDao.getId(item.itemID)
                        val itemTypeId = itemTypeDao.getId(item.itemType)
                        inventoryItemDao.insert(
                            InventoryItem(
                                0,
                                id,
                                itemTypeId,
                                itemId,
                                colorDao.getId(item.color),
                                item.qty,
                                0,
                                if (item.extra == "N") 0 else 1
                            )
                        )
                    }
                }
            }
        }
        toast.cancel()
    }

}
