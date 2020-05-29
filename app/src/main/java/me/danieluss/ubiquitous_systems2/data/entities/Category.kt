package me.danieluss.ubiquitous_systems2.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Categories")
data class Category(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "Code") var code: Long,
    @ColumnInfo(name = "Name") var name: String,
    @ColumnInfo(name = "NamePL") var namePl: String?
)