package me.danieluss.ubiquitous_systems2

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.room.withTransaction
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.danieluss.ubiquitous_systems2.data.dto.ItemDetails
import me.danieluss.ubiquitous_systems2.data.entities.InventoryItem
import me.danieluss.ubiquitous_systems2.data.getDbHelper
import java.io.ByteArrayInputStream


class DetailAdapter(context: Activity, var inventoryDetails: MutableList<ItemDetails>) :
    ArrayAdapter<String>(
        context,
        R.layout.details_list_layout,
        inventoryDetails.map { item -> item.item.name }
    ) {

    init {
        sort()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        var rowView = view
        if (rowView == null)
            rowView = inflater.inflate(R.layout.details_list_layout, null, true)!!

        val titleText = rowView.findViewById<View>(R.id.title) as TextView
        val subDescText =
            rowView.findViewById<View>(R.id.subdesc) as TextView
        val descText =
            rowView.findViewById<View>(R.id.desc) as TextView
        val qtyText =
            rowView.findViewById<View>(R.id.qty) as TextView
        val imageView: ImageView = rowView.findViewById<View>(R.id.icon) as ImageView
        val plus =
            rowView.findViewById<View>(R.id.plus) as Button
        val minus =
            rowView.findViewById<View>(R.id.minus) as Button
        val layout =
            rowView.findViewById<View>(R.id.constraintLayout) as ConstraintLayout

        var item = inventoryDetails[position]
        titleText.text = item.item.name
        descText.text = "${item.category.name} [${item.item.code}]"
        subDescText.text = "${item.type.name} ${item.color?.name ?: "NONE"} [${item.codes?.code ?: "NONE"}]"
        qtyText.text = "${item.invItem.quantityInStore}/${item.invItem.quantityInSet}"
        val img = item.codes?.image

        plus.setOnClickListener {
            inc(item, 1)
        }

        minus.setOnClickListener {
            inc(item, -1)
        }

        if (img == null) {
            imageView.setImageResource(R.drawable.ic_not_interested_black_96dp)
        } else {
            setImage(img, imageView)
        }

        val w = item.invItem.quantityInStore.toFloat() / item.invItem.quantityInSet.toFloat()
        val color =
            if (w > 0.5)
                Utils.interpolateColor(
                    Color.parseColor("#F5F5F5"),
                    Color.parseColor("#DDF2C7"),
                    (w - 0.5f) / 0.5f
                )
            else
                Utils.interpolateColor(
                    Color.parseColor("#F2D2C7"),
                    Color.parseColor("#F5F5F5"),
                    (w) / 0.5f
                )
        layout.setBackgroundColor(
            color
        )

        return rowView
    }

    private fun setImage(byteArr: ByteArray, imageView: ImageView) {
        val input = ByteArrayInputStream(byteArr)
        val bitmap = BitmapFactory.decodeStream(input)
        imageView.setImageBitmap(bitmap)
    }

    private fun updateInv(item: InventoryItem) {
        var db = getDbHelper(context).database
        GlobalScope.launch {
            db.withTransaction {
                db.inventoryItemDao().update(item)
            }
        }
    }

    private fun inc(item: ItemDetails, amount: Int) {
        if (item.invItem.quantityInStore + amount <= item.invItem.quantityInSet
            && item.invItem.quantityInStore + amount >= 0
        )
            item.invItem.quantityInStore += amount
//        Don't sort each time we click something...
//        sort()
        notifyDataSetChanged()
        updateInv(item.invItem)
    }

    private fun sort() {
        inventoryDetails.sortWith(compareBy<ItemDetails> { x ->
            if (x.invItem.quantityInStore / x.invItem.quantityInSet == 1)
                1
            else
                -1
        }.thenComparator { x, y ->
            if (x.item.name == AddProjectActivity.UNAVAILABLE_IN_DB && y.item.name == AddProjectActivity.UNAVAILABLE_IN_DB) {
                return@thenComparator 0
            }
            if (x.item.name == AddProjectActivity.UNAVAILABLE_IN_DB) {
                return@thenComparator 1
            }
            if (y.item.name == AddProjectActivity.UNAVAILABLE_IN_DB) {
                return@thenComparator -1
            }
            val xInv = x.invItem
            val yInv = y.invItem
            yInv.lackingParts() - xInv.lackingParts()
        }.thenComparator { x, y ->
            if (x.item.name == AddProjectActivity.UNAVAILABLE_IN_DB && y.item.name == AddProjectActivity.UNAVAILABLE_IN_DB) {
                return@thenComparator 0
            }
            if (x.item.name == AddProjectActivity.UNAVAILABLE_IN_DB) {
                return@thenComparator 1
            }
            if (y.item.name == AddProjectActivity.UNAVAILABLE_IN_DB) {
                return@thenComparator -1
            }
            x.item.name.compareTo(y.item.name)
        })
    }

}