package me.danieluss.ubiquitous_systems2

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.CATEGORY_OPENABLE
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.withTransaction
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.danieluss.ubiquitous_systems2.AddProjectActivity.Companion.UNAVAILABLE_IN_DB
import me.danieluss.ubiquitous_systems2.Utils.asyncMap
import me.danieluss.ubiquitous_systems2.data.dto.InventoryOut
import me.danieluss.ubiquitous_systems2.data.dto.ItemDetails
import me.danieluss.ubiquitous_systems2.data.dto.ItemOut
import me.danieluss.ubiquitous_systems2.data.entities.Code
import me.danieluss.ubiquitous_systems2.data.getDbHelper
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.PrintWriter


class DetailsActivity : AppCompatActivity() {

    private lateinit var queue: RequestQueue
    private lateinit var adapter: DetailAdapter
    private lateinit var projectName: String
    private lateinit var mutInventoryDetails: MutableList<ItemDetails>

    companion object {
        const val PROJECT = "project"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val toast = Toast.makeText(applicationContext, "Loading...", Toast.LENGTH_LONG)
        toast.show()

        queue = Volley.newRequestQueue(this)

        val projectName = intent.extras!!.getString(PROJECT)!!

        val db = getDbHelper(this).database
        val inventory = db.inventoryItemDao().get(db.inventoryDao().getId(projectName)!!)!!
        var inventoryDetails: List<ItemDetails>? = null
        runBlocking {
            inventoryDetails = inventory.asyncMap { inventoryItem ->
                val item = db.itemDao().get(inventoryItem.itemID)
                val color = db.colorDao().get(inventoryItem.colorID)
                val category = db.categoryDao().get(item.categoryID)
                val itemType = db.itemTypeDao().get(inventoryItem.typeID)
                var codes: Code? = null
                if (color?.id != null) {
                    codes = db.codeDao().get(inventoryItem.itemID, color.id)
                }
                if (codes == null) {
                    codes = db.codeDao().get(inventoryItem.itemID)
                }
                ItemDetails(
                    item,
                    inventoryItem,
                    codes,
                    color,
                    category,
                    itemType
                )
            }
        }
        mutInventoryDetails = mutableListOf<ItemDetails>()
        mutInventoryDetails.addAll(inventoryDetails!!)
        adapter = DetailAdapter(this, mutInventoryDetails)
        list!!.adapter = adapter
        mutInventoryDetails.forEach { it ->
            if (it.codes?.image == null && it.item.name != UNAVAILABLE_IN_DB)
                getImage(it)
        }
        toast.cancel()

        this.projectName = projectName

        archive.setOnClickListener {
            var condition: Boolean = false
            runBlocking {
                db.withTransaction {
                    var inv = db.inventoryDao().get(projectName)!!
                    inv.active = 1 - inv.active
                    condition = inv.active == 1L
                    db.inventoryDao().update(inv)
                }
                Toast.makeText(
                    applicationContext,
                    if (condition) "Project unarchived" else "Project archived",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        email.setOnClickListener {
            sendEmail()
        }

        save.setOnClickListener {
            save()
        }

        searchFilter.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

        })

    }

    private fun inventoryToString(condition: Boolean): String {
        val xmlMapper = JacksonConfig.xmlMapper()

        return xmlMapper.writeValueAsString(InventoryOut(
            mutInventoryDetails.map { it ->
                ItemOut(
                    it.type.code,
                    it.item.code,
                    it.color?.code,
                    it.invItem.lackingParts(),
                    if (condition) "U" else "N"
                )
            }.filter { it ->
                it.qty != 0
            }
        ))
    }

    private fun getImage(item: ItemDetails) {
        val urls = mutableListOf<String>()
        if (item.codes?.code != null) {
            urls.add("https://www.lego.com/service/bricks/5/2/${item.codes?.code}")
        }
        if (item.color?.code != null) {
            urls.add(
                "http://img.bricklink.com/P/${item.color?.code}/${item.item.code}.gif"
            )
        }
        urls.add(
            "https://www.bricklink.com/PL/${item.item.code}.jpg"
        )

        tryDownload(item, 0, urls)
    }

    private fun tryDownload(item: ItemDetails, pos: Int, urls: List<String>) {
        if (pos >= urls.size) {
            return
        }
        val url = urls[pos]
        val request = ImageRequest(url,
            Response.Listener { bitmap ->
                Log.d("NET", "$pos $urls")
                Log.d("NET", "${item.item.code} got image from: $url")
                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
                val bArr: ByteArray = bos.toByteArray()
                val db = getDbHelper(this).database
                GlobalScope.launch {
                    db.withTransaction {
                        if (item.codes != null) {
                            item.codes!!.image = bArr
                            db.codeDao().update(item.codes!!)
                            Log.d("DB", "Update ${item.item.id}")
                        } else {
                            item.codes = Code(
                                0,
                                item.item.id,
                                item.color?.id,
                                null,
                                bArr
                            )
                            db.codeDao().insert(item.codes!!)
                            Log.d("DB", "Insert ${item.item.id}")
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }, 0, 0, null, null,
            Response.ErrorListener {
                Log.d("NET", "${item.item.name} couldn't get image from: $url")
                tryDownload(item, pos + 1, urls)
            })

        queue.add(request)
    }

    private fun sendEmail() {
        afterDialog { condition ->
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "vnd.android.cursor.dir/email"
            val to = arrayOf("recipient@example.com")
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[Block List] $projectName.xml")
            emailIntent.putExtra(Intent.EXTRA_TEXT, inventoryToString(condition))

            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }
    }

    private fun save() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.type = "*/*"
        intent.addCategory(CATEGORY_OPENABLE)
        intent.type = "text/xml";

        try {
            startActivityForResult(
                Intent.createChooser(intent, "Select a File to Save"),
                IntentCodes.FILE_WRITE_CODE.code
            )
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                this, "Please install a File Manager.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun afterDialog(callback: (condition: Boolean) -> Unit) {

        this.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Optionally use second-hand parts")
            builder.apply {
                setPositiveButton("yes",
                    DialogInterface.OnClickListener { _, _ ->
                        callback(true)
                    })
                setNegativeButton("no",
                    DialogInterface.OnClickListener { _, _ ->
                        callback(false)
                    })
            }
            builder
                .create()
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IntentCodes.FILE_WRITE_CODE.code) {
            val writer = PrintWriter(contentResolver.openOutputStream(data!!.data!!)!!)
            afterDialog { condition ->
                writer.use {
                    writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                    writer.print(inventoryToString(condition))
                    Toast.makeText(this, "Saved file", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}