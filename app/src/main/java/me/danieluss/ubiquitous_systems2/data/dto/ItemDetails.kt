package me.danieluss.ubiquitous_systems2.data.dto

import me.danieluss.ubiquitous_systems2.data.entities.*

data class ItemDetails(
    var item: Item,
    var invItem: InventoryItem,
    var codes: Code?,
    var color: Color?,
    var category: Category,
    var type: ItemType
)