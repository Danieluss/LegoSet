package me.danieluss.ubiquitous_systems2.data.dao

import androidx.room.Dao
import androidx.room.Query
import me.danieluss.ubiquitous_systems2.data.Tables
import me.danieluss.ubiquitous_systems2.data.entities.*

@Dao
interface CodeDao:
    BaseDao<Code> {

    @Query("SELECT * FROM ${Tables.CODES} WHERE ItemID=:itemId AND ColorID=:colorId")
    fun get(itemId: Long, colorId: Long): Code?

    @Query("SELECT * FROM ${Tables.CODES} WHERE ItemID=:itemId AND ColorID=null")
    fun get(itemId: Long): Code?

}