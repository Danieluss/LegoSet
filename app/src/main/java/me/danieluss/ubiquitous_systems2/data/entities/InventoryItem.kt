package me.danieluss.ubiquitous_systems2.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "InventoriesParts")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "InventoryID") var inventoryID: Long,
    @ColumnInfo(name = "TypeID") var typeID: Long,
    @ColumnInfo(name = "ItemID") var itemID: Long,
    @ColumnInfo(name = "ColorID") var colorID: Long,
    @ColumnInfo(name = "QuantityInSet") var quantityInSet: Int,
    @ColumnInfo(name = "QuantityInStore") var quantityInStore: Int,
    @ColumnInfo(name = "Extra") var extra: Int
) {
    fun lackingParts(): Int {
        return (quantityInSet - quantityInStore)
    }
}