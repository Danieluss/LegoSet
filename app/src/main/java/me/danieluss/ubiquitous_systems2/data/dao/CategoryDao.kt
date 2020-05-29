package me.danieluss.ubiquitous_systems2.data.dao

import androidx.room.Dao
import androidx.room.Query
import me.danieluss.ubiquitous_systems2.data.Tables
import me.danieluss.ubiquitous_systems2.data.entities.Category
import me.danieluss.ubiquitous_systems2.data.entities.Inventory
import me.danieluss.ubiquitous_systems2.data.entities.Item
import me.danieluss.ubiquitous_systems2.data.entities.ItemType

@Dao
interface CategoryDao:
    BaseDao<Category> {

    @Query("SELECT id FROM ${Tables.CATEGORIES} WHERE name=:name")
    fun getId(name: String): Long

    @Query("SELECT * FROM ${Tables.CATEGORIES} WHERE id=:id")
    fun get(id: Long): Category
}