package me.danieluss.ubiquitous_systems2.data.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class ItemOut(
    @JsonProperty("ITEMTYPE")
    var itemType: String,
    @JsonProperty("ITEMID")
    var itemId: String,
    @JsonProperty("COLOR")
    var color: Long?,
    @JsonProperty("QTY")
    var qty: Int,
    @JsonProperty("CONDITION")
    var condition: String?
)