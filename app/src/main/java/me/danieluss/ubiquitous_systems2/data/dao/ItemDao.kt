package me.danieluss.ubiquitous_systems2.data.dao

import androidx.room.Dao
import androidx.room.Query
import me.danieluss.ubiquitous_systems2.data.Tables
import me.danieluss.ubiquitous_systems2.data.entities.Item
import me.danieluss.ubiquitous_systems2.data.entities.ItemType

@Dao
interface ItemDao:
    BaseDao<Item>{

    @Query("SELECT id FROM ${Tables.PARTS} WHERE code=:code")
    fun getId(code: String): Long

    @Query("SELECT * FROM ${Tables.PARTS} WHERE id=:id")
    fun get(id: Long): Item
}