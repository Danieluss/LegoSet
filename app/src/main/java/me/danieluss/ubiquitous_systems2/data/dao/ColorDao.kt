package me.danieluss.ubiquitous_systems2.data.dao

import androidx.room.Dao
import androidx.room.Query
import me.danieluss.ubiquitous_systems2.data.Tables
import me.danieluss.ubiquitous_systems2.data.entities.Color
import me.danieluss.ubiquitous_systems2.data.entities.Inventory
import me.danieluss.ubiquitous_systems2.data.entities.Item
import me.danieluss.ubiquitous_systems2.data.entities.ItemType

@Dao
interface ColorDao:
    BaseDao<Color> {

    @Query("SELECT id FROM ${Tables.COLORS} WHERE code=:code")
    fun getId(code: Long): Long

    @Query("SELECT * FROM ${Tables.COLORS} WHERE id=:id")
    fun get(id: Long): Color?


}