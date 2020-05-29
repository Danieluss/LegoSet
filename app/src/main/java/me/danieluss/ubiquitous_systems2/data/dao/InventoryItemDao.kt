package me.danieluss.ubiquitous_systems2.data.dao

import androidx.room.Dao
import androidx.room.Query
import me.danieluss.ubiquitous_systems2.data.Tables
import me.danieluss.ubiquitous_systems2.data.entities.Inventory
import me.danieluss.ubiquitous_systems2.data.entities.InventoryItem

@Dao
interface InventoryItemDao:
    BaseDao<InventoryItem> {

    @Query("SELECT * FROM ${Tables.INVENTORIES_PARTS} WHERE inventoryID=:inventory")
    fun get(inventory: Long): List<InventoryItem>?

}