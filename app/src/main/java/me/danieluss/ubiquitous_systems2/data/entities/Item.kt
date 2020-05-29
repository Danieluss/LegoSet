package me.danieluss.ubiquitous_systems2.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Parts")
data class Item(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "Code") var code: String,
    @ColumnInfo(name = "Name") var name: String,
    @ColumnInfo(name = "NamePL") var namePl: String?,
    @ColumnInfo(name = "TypeID") var typeID: Long,
    @ColumnInfo(name = "CategoryID") var categoryID: Long
)