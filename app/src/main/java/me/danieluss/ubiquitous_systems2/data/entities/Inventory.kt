package me.danieluss.ubiquitous_systems2.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Inventories")
data class Inventory(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "Active") var active: Long,
    @ColumnInfo(name = "LastAccessed") var lastAccessed: Long,
    @ColumnInfo(name = "Name") var name: String
)