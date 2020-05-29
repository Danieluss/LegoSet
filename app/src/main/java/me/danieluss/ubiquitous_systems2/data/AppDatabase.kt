package me.danieluss.ubiquitous_systems2.data

import androidx.room.Database
import androidx.room.RoomDatabase
import me.danieluss.ubiquitous_systems2.data.dao.*
import me.danieluss.ubiquitous_systems2.data.entities.*

@Database(
    entities = [Code::class, Inventory::class, InventoryItem::class, ItemType::class, Item::class, Color::class, Category::class],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inventoryDao(): InventoryDao
    abstract fun inventoryItemDao(): InventoryItemDao
    abstract fun itemTypeDao(): ItemTypeDao
    abstract fun itemDao(): ItemDao
    abstract fun colorDao(): ColorDao
    abstract fun categoryDao(): CategoryDao
    abstract fun codeDao(): CodeDao
}