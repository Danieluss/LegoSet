package me.danieluss.ubiquitous_systems2.data.dao

import androidx.room.Dao
import androidx.room.Query
import me.danieluss.ubiquitous_systems2.data.Tables
import me.danieluss.ubiquitous_systems2.data.entities.Inventory

@Dao
interface InventoryDao:
    BaseDao<Inventory> {

    @Query("SELECT * FROM ${Tables.INVENTORIES}")
    fun getAll(): List<Inventory>

    @Query("SELECT * FROM ${Tables.INVENTORIES} WHERE name=:name")
    fun get(name: String): Inventory?

    @Query("SELECT id FROM ${Tables.INVENTORIES} WHERE name=:name")
    fun getId(name: String): Long?

    @Query("SELECT * FROM ${Tables.INVENTORIES} WHERE active=1")
    fun getActive(): List<Inventory>

}