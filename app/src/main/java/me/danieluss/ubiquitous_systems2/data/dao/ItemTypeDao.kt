package me.danieluss.ubiquitous_systems2.data.dao

import androidx.room.Dao
import androidx.room.Query
import me.danieluss.ubiquitous_systems2.data.Tables
import me.danieluss.ubiquitous_systems2.data.entities.Category
import me.danieluss.ubiquitous_systems2.data.entities.Inventory
import me.danieluss.ubiquitous_systems2.data.entities.ItemType

@Dao
interface ItemTypeDao:
    BaseDao<ItemType> {

    @Query("SELECT id FROM ${Tables.ITEM_TYPES} WHERE code=:code")
    fun getId(code: String): Long


    @Query("SELECT * FROM ${Tables.ITEM_TYPES} WHERE id=:id")
    fun get(id: Long): ItemType

}