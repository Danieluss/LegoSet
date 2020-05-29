package me.danieluss.ubiquitous_systems2.data.dto

data class ItemXml(
    var itemType: String,
    var itemID: String,
    var qty: Int,
    var color: Long,
    var extra: String,
    var alternate: String,
    var matchId: Int,
    var counterPart: String
)